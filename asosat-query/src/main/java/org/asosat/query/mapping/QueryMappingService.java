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

import static org.asosat.kernel.util.MyObjUtils.isEquals;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.asosat.query.QueryRuntimeException;

/**
 * asosat-query
 *
 * @author bingo 下午12:59:22
 *
 */
@ApplicationScoped
public class QueryMappingService {

  private Map<String, Query> queries = new HashMap<>();

  public Query getQuery(String name) {
    return this.queries.get(name);
  }

  @PostConstruct
  public void init() {
    new QueryParser().parse().forEach(m -> {
      List<String> brokens = m.selfValidate();
      if (!brokens.isEmpty()) {
        throw new QueryRuntimeException(String.join("\n", brokens.toArray(new String[0])));
      }
      m.getQueries().forEach(q -> {
        q.getParamMappings().putAll(m.getParaMapping());// copy
        if (this.queries.containsKey(q.getVersionedName())) {
          throw new QueryRuntimeException(
              String.format("The 'name' [%s] of query element in query file [%s] can not repeat!",
                  q.getVersionedName(), m.getUrl()));
        } else {
          this.queries.put(q.getVersionedName(), q);
        }
      });
    });
    this.queries.keySet().forEach(q -> {
      List<String> refs = new LinkedList<>();
      List<String> tmp = new LinkedList<>(this.queries.get(q).getVersionedFetchQueryNames());
      while (!tmp.isEmpty()) {
        String tq = tmp.remove(0);
        refs.add(tq);
        if (isEquals(tq, q)) {
          throw new QueryRuntimeException(
              String.format("The queries in system circular reference occurred on [%s -> %s]", q,
                  String.join(" -> ", refs.toArray(new String[0]))));
        }
        Query fq = this.queries.get(tq);
        if (fq == null) {
          throw new QueryRuntimeException(String.format(
              "The 'name' [%s] of 'fetch-query' in query [%s] in system can not found the refered query!",
              tq, q));
        }
        tmp.addAll(this.queries.get(tq).getVersionedFetchQueryNames());
      }
      refs.clear();
    });
  }

}
