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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author bingo 上午10:24:46
 *
 */
public class GeneralRuntimeExceptionWrapper {

  private final GeneralRuntimeException exception;
  private final List<ConjoinedBuilder> builders = new LinkedList<>();

  GeneralRuntimeExceptionWrapper(GeneralRuntimeException exception) {
    this.exception = exception;
  }

  public ConjoinedBuilder ifCodeIs(Object code) {
    return new ConjoinedBuilder(this, code);
  }

  public Builder whatever() {
    Builder builder = new Builder(this, null);
    return builder;
  }

  public static class Builder {
    private Object[] parameters = new Object[0];
    private Map<Object, Object> attributes = new HashMap<>();
    private Object subCode;
    private final Object code;
    private final GeneralRuntimeExceptionWrapper wrapper;

    Builder(GeneralRuntimeExceptionWrapper wrapper, Object code) {
      super();
      this.code = code;
      this.wrapper = wrapper;
      int varLen = wrapper.exception.getParameters().length;
      if (varLen > 0) {
        this.parameters = new Object[varLen];
        System.arraycopy(wrapper.exception.getParameters(), 0, this.parameters, 0, varLen);
      }
      this.attributes.putAll(wrapper.exception.getAttributes());
      this.subCode = wrapper.exception.getSubCode();
    }

    public Builder thenAttributes(Function<Map<Object, Object>, Map<Object, Object>> func) {
      if (func != null) {
        this.setAttributes(func.apply(new HashMap<>(this.attributes)));
      }
      return this;
    }

    public Builder thenAttributes(Map<Object, Object> attributes) {
      return this.thenAttributes((t) -> attributes);
    }

    public Builder thenParameters(Function<List<Object>, List<Object>> func) {
      if (func != null) {
        List<Object> updated = func.apply(asList(this.parameters));
        this.setParameters(updated == null ? null : updated.toArray());
      }
      return this;
    }

    public Builder thenParameters(List<Object> parameters) {
      return this.thenParameters((t) -> parameters);
    }

    public Builder thenSubCode(Object subCode) {
      this.subCode = subCode;
      return this;
    }

    public GeneralRuntimeException wrap() {
      GeneralRuntimeException ex = this.wrapper.exception.attributes((t) -> this.getAttributes())
          .parameters((t) -> asList(this.getParameters())).subCode(this.getSubCode());
      this.attributes.clear();
      return ex;
    }

    Map<Object, Object> getAttributes() {
      return this.attributes;
    }

    Object getCode() {
      return this.code;
    }

    Object[] getParameters() {
      return this.parameters;
    }

    Object getSubCode() {
      return this.subCode;
    }

    GeneralRuntimeExceptionWrapper getWrapper() {
      return this.wrapper;
    }

    void setAttributes(Map<Object, Object> attributes) {
      this.attributes.clear();
      if (attributes != null) {
        this.attributes.putAll(attributes);
      }
    }

    void setParameters(Object[] parameters) {
      this.parameters = parameters == null ? new Object[0] : parameters;
    }

  }

  public static class ConjoinedBuilder extends Builder {
    ConjoinedBuilder(GeneralRuntimeExceptionWrapper wrapper, Object code) {
      super(wrapper, code);
      wrapper.builders.removeIf(p -> Objects.equals(p.getCode(), code));
      wrapper.builders.add(this);
    }

    public ConjoinedBuilder elseIfCodeIs(Object code) {
      return new ConjoinedBuilder(this.getWrapper(), code);
    }

    @Override
    public ConjoinedBuilder thenAttributes(
        Function<Map<Object, Object>, Map<Object, Object>> func) {
      super.thenAttributes(func);
      return this;
    }

    @Override
    public ConjoinedBuilder thenAttributes(Map<Object, Object> attributes) {
      super.thenAttributes(attributes);
      return this;
    }

    @Override
    public ConjoinedBuilder thenParameters(Function<List<Object>, List<Object>> func) {
      super.thenParameters(func);
      return this;
    }

    @Override
    public ConjoinedBuilder thenParameters(List<Object> parameters) {
      super.thenParameters(parameters);
      return this;
    }

    @Override
    public ConjoinedBuilder thenSubCode(Object subCode) {
      super.thenSubCode(subCode);
      return this;
    }

    @Override
    public GeneralRuntimeException wrap() {
      for (Builder builder : this.getWrapper().builders) {
        if (Objects.equals(builder.code, this.getWrapper().exception.getCode())) {
          this.getWrapper().exception.attributes((t) -> builder.attributes)
              .parameters((t) -> asList(builder.parameters)).subCode(builder.subCode);
          break;
        } else {
          builder.attributes.clear();
        }
      }
      this.getWrapper().builders.clear();
      return this.getWrapper().exception;
    }

  }
}
