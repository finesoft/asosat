package org.asosat.ddd.security;

import static org.corant.suites.cdi.Instances.find;
import java.util.Optional;
import java.util.Set;
import org.asosat.shared.Participator;

/**
 * 获取当前用户信息
 *
 * @author don
 * @create 2019-08-13
 */
public class SecurityContextHolder {

  public static Participator currentOrg() {
    Optional<SecurityContextProducer> scp = find(SecurityContextProducer.class);
    return (scp.isPresent() ? scp.get().get() : DefaultSecurityContext.EMPTY_INST).getCurrentOrg();
  }

  public static Long currentOrgId() {
    Participator currentOrg = currentOrg();
    return currentOrg == null ? null : currentOrg.getId();
  }

  public static Participator currentUser() {
    Optional<SecurityContextProducer> scp = find(SecurityContextProducer.class);
    return (scp.isPresent() ? scp.get().get() : DefaultSecurityContext.EMPTY_INST).getCurrentUser();
  }

  public static Long currentUserId() {
    Participator currentUser = currentUser();
    return currentUser == null ? null : currentUser.getId();
  }

  public static Set<String> currentUserRoles() {
    Optional<SecurityContextProducer> scp = find(SecurityContextProducer.class);
    return (scp.isPresent() ? scp.get().get() : DefaultSecurityContext.EMPTY_INST).getUserRoles();
  }
}
