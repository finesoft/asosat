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
package org.asosat.thorntail.provider;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.normal.conversion.Conversions;
import org.asosat.kernel.util.IdentifierGenerators;
import org.eclipse.microprofile.config.Config;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * @author bingo 下午5:00:54
 *
 */
public class HibernateSnowflakeIdGenerator implements IdentifierGenerator {

  static final String IDGEN_SF_WK_ID = "identifierGenerator.snowflake.worker.id";
  static final String IDGEN_SF_DC_ID = "identifierGenerator.snowflake.datacenter.id";
  static org.asosat.kernel.util.IdentifierGenerators.IdentifierGenerator GENERATOR;
  static volatile boolean ENABLED = false;
  static volatile String TSSQL = null;

  public HibernateSnowflakeIdGenerator() {
    if (!ENABLED) {
      synchronized (HibernateSnowflakeIdGenerator.class) {
        if (!ENABLED) {
          if (Conversions.toInteger(
              DefaultContext.bean(Config.class).getValue(IDGEN_SF_DC_ID, Integer.class), -1) < 0) {
            GENERATOR =
                IdentifierGenerators.snowflakeUUIDGenerator(
                    DefaultContext.bean(Config.class).getValue(IDGEN_SF_DC_ID, Integer.class),
                    Conversions.toInteger(
                        DefaultContext.bean(Config.class).getValue(IDGEN_SF_WK_ID, Integer.class),
                        0));
          } else {
            GENERATOR = IdentifierGenerators.snowflakeBufferUUIDGenerator(
                Conversions.toInteger(
                    DefaultContext.bean(Config.class).getValue(IDGEN_SF_WK_ID, Integer.class), 0),
                true);
          }
          ENABLED = true;
        }
      }
    }
  }

  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object)
      throws HibernateException {
    return GENERATOR.generate(() -> this.timeSeq(session));
  }

  long timeSeq(SharedSessionContractImplementor session) {
    if (TSSQL == null) {
      synchronized (HibernateSnowflakeIdGenerator.class) {
        if (TSSQL == null) {
          TSSQL = session.getFactory().getServiceRegistry().getService(JdbcServices.class)
              .getDialect().getCurrentTimestampSelectString();
        }
      }
    }
    try {
      final PreparedStatement st =
          session.getJdbcCoordinator().getStatementPreparer().prepareStatement(TSSQL);
      try {
        final ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(st);
        try {
          rs.next();
          long value = rs.getTimestamp(1).getTime();
          return value;
        } finally {
          try {
            session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs,
                st);
          } catch (Throwable ignore) {
          }
        }
      } finally {
        session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(st);
        session.getJdbcCoordinator().afterStatementExecution();
      }

    } catch (SQLException sqle) {
      throw session.getFactory().getServiceRegistry().getService(JdbcServices.class)
          .getSqlExceptionHelper().convert(sqle, "could not get next sequence value", TSSQL);
    }
  }

}
