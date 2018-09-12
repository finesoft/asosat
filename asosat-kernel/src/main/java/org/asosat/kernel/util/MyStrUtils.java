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
package org.asosat.kernel.util;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author bingo 上午12:31:35
 *
 */
public class MyStrUtils {

  private MyStrUtils() {
    super();
  }

  public static String asDefaultIfBlank(final Object obj, final String defaultStr) {
    return defaultIfBlank(asDefaultString(obj), defaultStr);
  }

  public static String asDefaultString(final Object obj) {
    return defaultString(asString(obj));
  }

  public static String asString(final Object obj) {
    return obj != null ? obj.toString() : null;
  }

  public static boolean contains(String str, String searchStr) {
    return StringUtils.contains(str, searchStr);
  }

  /**
   * @see StringUtils#defaultIfBlank(CharSequence, CharSequence)
   */
  public static <T extends CharSequence> T defaultIfBlank(final T str, final T defaultStr) {
    return StringUtils.defaultIfBlank(str, defaultStr);
  }

  /**
   * @see StringUtils#defaultIfEmpty(CharSequence, CharSequence)
   */
  public static <T extends CharSequence> T defaultIfEmpty(final T str, final T defaultStr) {
    return StringUtils.defaultIfEmpty(str, defaultStr);
  }

  /**
   * @see StringUtils#defaultString(String)
   */
  public static String defaultString(final String str) {
    return StringUtils.defaultString(str);
  }

  /**
   * @see StringUtils#difference(String, String)
   * @param str1
   * @param str2
   * @return
   */
  public static String difference(final String str1, final String str2) {
    return StringUtils.difference(str1, str2);
  }

  /**
   * @see StringUtils#isAnyBlank(CharSequence)
   * @param cs
   * @return
   */
  public static boolean isAnyBlank(final CharSequence... css) {
    return StringUtils.isAnyBlank(css);
  }


  /**
   * @see StringUtils#isBlank(CharSequence)
   * @param cs
   * @return
   */
  public static boolean isBlank(final CharSequence cs) {
    return StringUtils.isBlank(cs);
  }

  /**
   * @see StringUtils#isNoneBlank(CharSequence...)
   * @param css
   * @return
   */
  public static boolean isNoneBlank(final CharSequence... css) {
    return StringUtils.isNoneBlank(css);
  }

  /**
   * @see StringUtils#isNotBlank(CharSequence)
   * @param cs
   * @return
   */
  public static boolean isNotBlank(final CharSequence cs) {
    return StringUtils.isNotBlank(cs);
  }

  /**
   * @see StringUtils#left(String, int)
   * @param str
   * @param len
   * @return
   */
  public static String left(final String str, final int len) {
    return StringUtils.left(str, len);
  }

  /**
   * @see StringUtils#mid(String, int, int)
   *
   * @param str
   * @param pos
   * @param len
   * @return
   */
  public static String mid(final String str, int pos, final int len) {
    return StringUtils.mid(str, pos, len);
  }

  /**
   * @see StringUtils#right(String, int)
   * @param str
   * @param len
   * @return
   */
  public static String right(final String str, final int len) {
    return StringUtils.right(str, len);
  }

  /**
   * @see StringUtils#split(String, String)
   * @param str
   * @param separatorChars
   * @return
   */
  public static String[] split(final String str, final String separatorChars) {
    return StringUtils.split(str, separatorChars);
  }

  /**
   * @see StringUtils#strip(String, String)
   * @param str
   * @param stripChars
   * @return
   */
  public static String strip(String str, final String stripChars) {
    return StringUtils.strip(str, stripChars);
  }

  /**
   * @see StringUtils#stripAccents(String)
   * @param input
   * @return
   */
  public static String stripAccents(final String input) {
    return StringUtils.stripAccents(input);
  }

  /**
   * @see StringUtils#stripStart(String, String)
   * @param str
   * @param stripChars
   * @return
   */
  public static String stripStart(final String str, final String stripChars) {
    return StringUtils.stripStart(str, stripChars);
  }

  /**
   * @see StringUtils#substring(String, int)
   * @param str
   * @param start
   * @return
   */
  public static String substring(final String str, int start) {
    return StringUtils.substring(str, start);
  }

  /**
   * @see StringUtils#substring(String, int, int)
   * @param str
   * @param start
   * @param end
   * @return
   */
  public static String substring(final String str, int start, int end) {
    return StringUtils.substring(str, start, end);
  }

  /**
   * @see StringUtils#truncate(String, int)
   * @param str
   * @param maxWidth
   * @return
   */
  public static String truncate(final String str, final int maxWidth) {
    return StringUtils.truncate(str, maxWidth);
  }

  /**
   * @see StringUtils#truncate(String, int, int)
   * @param str
   * @param offset
   * @param maxWidth
   * @return
   */
  public static String truncate(final String str, final int offset, final int maxWidth) {
    return StringUtils.truncate(str, offset, maxWidth);
  }
}
