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

import static org.asosat.kernel.util.MyObjUtils.EMPTY_ARGS;
import static org.asosat.kernel.util.MyStrUtils.asDefaultString;
import static org.asosat.kernel.util.MyStrUtils.asString;
import java.util.Arrays;
import java.util.Locale;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.asosat.kernel.abstraction.Readable;
import org.asosat.kernel.resource.EnumerationResource;
import org.asosat.kernel.resource.GlobalMessageCodes;
import org.asosat.kernel.resource.MessageSeverity;
import org.asosat.kernel.resource.PropertyMessageResource;

/**
 * @author bingo 上午10:31:53
 *
 */
@ApplicationScoped
public class GeneralRuntimeExceptionMessagerImpl implements GeneralRuntimeExceptionMessager {

  @Inject
  private PropertyMessageResource messageSource;

  @Inject
  private EnumerationResource enumSource;

  public GeneralRuntimeExceptionMessagerImpl() {}

  public String genMessageKey(String code, String subCode) {
    StringBuilder sb = new StringBuilder(MessageSeverity.ERR.name()).append(".").append(code);
    if (subCode != null) {
      sb.append(".").append(subCode);
    }
    return sb.toString();
  }


  public Object[] genParameters(Locale locale, Object[] parameters) {
    if (parameters.length > 0) {
      return Arrays.stream(parameters).map(p -> this.handleParameter(locale, p)).toArray();
    }
    return new Object[0];
  }

  @Override
  public String getMessage(Locale locale, GeneralRuntimeException exception) {
    if (exception == null) {
      return null;
    }
    String key =
        this.genMessageKey(asDefaultString(exception.getCode()), asString(exception.getSubCode()));
    Locale localeToUse = locale == null ? Locale.getDefault() : locale;
    Object[] parameters = this.genParameters(localeToUse, exception.getParameters());
    return this.messageSource.getMessage(localeToUse, key, parameters,
        (l) -> this.getUnknowErrorMessage(l));
  }

  @Override
  public String getUnknowErrorMessage(Locale locale) {
    return this.messageSource.getMessage(locale,
        this.genMessageKey(GlobalMessageCodes.ERR_UNKNOW, null), EMPTY_ARGS);
  }

  @SuppressWarnings("rawtypes")
  private Object handleParameter(Locale locale, Object obj) {
    if (obj instanceof Enum) {
      String literal = this.enumSource.getEnumItemLiteral((Enum) obj, locale);
      return literal == null ? obj : literal;
    } else if (obj instanceof Readable) {
      return ((Readable) obj).toHumanReader(locale);
    }
    return obj;
  }
}
