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
 * asosat-core-ddds <br/>
 *
 * @author bingo 2018年3月28日
 * @since
 */
package org.asosat.domain.unitofwork;

import org.asosat.kernel.resource.GlobalMessageCodes;

class PkgMsgCds implements GlobalMessageCodes {

  static final String ERR_UOW_TRANS = "transactionUnitOfWorks.transaction_error";
  static final String ERR_UOW_NOT_ACT = "unitOfWorks_error_not_activated";
  static final String ERR_UOW_CREATE = "transactionUnitOfWorks.create_error";

}