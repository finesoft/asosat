package org.asosat.ddd.service;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoField.SECOND_OF_DAY;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * 单号生成
 * e.g. 01912169864
 * @author don
 * @date 2019-12-13
 */
@Transactional
@ApplicationScoped
public class NumberGeneration {

  private static final DateTimeFormatter FORMATTER = ofPattern("yyMMdd");

  private static final String PREFIX_KEY = NumberGeneration.class.getName() + ":";

  private static final Logger LOG = Logger.getLogger(NumberGeneration.class.getName());

  @Inject
  RedissonClient redisson;

  public Long nextUniqueNoOfDay(int businessCode, int initTailDigit) {
    ZonedDateTime now = ZonedDateTime.now();
    final String today = FORMATTER.format(now);
    final String dataKey = PREFIX_KEY + "data_" + businessCode + "_" + today; //e.g. className:data_1_191216
    final String digitKey = PREFIX_KEY + "digit_" + businessCode + "_" + today; //e.g. className:digit_1_191216
    LOG.fine("dataKey   " + dataKey);
    LOG.fine("digitKey  " + digitKey);
    RBlockingQueue<Integer> blockingQueue = redisson.getBlockingQueue(dataKey);
    if (blockingQueue.isEmpty()) {
      RLock rLock = redisson.getLock(digitKey + "_lock");//e.g. className:digit_1_191216_lock
      rLock.lock();
      try {
        if (blockingQueue.isEmpty()) {
          LOG.fine("blockingQueue is empty");
          RAtomicLong atomicLong = redisson.getAtomicLong(digitKey);
          int digit;
          if (atomicLong.isExists()) {
            digit = (int) atomicLong.getAndIncrement();
          } else {
            atomicLong.set(digit = initTailDigit);
          }
          List<Integer> data = new ArrayList<>();
          for (double i = Math.pow(10, digit - 1); i < Math.pow(10, digit); i++) {
            data.add((int) i);
          }
          Collections.shuffle(data);
          blockingQueue.addAll(data);

          ZonedDateTime tomorrow = now.plusDays(1).with(t -> t.with(SECOND_OF_DAY, 1));
          atomicLong.expireAt(tomorrow.toInstant().toEpochMilli());
          blockingQueue.expireAt(tomorrow.toInstant().toEpochMilli());
          LOG.fine("blockingQueue is done");
        }
      } finally {
        rLock.unlock();
      }
    }
    Integer currentNo = blockingQueue.remove();
    return Long.valueOf(businessCode + today + currentNo);
  }
}
