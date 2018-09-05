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
/**
 * @author bingo 下午2:15:13
 *
 */
package org.asosat.kernel.domain.message;

import org.asosat.kernel.resource.GlobalMessageCodes;

class PkgMsgCds implements GlobalMessageCodes {

  static final String ERR_EXMSG_CVT = "exchangeMessage.convert_error";
  static final String ERR_MSG_CFG_QUEUE_NULL = "message.annotation_error_queue_not_found";
  static final String ERR_MSG_CFG_QUEUE_DUP = "message.annotation_error_queue_repeat";
  static final String ERR_MSG_QUEUE_NULL = "message.queue_error_null";

}
