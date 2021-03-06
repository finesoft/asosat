/*
 * Copyright (c) 2013-2018, Bingo.Chen (finesoft@gmail.com).
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
package org.asosat.ddd.application.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import org.asosat.ddd.security.SecurityContextHolder;
import org.asosat.shared.Participator;
import org.corant.suites.ddd.annotation.stereotype.InfrastructureServices;
import org.corant.suites.query.shared.spi.ParameterReviser;

/**
 * corant-asosat-ddd
 *
 * @author bingo 上午10:59:43
 */
@ApplicationScoped
@InfrastructureServices
public class SecurityContextParameterReviser implements ParameterReviser {

  @Override
  public Map<String, Object> get() {
    Map<String, Object> securityContext = new HashMap<>();
    Participator currentUser= SecurityContextHolder.currentUser();
    if (currentUser != null) {
      securityContext.putIfAbsent(Participator.CURRENT_USER_ID_KEY, currentUser.getId());
    }
    Participator currentOrg = SecurityContextHolder.currentOrg();
    if (currentOrg != null) {
      securityContext.putIfAbsent(Participator.CURRENT_ORG_ID_KEY, currentOrg.getId());
    }
    Set<String> currentUserRoles = SecurityContextHolder.currentUserRoles();
    if (currentUserRoles != null) {
      securityContext.putIfAbsent("_currentUserRoles", currentUserRoles);
    }
    return securityContext;
  }
}
