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

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.asosat.kernel.util.ConvertUtils;

/**
 * asosat-script
 *
 * @author bingo 下午5:37:39
 *
 */
public class JdbcUtils {
  /**
   * Constant that indicates an unknown (or unspecified) SQL type.
   *
   * @see java.sql.Types
   */
  public static final int TYPE_UNKNOWN = Integer.MIN_VALUE;
  private static final Logger logger = Logger.getLogger(JdbcUtils.class.getName());


  /**
   * Close the given JDBC Connection and ignore any thrown exception. This is useful for typical
   * finally blocks in manual JDBC code.
   *
   * @param con the JDBC Connection to close (may be {@code null})
   */
  public static void closeConnection(Connection con) {
    if (con != null) {
      try {
        con.close();
      } catch (SQLException ex) {
        logger.log(Level.WARNING, ex, () -> "Could not close JDBC Connection");
      } catch (Throwable ex) {
        // We don't trust the JDBC driver: It might throw RuntimeException or Error.
        logger.log(Level.WARNING, ex, () -> "Unexpected exception on closing JDBC Connection");
      }
    }
  }

  /**
   * Close the given JDBC ResultSet and ignore any thrown exception. This is useful for typical
   * finally blocks in manual JDBC code.
   *
   * @param rs the JDBC ResultSet to close (may be {@code null})
   */
  public static void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException ex) {
        logger.log(Level.WARNING, ex, () -> "Could not close JDBC ResultSet");
      } catch (Throwable ex) {
        logger.log(Level.WARNING, ex, () -> "Unexpected exception on closing JDBC ResultSet");
      }
    }
  }

  /**
   * Close the given JDBC Statement and ignore any thrown exception. This is useful for typical
   * finally blocks in manual JDBC code.
   *
   * @param stmt the JDBC Statement to close (may be {@code null})
   */
  public static void closeStatement(Statement stmt) {
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException ex) {
        logger.log(Level.WARNING, ex, () -> "Could not close JDBC Statement");
      } catch (Throwable ex) {
        // We don't trust the JDBC driver: It might throw RuntimeException or Error.
        logger.log(Level.WARNING, ex, () -> "Unexpected exception on closing JDBC Statement");
      }
    }
  }



  /**
   * Retrieve a JDBC column value from a ResultSet, using the most appropriate value type. The
   * returned value should be a detached value object, not having any ties to the active ResultSet:
   * in particular, it should not be a Blob or Clob object but rather a byte array or String
   * representation, respectively.
   * <p>
   * Uses the {@code getObject(index)} method, but includes additional "hacks" to get around Oracle
   * 10g returning a non-standard object for its TIMESTAMP datatype and a {@code java.sql.Date} for
   * DATE columns leaving out the time portion: These columns will explicitly be extracted as
   * standard {@code java.sql.Timestamp} object.
   *
   * @param rs is the ResultSet holding the data
   * @param index is the column index
   * @return the value object
   * @throws SQLException if thrown by the JDBC API
   * @see java.sql.Blob
   * @see java.sql.Clob
   * @see java.sql.Timestamp
   */
  public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
    Object obj = rs.getObject(index);
    String className = null;
    if (obj != null) {
      className = obj.getClass().getName();
    }
    if (obj instanceof Blob) {
      Blob blob = (Blob) obj;
      obj = blob.getBytes(1, (int) blob.length());
    } else if (obj instanceof Clob) {
      Clob clob = (Clob) obj;
      obj = clob.getSubString(1, (int) clob.length());
    } else if ("oracle.sql.TIMESTAMP".equals(className)
        || "oracle.sql.TIMESTAMPTZ".equals(className)) {
      obj = rs.getTimestamp(index);
    } else if (className != null && className.startsWith("oracle.sql.DATE")) {
      String metaDataClassName = rs.getMetaData().getColumnClassName(index);
      if ("java.sql.Timestamp".equals(metaDataClassName)
          || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
        obj = rs.getTimestamp(index);
      } else {
        obj = rs.getDate(index);
      }
    } else if (obj instanceof java.sql.Date) {
      if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
        obj = rs.getTimestamp(index);
      }
    }
    return obj;
  }

  /**
   * Retrieve a JDBC column value from a ResultSet, using the specified value type.
   * <p>
   * Uses the specifically typed ResultSet accessor methods, falling back to
   * {@link #getResultSetValue(java.sql.ResultSet, int)} for unknown types.
   * <p>
   * Note that the returned value may not be assignable to the specified required type, in case of
   * an unknown type. Calling code needs to deal with this case appropriately, e.g. throwing a
   * corresponding exception.
   *
   * @param rs is the ResultSet holding the data
   * @param index is the column index
   * @param requiredType the required value type (may be {@code null})
   * @return the value object (possibly not of the specified required type, with further conversion
   *         steps necessary)
   * @throws SQLException if thrown by the JDBC API
   * @see #getResultSetValue(ResultSet, int)
   */
  public static Object getResultSetValue(ResultSet rs, int index, Class<?> requiredType)
      throws SQLException {
    if (requiredType == null) {
      return getResultSetValue(rs, index);
    }

    Object value;

    // Explicitly extract typed value, as far as possible.
    if (String.class == requiredType) {
      return rs.getString(index);
    } else if (boolean.class == requiredType || Boolean.class == requiredType) {
      value = rs.getBoolean(index);
    } else if (byte.class == requiredType || Byte.class == requiredType) {
      value = rs.getByte(index);
    } else if (short.class == requiredType || Short.class == requiredType) {
      value = rs.getShort(index);
    } else if (int.class == requiredType || Integer.class == requiredType) {
      value = rs.getInt(index);
    } else if (long.class == requiredType || Long.class == requiredType) {
      value = rs.getLong(index);
    } else if (float.class == requiredType || Float.class == requiredType) {
      value = rs.getFloat(index);
    } else if (double.class == requiredType || Double.class == requiredType
        || Number.class == requiredType) {
      value = rs.getDouble(index);
    } else if (BigDecimal.class == requiredType) {
      return rs.getBigDecimal(index);
    } else if (java.sql.Date.class == requiredType) {
      return rs.getDate(index);
    } else if (java.sql.Time.class == requiredType) {
      return rs.getTime(index);
    } else if (java.sql.Timestamp.class == requiredType || java.util.Date.class == requiredType) {
      return rs.getTimestamp(index);
    } else if (byte[].class == requiredType) {
      return rs.getBytes(index);
    } else if (Blob.class == requiredType) {
      return rs.getBlob(index);
    } else if (Clob.class == requiredType) {
      return rs.getClob(index);
    } else if (requiredType.isEnum()) {
      // leave enum type conversion up to the caller (e.g. a ConversionService)
      // but make sure that we return nothing other than a String or an Integer.
      Object obj = rs.getObject(index);
      if (obj instanceof String) {
        return obj;
      } else if (obj instanceof Number) {
        // Defensively convert any Number to an Integer (as needed by our
        // ConversionService's IntegerToEnumConverterFactory) for use as index
        return ConvertUtils.toInteger(obj);
      } else {
        // e.g. on Postgres: getObject returns a PGObject but we need a String
        return rs.getString(index);
      }
    }

    else {
      // Some unknown type desired -> rely on getObject.
      try {
        return rs.getObject(index, requiredType);
      } catch (AbstractMethodError err) {
        logger.log(Level.WARNING, err,
            () -> "JDBC driver does not implement JDBC 4.1 'getObject(int, Class)' method");
      } catch (SQLFeatureNotSupportedException ex) {
        logger.log(Level.WARNING, ex,
            () -> "JDBC driver does not support JDBC 4.1 'getObject(int, Class)' method");
      } catch (SQLException ex) {
        logger.log(Level.WARNING, ex,
            () -> "JDBC driver has limited support for JDBC 4.1 'getObject(int, Class)' method");
      }

      // Corresponding SQL types for JSR-310 / Joda-Time types, left up
      // to the caller to convert them (e.g. through a ConversionService).
      String typeName = requiredType.getSimpleName();
      if ("LocalDate".equals(typeName)) {
        return rs.getDate(index);
      } else if ("LocalTime".equals(typeName)) {
        return rs.getTime(index);
      } else if ("LocalDateTime".equals(typeName)) {
        return rs.getTimestamp(index);
      }

      // Fall back to getObject without type specification, again
      // left up to the caller to convert the value if necessary.
      return getResultSetValue(rs, index);
    }

    // Perform was-null check if necessary (for results that the JDBC driver returns as primitives).
    return (rs.wasNull() ? null : value);
  }


  /**
   * Check whether the given SQL type is numeric.
   *
   * @param sqlType the SQL type to be checked
   * @return whether the type is numeric
   */
  public static boolean isNumeric(int sqlType) {
    return Types.BIT == sqlType || Types.BIGINT == sqlType || Types.DECIMAL == sqlType
        || Types.DOUBLE == sqlType || Types.FLOAT == sqlType || Types.INTEGER == sqlType
        || Types.NUMERIC == sqlType || Types.REAL == sqlType || Types.SMALLINT == sqlType
        || Types.TINYINT == sqlType;
  }

  /**
   * Determine the column name to use. The column name is determined based on a lookup using
   * ResultSetMetaData.
   * <p>
   * This method implementation takes into account recent clarifications expressed in the JDBC 4.0
   * specification:
   * <p>
   * <i>columnLabel - the label for the column specified with the SQL AS clause. If the SQL AS
   * clause was not specified, then the label is the name of the column</i>.
   *
   * @return the column name to use
   * @param resultSetMetaData the current meta data to use
   * @param columnIndex the index of the column for the look up
   * @throws SQLException in case of lookup failure
   */
  public static String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex)
      throws SQLException {
    String name = resultSetMetaData.getColumnLabel(columnIndex);
    if (name == null || name.length() < 1) {
      name = resultSetMetaData.getColumnName(columnIndex);
    }
    return name;
  }

}
