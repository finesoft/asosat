/*
 * Copyright (c) 2013-2018. BIN.CHEN
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
package org.asosat.kernel.exception;

import static org.asosat.kernel.util.MyBagUtils.asList;
import static org.asosat.kernel.util.MyStrUtils.asDefaultString;
import static org.asosat.kernel.util.MyStrUtils.defaultString;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.resource.GlobalMessageCodes;

/**
 * @author bingo 下午6:19:52
 *
 */
public class GeneralRuntimeException extends RuntimeException {

  private static final long serialVersionUID = -3720369148530068164L;

  private Object code;

  private Object subCode;

  private GeneralExceptionSeverity serverity = GeneralExceptionSeverity.ERROR;

  private Object[] parameters = new Object[0];

  private Map<Object, Object> attributes = new HashMap<>();

  public GeneralRuntimeException() {
    this(GlobalMessageCodes.ERR_UNKNOW);
  }

  public GeneralRuntimeException(Object code) {
    this(code, null, new HashMap<>(), new Object[0]);
  }

  public GeneralRuntimeException(Object code, Object... variants) {
    this(code, null, new HashMap<>(), variants);
  }

  public GeneralRuntimeException(Object code, Object subCode, Map<Object, Object> attributes,
      Object... parameters) {
    super();
    this.setCode(code);
    this.setSubCode(subCode);
    this.setParameters(parameters);
    this.setAttributes(attributes);
  }

  public GeneralRuntimeException(Throwable cause) {
    super(cause);
    if (cause instanceof GeneralRuntimeException) {
      GeneralRuntimeException causeToUse = (GeneralRuntimeException) cause;
      this.setCode(causeToUse.getCode());
      this.setSubCode(causeToUse.getSubCode());
      this.setParameters(causeToUse.getParameters());
      this.setAttributes(causeToUse.getAttributes());
    }
  }

  public GeneralRuntimeException(Throwable cause, Object code) {
    super(cause);
    this.setCode(code);
  }

  public GeneralRuntimeException(Throwable cause, Object code, Object... parameters) {
    super(cause);
    this.setCode(code);
    this.setParameters(parameters);
  }

  public GeneralRuntimeException attribute(Object name, Object value) {
    this.attributes.put(name, value);
    return this;
  }

  public GeneralRuntimeException attributes(
      Function<Map<Object, Object>, Map<Object, Object>> func) {
    if (func != null) {
      this.setAttributes(func.apply(new HashMap<>(this.attributes)));
    }
    return this;
  }

  /**
   * For handle intention
   */
  public Map<Object, Object> getAttributes() {
    return Collections.unmodifiableMap(this.attributes);
  }

  /**
   * Imply the exception type, for exception type intention
   */
  public Object getCode() {
    return this.code;
  }

  @Override
  public String getLocalizedMessage() {
    GeneralRuntimeExceptionMessager msger =
        DefaultContext.bean(GeneralRuntimeExceptionMessager.class);
    if (msger != null) {
      return this.getLocalizedMessage(Locale.getDefault(), msger);
    } else {
      return defaultString(super.getMessage()) + " " + asDefaultString(this.getCode());
    }
  }

  public String getLocalizedMessage(Locale locale, GeneralRuntimeExceptionMessager messager) {
    return messager.getMessage(locale, this);
  }

  @Override
  public String getMessage() {
    return this.getLocalizedMessage();
  }

  public Object[] getParameters() {
    return Arrays.copyOf(this.parameters, this.parameters.length);
  }

  public GeneralExceptionSeverity getServerity() {
    return this.serverity;
  }

  /**
   * Imply the exception type polymorph, for exception type polymorph
   */
  public Object getSubCode() {
    return this.subCode;
  }

  /**
   * Supply the original parameters list for handler and then return the new variants
   */
  public GeneralRuntimeException parameters(Function<List<Object>, List<Object>> func) {
    if (func != null) {
      List<Object> updated = func.apply(asList(this.parameters));
      this.setParameters(updated == null ? new Object[0] : updated.toArray());
    }
    return this;
  }

  public GeneralRuntimeException serverity(GeneralExceptionSeverity serverity) {
    this.setServerity(serverity);
    return this;
  }

  public GeneralRuntimeException subCode(Object subCode) {
    this.setSubCode(subCode);
    return this;
  }

  /**
   * Return the exception wrapper for transform the exception sub code or variants or attributes.
   * Example:
   *
   * <pre>
   * try {
   *    //...domain logic break by new GeneralRuntimeException("original code","original var")
   * } catch (GeneralRuntimeException ex) {
   *   throw ex.wrapper().ifCodeIs("original code").thenSubcode("new sub
   *   code").thenVariants((o)->Arrays.asList("new var")).wrap();
   * }
   * </pre>
   *
   * @see GeneralRuntimeExceptionWrapper
   */
  public GeneralRuntimeExceptionWrapper wrapper() {
    return new GeneralRuntimeExceptionWrapper(this);
  }

  protected void setAttributes(Map<Object, Object> attributes) {
    this.attributes.clear();
    if (attributes != null) {
      this.attributes.putAll(attributes);
    }
  }

  protected void setCode(Object code) {
    this.code = code;
  }

  protected void setParameters(Object[] parameters) {
    this.parameters =
        parameters == null ? new Object[0] : Arrays.copyOf(parameters, parameters.length);
  }

  protected void setServerity(GeneralExceptionSeverity serverity) {
    this.serverity = serverity == null ? GeneralExceptionSeverity.ERROR : serverity;
  }

  protected void setSubCode(Object subCode) {
    this.subCode = subCode;
  }

}
