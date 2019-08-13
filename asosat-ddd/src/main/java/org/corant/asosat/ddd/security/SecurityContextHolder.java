package org.corant.asosat.ddd.security;

import org.corant.asosat.ddd.domain.shared.Participator;

import static org.corant.kernel.util.Instances.resolve;

import java.util.Optional;

/**
 * 获取当前用户信息
 *
 * @author don
 * @create 2019-08-13
 */
public class SecurityContextHolder {

    public static Participator currentOrg() {
        Optional<SecurityContextProducer> scp = resolve(SecurityContextProducer.class);
        return (scp.isPresent() ? scp.get().get() : DefaultSecurityContext.EMPTY_INST).getCurrentUser();
    }

    public static Participator currentUser() {
        Optional<SecurityContextProducer> scp = resolve(SecurityContextProducer.class);
        return (scp.isPresent() ? scp.get().get() : DefaultSecurityContext.EMPTY_INST).getCurrentOrg();
    }
}
