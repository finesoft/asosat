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

import static org.asosat.kernel.util.Preconditions.requireNotNull;
import static org.asosat.kernel.util.Preconditions.requireTrue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * asosat-query
 *
 * @author bingo 上午11:04:34
 *
 */
public class RemoveHelper {
  /** Order by 正则表达式 */
  public static final String ORDER_BY_REGEX = "order\\s*by[\\w|\\W|\\s|\\S]*";
  /** Xsql Order by 正则表达式 */
  public static final String XSQL_ORDER_BY_REGEX = "/~.*order\\s*by[\\w|\\W|\\s|\\S]*~/";
  /** From正则表达式 */
  public static final String FROM_REGEX = "\\sfrom\\s";

  /** sql contains whre regex. */
  public static final String WHERE_REGEX = "\\s+where\\s+";
  /** sql contains <code>order by </code> regex. */
  public static final String ORDER_REGEX = "order\\s+by";

  public static boolean containOrder(String sql) {
    return containRegex(sql, ORDER_REGEX);
  }

  public static boolean containRegex(String sql, String regex) {
    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(sql);
    return matcher.find();
  }



  public static boolean containWhere(String sql) {
    return containRegex(sql, WHERE_REGEX);
  }

  public static String removeFetchKeyword(String sql) {
    return sql.replaceAll("(?i)fetch", "");
  }

  /**
   * 去除orderby 子句
   *
   * @param sql sql
   * @return 去掉order by sql
   */
  public static String removeOrders(String sql) {
    requireNotNull(sql, "");// FIXME
    Pattern p = Pattern.compile(ORDER_BY_REGEX, Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(sql);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, "");
    }
    m.appendTail(sb);
    return sb.toString();
  }

  /**
   * 去除select 子句，未考虑union的情况
   *
   * @param sql sql
   * @return 删除掉的selcet的子句
   */
  public static String removeSelect(String sql) {
    requireNotNull(sql, "");// FIXME
    int beginPos = indexOfByRegex(sql.toLowerCase(), FROM_REGEX);
    // Preconditions.checkArgument(beginPos != -1, " sql : " + sql + " must has a keyword 'from'");
    requireTrue(beginPos != -1, "");
    return sql.substring(beginPos);
  }

  public static String removeXsqlBuilderOrders(String string) {
    requireNotNull(string, "");// FIXME
    Pattern p = Pattern.compile(XSQL_ORDER_BY_REGEX, Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(string);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, "");
    }
    m.appendTail(sb);
    return removeOrders(sb.toString());
  }

  private static int indexOfByRegex(String input, String regex) {
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(input);
    if (m.find()) {
      return m.start();
    }
    return -1;
  }
}
