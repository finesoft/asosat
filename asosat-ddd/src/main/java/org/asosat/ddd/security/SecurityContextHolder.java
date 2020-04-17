package org.asosat.ddd.security;

import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.ObjectUtils.defaultObject;
import static org.corant.suites.cdi.Instances.find;

import java.util.Optional;
import java.util.Set;
import org.asosat.shared.Participator;

/**
 * 获取当前用户信息
 * @author don
 * @create 2019-08-13
 */
public class SecurityContextHolder {

  @Deprecated
  private static final ThreadLocal<DefaultSecurityContext> SC = new ThreadLocal<>();

  public static Participator currentOrg() {
    return resolveDefaultSecurityContext().getCurrentOrg();
  }

  public static Participator currentUser() {
    return resolveDefaultSecurityContext().getCurrentUser();
  }

  public static Set<String> currentUserRoles() {
    return resolveDefaultSecurityContext().getUserRoles();
  }

  public static DefaultSecurityContext resolveDefaultSecurityContext() {
    DefaultSecurityContext ctx = null;
    Optional<SecurityContextProducer> optional = find(SecurityContextProducer.class);
    SecurityContextProducer securityContextProducer = optional.orElse(null);
    if (securityContextProducer != null) {
      ctx = securityContextProducer.get();
    }
    if (ctx == null) {
      ctx = defaultObject(SC.get(), DefaultSecurityContext.EMPTY_INST);
    }
    return ctx;
  }

  @Deprecated //FIXME DON 临时用
  public static void propagateSecurityContext(Long userId, String userName, Long orgId, String orgName) {
    propagateSecurityContext(new Participator(userId, userName), new Participator(orgId, orgName));
  }

  @Deprecated //FIXME DON 临时用
  public static void propagateSecurityContext(Participator user, Participator org) {
    SC.set(new DefaultSecurityContext(null, null, null, shouldNotNull(user), shouldNotNull(org), true, "MP-JWT", null));
  }
}
