package org.asosat.domain;

import javax.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.asosat.kernel.normal.conversion.ConversionService;
import org.asosat.kernel.util.LogFactory;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
@EnableAutoWeld
@AddPackages({ConversionService.class, Logger.class, LogFactory.class})
public class AppTest {

  @Inject
  ConversionService service;

  @Inject
  Logger logger;

  @Test
  public void test() {}

}
