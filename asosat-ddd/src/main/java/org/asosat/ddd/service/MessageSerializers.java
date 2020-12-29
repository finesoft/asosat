/*
 * Copyright (c) 2013-2018, Bingo.Chen (finesoft@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.asosat.ddd.service;

import static org.asosat.ddd.security.SecurityContextHolder.currentOrg;
import static org.asosat.ddd.security.SecurityContextHolder.currentUser;
import static org.corant.shared.util.Assertions.shouldBeTrue;
import static org.corant.shared.util.Streams.copy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.BytesMessage;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatRuntimeException;
import javax.jms.TextMessage;
import org.asosat.ddd.security.SecurityContextHolder;
import org.asosat.shared.Participator;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.shared.util.Resources.InputStreamResource;
import org.corant.suites.bundle.GlobalMessageCodes;
import org.corant.suites.bundle.exception.GeneralRuntimeException;
import org.corant.suites.jms.shared.annotation.MessageSend.SerializationSchema;
import org.corant.suites.jms.shared.annotation.MessageSerialization;
import org.corant.suites.jms.shared.context.MessageSerializer;
import org.corant.suites.json.JsonUtils;

/**
 * corant-suites-ddd
 * @author bingo 下午3:43:14
 */
public class MessageSerializers {

  static final ObjectMapper jsonObjectMapper = JsonUtils.copyMapper();

  @ApplicationScoped
  @MessageSerialization(schema = SerializationSchema.BINARY)
  public static class BinaryMessageSerializer implements MessageSerializer {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(Message message, Class<T> clazz) {
      shouldBeTrue(message instanceof BytesMessage);
      shouldBeTrue(InputStreamResource.class.isAssignableFrom(clazz));
      BytesMessage bmsg = (BytesMessage) message;
      try {
        byte[] data = new byte[(int) bmsg.getBodyLength()];
        bmsg.readBytes(data);
        return (T) new InputStreamResource(new ByteArrayInputStream(data), null);
      } catch (JMSException e) {
        throw new CorantRuntimeException(e);
      }
    }

    @Override
    public Message serialize(JMSContext jmsContext, Serializable object) {
      shouldBeTrue(object instanceof InputStream);
      BytesMessage message = jmsContext.createBytesMessage();
      try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
        copy((InputStream) object, buffer);
        byte[] bytes = buffer.toByteArray();
        message.writeBytes(bytes);
        resolveSchemaProperty(message, SerializationSchema.BINARY);
        return message;
      } catch (JMSException | IOException e) {
        throw new MessageFormatRuntimeException(e.getMessage());
      }
    }
  }

  @ApplicationScoped
  @MessageSerialization(schema = SerializationSchema.JSON_STRING)
  public static class JsonMessageSerializer implements MessageSerializer {

    @Override
    public <T> T deserialize(Message message, Class<T> clazz) {
      shouldBeTrue(message instanceof TextMessage);
      TextMessage tMsg = (TextMessage) message;
      try {
        if (message.propertyExists("SC_USER_ID") && message.propertyExists("SC_ORG_NAME")) {
          Long userId = message.getLongProperty("SC_USER_ID");// FIXME DON
          String userName = message.getStringProperty("SC_USER_NAME");
          Long orgId = message.getLongProperty("SC_ORG_ID");
          String orgName = message.getStringProperty("SC_ORG_NAME");
          SecurityContextHolder.propagateSecurityContext(userId, userName, orgId, orgName);
        }
        return from(tMsg.getText(), clazz);
      } catch (JMSException e) {
        throw new CorantRuntimeException(e);
      }
    }

    @Override
    public Message serialize(JMSContext jmsContext, Serializable object) {
      Message message = jmsContext.createTextMessage(to(object));
      resolveSchemaProperty(message, SerializationSchema.JSON_STRING);
      try {
        Participator user = currentUser(), org = currentOrg();
        if (user != null) {
          message.setLongProperty("SC_USER_ID", user.getId());// FIXME DON
          message.setStringProperty("SC_USER_NAME", user.getName());
        }
        if (org != null) {
          message.setLongProperty("SC_ORG_ID", org.getId());
          message.setStringProperty("SC_ORG_NAME", org.getName());
        }
      } catch (JMSException e) {
        throw new CorantRuntimeException(e);
      }
      return message;
    }

    <T> T from(String text, Class<T> clazz) {
      try {
        return jsonObjectMapper.readValue(text, clazz);
      } catch (IOException e) {
        throw new GeneralRuntimeException(e, GlobalMessageCodes.ERR_OBJ_SEL, text, clazz.getName());
      }
    }

    String to(Serializable message) {
      try {
        return jsonObjectMapper.writeValueAsString(message);
      } catch (JsonProcessingException e) {
        throw new GeneralRuntimeException(e, GlobalMessageCodes.ERR_OBJ_SEL, message);
      }
    }
  }
}
