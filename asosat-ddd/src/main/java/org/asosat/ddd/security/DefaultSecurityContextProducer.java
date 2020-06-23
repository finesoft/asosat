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
package org.asosat.ddd.security;

import static org.corant.shared.util.Conversions.toLong;
import static org.corant.shared.util.Objects.defaultObject;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.Context;
import javax.inject.Inject;
import org.asosat.shared.Participator;
import org.corant.shared.util.Conversions;
import org.corant.suites.cdi.Instances;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.weld.manager.api.WeldManager;

/**
 * corant-asosat-ddd
 *
 * @author bingo 上午11:34:06
 *
 */
@ApplicationScoped
public class DefaultSecurityContextProducer implements SecurityContextProducer {

  @Inject
  WeldManager wm;

  @Override
  public DefaultSecurityContext get() {
    if (wm.getActiveContexts().stream().map(Context::getScope)
        .anyMatch(c -> c.equals(RequestScoped.class))) {
      Optional<JsonWebToken> jwto = Instances.find(JsonWebToken.class);
      if (jwto.isPresent()) {
        JsonWebToken jwt = jwto.get();
        Participator currentUser = null;
        Participator currentOrg = null;
        if (jwt.containsClaim("userId")) {
          Long userId = toLong(jwt.getClaim("userId"));
          String userName = defaultObject(Conversions.toString(jwt.getClaim("name")),
              Conversions.toString(jwt.getClaim("preferred_username"))); // from2019/12/19
          currentUser = new Participator(userId, userName);
        }
        if (jwt.containsClaim("orgId")) {
          Long orgId = toLong(jwt.getClaim("orgId"));
          String orgName = Conversions.toString(jwt.getClaim("orgName"));
          currentOrg = new Participator(orgId, orgName);
        }
        return new DefaultSecurityContext(jwt.getRawToken(), null, jwt, currentUser, currentOrg,
            true, "MP-JWT", jwt.getGroups());
      }
    }
    return null;
  }

}
