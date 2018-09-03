package org.asosat.kernel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.asosat.kernel.util.IdentifierGenerators;
import org.asosat.kernel.util.IdentifierGenerators.SnowflakeBufferUUIDGenerator;
import org.asosat.kernel.util.IdentifierGenerators.SnowflakeUUIDGenerator;

public class IdentifierGeneratorsTest {

  public static void main(String... strings) throws InterruptedException {
    int tn = 2, n = 9876;
    final long[][] arr = new long[tn][n];
    ExecutorService es = Executors.newFixedThreadPool(tn);
    final CountDownLatch latch = new CountDownLatch(tn);
    for (int d = 0; d < tn; d++) {
      final int t = d;
      es.submit(() -> {
        for (int i = 0; i < n; i++) {
          arr[t][i] =
              IdentifierGenerators.snowflakeBufferUUID(t, true, () -> System.currentTimeMillis());
        }
        latch.countDown();
      });
    }
    latch.await();
    Set<Long> set = new HashSet<>(n);
    Map<Long, List<Long>> tmp = new LinkedHashMap<>();
    for (long[] ar : arr) {
      for (long a : ar) {
        set.add(a);
        long time = SnowflakeBufferUUIDGenerator.parseGeningInstant(a).toEpochMilli();
        long woid = SnowflakeBufferUUIDGenerator.parseGeningWorkerId(a);
        long seq = SnowflakeBufferUUIDGenerator.parseGeningSequence(a);
        long dcid = SnowflakeUUIDGenerator.parseGeningDataCenterId(a);
        tmp.computeIfAbsent(time, (k) -> new ArrayList<>()).add(seq);
        System.out
            .println(a + "\tdcid" + dcid + "\twid:" + woid + "\ttime:" + time + "\tseq:" + seq);
      }
    }
    System.out.println("--------------------------------------------------");
    tmp.forEach((k, v) -> {
      v.stream().sorted().forEach((seq) -> System.out.println("time:" + k + "\tseq:" + seq));
    });
    es.shutdown();
    if (set.size() == n) {
      throw new IllegalStateException();
    }
  }

  public static void maina(String... strings) {
    long id = 335970863700521431l;
    System.out.println(SnowflakeBufferUUIDGenerator.parseGeningInstant(id));
    System.out.println(SnowflakeBufferUUIDGenerator.parseGeningSequence(id));
    System.out.println(SnowflakeBufferUUIDGenerator.parseGeningWorkerId(id));
  }
}
