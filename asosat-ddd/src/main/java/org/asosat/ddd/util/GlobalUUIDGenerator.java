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
package org.asosat.ddd.util;

import static org.corant.shared.util.Strings.isNotBlank;
import static org.eclipse.microprofile.config.ConfigProvider.getConfig;
import java.io.Serializable;
import java.time.Instant;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.corant.config.Configs;
import org.corant.shared.util.Identifiers.GeneralSnowflakeUUIDGenerator;
import org.corant.shared.util.Identifiers.SnowflakeD5W5S12UUIDGenerator;
import org.corant.shared.util.Identifiers.SnowflakeIpv4HostUUIDGenerator;
import org.corant.shared.util.Identifiers.SnowflakeW10S12UUIDGenerator;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * asosat-ddd
 *
 * @author bingo 上午11:18:05
 *
 */
public class GlobalUUIDGenerator {

  public static final String IG_SF_WK_IP = "identifier.generator.snowflake.worker-ip";
  public static final String IG_SF_WK_ID = "identifier.generator.snowflake.worker-id";
  public static final String IG_SF_DC_ID = "identifier.generator.snowflake.datacenter-id";
  public static final String IG_SF_DL_TM = "identifier.generator.snowflake.delayed-timing";
  public static final String IG_SF_UP_TM = "identifier.generator.snowflake.use-persistence-timer";

  static Logger logger = Logger.getLogger(GlobalUUIDGenerator.class.getName());
  static final GeneralSnowflakeUUIDGenerator generator;
  static final int dataCenterId;
  static final int workerId;
  static final String ip = Configs.getValue(IG_SF_WK_IP, String.class);
  static final boolean usePst = Configs.getValue(IG_SF_UP_TM, Boolean.class, Boolean.TRUE);
  static final long delayedTiming = Configs.getValue(IG_SF_DL_TM, Long.class, 16000L);
  static final boolean useSec;
  static final Supplier<Long> specTimeGenerator;

  static {
    dataCenterId = getConfig().getOptionalValue(IG_SF_DC_ID, Integer.class).orElse(-1);
    workerId = getConfig().getOptionalValue(IG_SF_WK_ID, Integer.class).orElse(-1);
    if (workerId >= 0) {
      if (dataCenterId >= 0) {
        generator = new SnowflakeD5W5S12UUIDGenerator(dataCenterId, workerId, delayedTiming);
      } else {
        generator = new SnowflakeW10S12UUIDGenerator(workerId, delayedTiming);
      }
      useSec = false;
    } else if (isNotBlank(ip)) {
      generator = new SnowflakeIpv4HostUUIDGenerator(ip, delayedTiming);
      useSec = true;
    } else {
      generator = new SnowflakeIpv4HostUUIDGenerator(delayedTiming);
      useSec = true;
    }
    specTimeGenerator =
        () -> (useSec ? Instant.now().getEpochSecond() : Instant.now().toEpochMilli());
    logger.info(() -> String.format("Global id generator Use %s.", generator.description()));
  }

  public static Long generate() {
    return generator.generate(specTimeGenerator);
  }

  public static class GlobalHibernateUUIDGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object)
        throws HibernateException {
      return GlobalUUIDGenerator.generate();
    }

  }
}
