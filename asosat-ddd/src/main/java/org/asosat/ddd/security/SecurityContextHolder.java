package org.asosat.ddd.security;

import static org.corant.kernel.util.Instances.resolve;
import java.util.Optional;
import org.asosat.shared.Participator;

/**
 * 获取当前用户信息 FIXME DON 之前代码在Participator里面，先剥离出来
 * 
 * @author don
 * @create 2019-08-13
 */
public class SecurityContextHolder {

  public static Participator currentOrg() {
    Optional<SecurityContextProducer> scp = resolve(SecurityContextProducer.class);
    return (scp.isPresent() ? scp.get().get() : DefaultSecurityContext.EMPTY_INST).getCurrentOrg();
  }

  public static Participator currentUser() {
    Optional<SecurityContextProducer> scp = resolve(SecurityContextProducer.class);
    return (scp.isPresent() ? scp.get().get() : DefaultSecurityContext.EMPTY_INST).getCurrentUser();
  }

  public static Long currentUserId() {
    Participator currentUser = currentUser();
    return currentUser == null ? null : currentUser.getId();
  }

  public static Long currentOrgId() {
    Participator currentOrg = currentOrg();
    return currentOrg == null ? null : currentOrg.getId();
  }
}
