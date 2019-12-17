package org.asosat.ddd.service;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.corant.shared.util.Assertions.shouldBeTrue;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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

  private static final DateTimeFormatter DAILY_FMT = ofPattern("yyMMdd");

  private static final DateTimeFormatter YEAR_FMT = ofPattern("yy");

  private static final String PREFIX_KEY = NumberGeneration.class.getName() + ":";

  private static final Logger LOGGER = Logger.getLogger(NumberGeneration.class.getName());

  @Inject
  RedissonClient redisson;

  /**
   * 每天随机唯一号  业务码+年月日+随机码
   * @param businessCode  业务码
   * @param initTailDigit 随机码初始位数
   * @return
   */
  public String nextRandomUniqueNoOfDaily(int businessCode, int initTailDigit) {
    shouldBeTrue(businessCode > 0 && initTailDigit > 0);
    ZonedDateTime now = ZonedDateTime.now();
    final String today = DAILY_FMT.format(now);
    final String dataKey = PREFIX_KEY + "randomUniqueNoOfDaily_data_" + businessCode + "_" + today; //e.g. className:data_1_191216
    final String digitKey = PREFIX_KEY + "randomUniqueNoOfDaily_digit_" + businessCode + "_" + today; //e.g. className:digit_1_191216
    RBlockingQueue<Integer> blockingQueue = redisson.getBlockingQueue(dataKey);
    if (blockingQueue.isEmpty()) {
      RLock rLock = redisson.getLock(digitKey + "_lock");//e.g. className:digit_1_191216_lock
      rLock.lock();
      try {
        if (blockingQueue.isEmpty()) {
          LOGGER.fine(() -> "blockingQueue is empty");
          RAtomicLong atomicLong = redisson.getAtomicLong(digitKey);
          int digit;
          if (atomicLong.isExists()) {
            digit = (int) atomicLong.incrementAndGet();
          } else {
            atomicLong.set(digit = initTailDigit);
          }
          List<Integer> data = new ArrayList<>();
          for (double i = Math.pow(10, digit - 1); i < Math.pow(10, digit); i++) {
            data.add((int) i);
          }
          Collections.shuffle(data);
          blockingQueue.addAll(data);

          ZonedDateTime tomorrow = now.plusDays(1).withHour(1).withMinute(0);
          atomicLong.expireAt(tomorrow.toInstant().toEpochMilli());
          blockingQueue.expireAt(tomorrow.toInstant().toEpochMilli());
          LOGGER.fine(() -> "blockingQueue is done");
        }
      } finally {
        rLock.unlock();
      }
    }
    Integer currentNo = blockingQueue.remove();
    return businessCode + today + currentNo;
  }

  /**
   * 每年递增顺序号 业务符号+年+顺序号
   * @param fixedCode
   * @param initTailDigit
   * @return
   */
  public String incrementNoOfYear(String fixedCode, int initTailDigit) {
    shouldBeTrue(isNotBlank(fixedCode) && initTailDigit > 0);
    ZonedDateTime now = ZonedDateTime.now();
    Function<String, String> keyGen = year -> PREFIX_KEY + "incrementNoOfYear_" + fixedCode + "_" + year; //e.g. className:increment_TNC_19

    final String thisYear = YEAR_FMT.format(now), thisKey = keyGen.apply(thisYear);

    RAtomicLong atomicLong = redisson.getAtomicLong(thisKey);
    long currentNo = atomicLong.incrementAndGet();
    if (currentNo == 1) {
      String lastYear = YEAR_FMT.format(now.minusYears(1)), lastKey = keyGen.apply(lastYear);
      redisson.getKeys().expire(lastKey, 1, TimeUnit.SECONDS);
    }
    return fixedCode + thisYear + leftPad(String.valueOf(currentNo), initTailDigit, '0');
  }
}
