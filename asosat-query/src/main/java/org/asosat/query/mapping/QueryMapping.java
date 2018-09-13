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
    this.paraMapping.values().forEach(p -> {
      if (p.type == null) {
        brokens.add(String.format(
            "The parameter entry element in query file [%s] with name [%s] must have type attribute!",
            this.url));
      }
    });
    if (isEmpty(this.queries)) {
      brokens.add(String.format("The query file [%s] must have query elements!", this.url));
    }
    Set<String> queryNames = new HashSet<>();
    // validate query elements
    this.queries.stream().forEach(q -> {
      if (isBlank(q.getName())) {
        brokens.add(String.format("The query file [%s] has noname query elements!", this.getUrl()));
      }
      if (q.resultClass == null) {
        brokens.add(String.format(
            "The query element [%s] in query file [%s] must have an non null 'result-class' attribute!",
            q.name, this.getUrl()));
      }
      if (isBlank(q.getScript())) {
        brokens.add(String.format(
            "The script element in query element [%s] in query file [%s] can not null!", q.name,
            this.getUrl()));
      }
      if (queryNames.contains(q.getVersionedName())) {
        brokens.add(String.format(
            "The name [%s] of query element in query file [%s] can not repeat!", this.getUrl()));
      } else {
        queryNames.add(q.getVersionedName());
      }
      // validate fetch queries elements
      q.fetchQueries.forEach(fq -> {
        if (isBlank(fq.referenceQuery)) {
          brokens
              .add(String.format("The query file [%s] has noname query elements!", this.getUrl()));
        }
      });
    });
    return brokens;
  }

}