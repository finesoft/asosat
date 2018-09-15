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
package org.asosat.kernel.normalization.setting;

import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

public class DefaultSetting {

  public static final String CHARSET_NAME = "utf-8";
  public static final Charset CHARSET = Charset.forName(CHARSET_NAME);
  public static final DateTimeFormatter DAY_DATE_FORMAT = DateTimeFormatter.ISO_DATE;
  public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
  public static final Locale LOCALE = Locale.CHINA;
  public static final TimeZone TIMEZONE = TimeZone.getTimeZone("Asia/Shanghai");

  public static final int DB_RS_FETCH_SIZE = 4;
  public static final int DBSTREAM_RS_FETCH_SIZE = 64;

  public static final String CMP_PREFIX = "ASOSAT-APPS";
  public static final String ASYNCTASK_EXECUTOR_ID = CMP_PREFIX + "-DEFAULT-ASYNCTASK-EXECUTOR";

}
