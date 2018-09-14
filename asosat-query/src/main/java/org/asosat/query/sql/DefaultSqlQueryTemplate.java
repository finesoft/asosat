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

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.asosat.query.QueryRuntimeException;
import org.asosat.query.dynamic.FreemarkerQueryTemplate;
import org.asosat.query.dynamic.QueryTemplateMethodModelEx;
import org.asosat.query.mapping.Query;
import freemarker.template.TemplateException;

/**
 * asosat-query
 *
 * @author bingo 下午7:46:22
 *
 */
public class DefaultSqlQueryTemplate
    extends FreemarkerQueryTemplate<DefaultSqlQueryParameter, Object[]> {

  /**
   * @param query
   */
  public DefaultSqlQueryTemplate(Query query) {
    super(query);
  }

  @Override
  public DefaultSqlQueryParameter doProcess(Map<String, Object> param) {
    StringWriter sw = new StringWriter();
    try {
      Map<String, Object> paramClone = new HashMap<>(param);
      this.getTemplate().process(paramClone, sw);
    } catch (TemplateException | IOException | NullPointerException e) {
      throw new QueryRuntimeException("Freemarker process stringTemplate is error", e);
    }
    return new DefaultSqlQueryParameter(sw.toString(), new Object[0], this.getResultClass(),
        this.getFetchQueries());// FIXME process parameter
  }

  @Override
  protected QueryTemplateMethodModelEx<Object[]> getTemplateMethodModel() {
    return new DefaultSqlTemplateMethodModelEx();
  }

  @Override
  protected void postProcess(DefaultSqlQueryParameter result,
      QueryTemplateMethodModelEx<Object[]> qtmm) {
    result.withParam(qtmm.getParameters());
    super.postProcess(result, qtmm);
  }

  @Override
  protected void preProcess(Map<String, Object> param, QueryTemplateMethodModelEx<Object[]> qtmm) {
    super.preProcess(param, qtmm);
  }

}
