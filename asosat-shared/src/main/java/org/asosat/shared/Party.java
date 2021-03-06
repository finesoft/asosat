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
package org.asosat.shared;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author bingo 下午7:22:18
 */
public interface Party extends Nameable {

  /**
   * 委托关系列表
   *
   * @param predicate 条件限定
   * @param includeHierarchy 是否罗列所有关系
   * @return
   */
  List<? extends Party> getEntrustingParties(Predicate<PartyAccountability> predicate,
      boolean includeHierarchy);

  /**
   * 根法人组织
   *
   * @return
   */
  Party getHierarchyParty();

  Long getId();

  /**
   * 责任关系列表
   *
   * @param predicate 条件限定
   * @param includeHierarchy 是否罗列所有关系
   * @return
   */
  List<? extends Party> getResponsibleParties(Predicate<PartyAccountability> predicate,
      boolean includeHierarchy);

  default Participator toParticipator() {
    return new Participator(getId(), getName());
  }

  /**
   * 法人
   *
   * @author bingo 2016年9月21日
   * @since
   */
  public interface Corporation extends Party {

    /**
     * 法人经营业务范围
     *
     * @return
     */
    default Set<?> getRealms() {
      return Collections.emptySet();
    }
  }

  /**
   * 用户
   *
   * @author bingo 2016年6月13日
   * @since
   */
  public interface Individual extends Party {
  }

  public interface PartyAccountability {

    /**
     * 委托方
     *
     * @return getEntrustingParty
     */
    Party getEntrustingParty();

    /**
     * 责任方
     *
     * @return
     */
    Party getResponsibleParty();

    /**
     * 责任类型
     *
     * @return getType
     */
    PartyAccountabilityType getType();

  }

  /**
   * @author bingo 下午7:23:43
   */
  public interface PartyAccountabilityType extends Nameable {

    /**
     * @param entrustingParty
     * @param entrustingPartyRole
     * @param responsibleParty
     * @param responsiblePartyRole
     * @return buildAccountability
     */
    PartyAccountability buildAccountability(final Party entrustingParty,
        final PartyRole entrustingPartyRole, final Party responsibleParty,
        final PartyRole responsiblePartyRole);

    /**
     * 是否含有该类型的委托责任关系
     *
     * @param entrustingParty
     * @param responsibleParty
     * @return
     */
    boolean hasConnection(final Party entrustingParty, final Party responsibleParty);

    /**
     * 检查是否可以建立委托责任关系
     *
     * @param entrustingParty
     * @param responsibleParty
     */
    void verifyConnection(final Party entrustingParty, final PartyRole entrustingPartyRole,
        final Party responsibleParty, final PartyRole responsiblePartyRole);
  }

  /**
   * 业务类型
   *
   * @author bingo 2016年9月20日
   * @since
   */
  public interface PartyRealm extends Nameable {

  }

  /**
   * 业务角色
   * <p>
   * corant-asosat-ddd
   *
   * @author bingo 下午3:54:56
   */
  public interface PartyRole extends Nameable {

  }
}
