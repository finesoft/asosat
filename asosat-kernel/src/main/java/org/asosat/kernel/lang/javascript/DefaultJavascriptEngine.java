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
package org.asosat.kernel.lang.javascript;

import java.io.Reader;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * nashorn script engine wrapped
 *
 * @author bingo 上午9:59:37
 *
 */
@ApplicationScoped
public class DefaultJavascriptEngine {

  private ScriptEngine javascriptEngine;

  public DefaultJavascriptEngine() {
    super();
  }

  public Bindings createBindings() {
    return this.javascriptEngine.createBindings();
  }

  public Object eval(Reader reader) throws ScriptException {
    return this.javascriptEngine.eval(reader);
  }

  public Object eval(Reader reader, Bindings n) throws ScriptException {
    return this.javascriptEngine.eval(reader, n);
  }

  public Object eval(Reader reader, ScriptContext context) throws ScriptException {
    return this.javascriptEngine.eval(reader, context);
  }

  public Object eval(String script) throws ScriptException {
    return this.javascriptEngine.eval(script);
  }

  public Object eval(String script, Bindings n) throws ScriptException {
    return this.javascriptEngine.eval(script, n);
  }

  public Object eval(String script, ScriptContext context) throws ScriptException {
    return this.javascriptEngine.eval(script, context);
  }

  public Object get(String key) {
    return this.javascriptEngine.get(key);
  }

  public Bindings getBindings(int scope) {
    return this.javascriptEngine.getBindings(scope);
  }

  public ScriptContext getContext() {
    return this.javascriptEngine.getContext();
  }

  public ScriptEngineFactory getFactory() {
    return this.javascriptEngine.getFactory();
  }

  @PostConstruct
  public void initialize() {
    this.javascriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
  }

  public void put(String key, Object value) {
    this.javascriptEngine.put(key, value);
  }

  public void setBindings(Bindings bindings, int scope) {
    this.javascriptEngine.setBindings(bindings, scope);
  }

  public void setContext(ScriptContext context) {
    this.javascriptEngine.setContext(context);
  }

}
