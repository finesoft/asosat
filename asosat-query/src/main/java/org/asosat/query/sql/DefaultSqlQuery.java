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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.asosat.kernel.abstraction.Query;

/**
 * asosat-script
 *
 * @author bingo 下午5:33:21
 *
 */
public class DefaultSqlQuery implements Query<String, Map<String, Object>> {

  @Override
  public <T> T get(String q, Map<String, Object> param) {
    return null;
  }

  @Override
  public <T> IteratedList<T> iterating(String q, Map<String, Object> param) {
    return null;
  }

  @Override
  public <T> PagedList<T> paging(String q, Map<String, Object> param) {
    return null;
  }

  @Override
  public <T> List<T> select(String q, Map<String, Object> param) {
    return null;
  }

  @Override
  public <T> Stream<T> stream(String q, Map<String, Object> param) {
    return null;
  }

}
