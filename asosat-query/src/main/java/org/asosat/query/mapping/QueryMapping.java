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
package org.asosat.query.mapping;

import static org.asosat.kernel.util.MyBagUtils.isEmpty;
import static org.asosat.kernel.util.MyObjUtils.isEquals;
import static org.asosat.kernel.util.MyObjUtils.isNull;
import static org.asosat.kernel.util.MyStrUtils.isBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * asosat-query
 *
 * @author bingo 下午3:41:30
 *
 */
public class QueryMapping {

  String url;
  final List<Query> queries = new ArrayList<>();
  final Map<String, ParameterMapping> paraMapping = new HashMap<>();
  String commonSegment;

  /**
   * @return the commonSegment
   */
  public String getCommonSegment() {
    return this.commonSegment;
  }

  /**
   * @return the paraMapping
   */
  public Map<String, ParameterMapping> getParaMapping() {
    return this.paraMapping;
  }

  /**
   * @return the queries
   */
  public List<Query> getQueries() {
    return this.queries;
  }

  /**
   * @return the uri
   */
  public String getUrl() {
    return this.url;
  }

  public List<String> selfValidate() {
    List<String> brokens = new ArrayList<>();
    // validate parameters-mapping elements
    this.getParaMapping().values().forEach(p -> {
      if (p.getType() == null) {
        brokens.add(String.format(
            "The 'type' attribute of parameter entry element in query file [%s] with name [%s] can not null!",
            this.url, p.getName()));
      }
    });
    if (isEmpty(this.getQueries())) {
      brokens.add(String.format("The query file [%s] must have 'query' elements!", this.getUrl()));
    }
    Set<String> queryNames = new HashSet<>();
    // validate query elements
    this.getQueries().stream().forEach(q -> {
      if (isBlank(q.getName())) {
        brokens
            .add(String.format("The query file [%s] has noname 'query' element!", this.getUrl()));
      }
      if (q.getResultClass() == null) {
        brokens.add(String.format(
            "The 'result-class' attribute of 'query' element [%s] in query file [%s] can not null!",
            q.getName(), this.getUrl()));
      }
      if (isBlank(q.getScript())) {
        brokens.add(String.format(
            "The 'script' element in 'query' element [%s] in query file [%s] can not null!",
            q.getName(), this.getUrl()));
      }
      if (queryNames.contains(q.getVersionedName())) {
        brokens.add(String.format(
            "The 'name' attribute of 'query' element [%s] in query file [%s] can not repeat!",
            q.getName(), this.getUrl()));
      } else {
        queryNames.add(q.getVersionedName());
      }
      // validate fetch queries elements
      q.getFetchQueries().forEach(fq -> {
        Set<String> injectProNames = new HashSet<>();
        if (isBlank(fq.getReferenceQuery())) {
          brokens.add(String.format(
              "The 'reference-query' attribute of 'fetch-query' in query element [%s] in query file [%s] can not null!",
              q.getName(), this.getUrl()));
        }
        if (isBlank(fq.getInjectPropertyName())) {
          brokens.add(String.format(
              "The 'inject-property-name' attribute of 'fetch-query' in query element [%s] in query file [%s] can not null!",
              q.getName(), this.getUrl()));
        } else if (injectProNames.contains(fq.getInjectPropertyName())) {
          brokens.add(String.format(
              "The 'fetch-query' [%s] with 'inject-property-name' [%s] in query element [%s] in query file [%s] can not repeat!",
              fq.getReferenceQuery(), fq.getInjectPropertyName(), q.getName(), this.getUrl()));
        } else {
          injectProNames.add(fq.getInjectPropertyName());
        }
        if (isEquals(q.getVersionedName(), fq.getVersionedReferenceQueryName())) {
          brokens.add(String.format(
              "The 'fetch-query' [%s] in query element [%s] in query file [%s] can not reference the parent query!",
              fq.getReferenceQuery(), q.getName(), this.getUrl()));
        }
        // validate fetch queries parameter
        fq.getParameters().forEach(fqp -> {
          if (isBlank(fqp.getName())) {
            brokens.add(String.format(
                "The 'name' attribute of 'parameter' in fetch query [%s] in query element [%s] in query file [%s] can not null!",
                fq.getReferenceQuery(), q.getName(), this.getUrl()));
          }
          if (isNull(fqp.getSource())) {
            brokens.add(String.format(
                "The 'source' attribute of 'parameter' in fetch query [%s] in query element [%s] in query file [%s] can not null!",
                fq.getReferenceQuery(), q.getName(), this.getUrl()));
          }
        });
      });
    });
    return brokens;
  }

}
