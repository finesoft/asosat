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
package org.asosat.query.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * asosat-query
 *
 * @author bingo 下午7:56:57
 *
 */
public class DefaultTemplateMethodModelEx implements TemplateMethodModelEx {

  public static final SimpleScalar SQL_PLACE_HOLDER = new SimpleScalar("?");

  private List<Object> parameters = new ArrayList<>();

  @SuppressWarnings("rawtypes")
  @Override
  public Object exec(List arguments) throws TemplateModelException {
    return null;
  }

  public List<Object> getParameters() {
    return Collections.unmodifiableList(this.parameters);
  }

}
