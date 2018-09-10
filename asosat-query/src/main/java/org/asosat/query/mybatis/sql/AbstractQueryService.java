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
package org.asosat.query.mybatis.sql;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.asosat.kernel.abstraction.Query;
import org.asosat.kernel.exception.GeneralRuntimeException;

/**
 * asosat-mybatis
 *
 * @author bingo 下午4:03:56
 *
 */
@ApplicationScoped
public abstract class AbstractQueryService implements Query<String, Map<String, Object>> {

  @Inject
  SqlSessionFactory sqlSessionFactory;

  @Override
  public <T> T get(String q, Map<String, Object> param) {
    try (SqlSession session = this.getSession()) {
      return session.selectOne(q, param);
    }
  }

  public SqlSession getSession() {
    return this.sqlSessionFactory.openSession();
  }

  @Override
  public <T> IteratedList<T> iterating(String q, Map<String, Object> param) {
    return null;
  }

  @Override
  public <T> PagedList<T> paging(String q, Map<String, Object> param) {
    try (SqlSession session = this.getSession()) {
    }
    return null;
  }

  @Override
  public <T> List<T> select(String q, Map<String, Object> param) {
    try (SqlSession session = this.getSession()) {
      return session.selectList(q, param);
    }
  }

  @Override
  public <T> Stream<T> stream(String q, Map<String, Object> param) {
    final SqlSession session = this.getSession();
    try {
      Cursor<T> cursor = session.selectCursor(q, param);
      if (cursor != null) {
        return StreamSupport.stream(cursor.spliterator(), false).onClose(session::close);
      } else {
        session.close();
        return Stream.empty();
      }
    } catch (Exception e) {
      session.close();
      throw new GeneralRuntimeException(e);
    }
  }

  protected BoundSql getBoundSql(String q, Map<String, Object> param) {
    MappedStatement ms = this.sqlSessionFactory.getConfiguration().getMappedStatement(q);
    return ms.getBoundSql(param);
  }

}
