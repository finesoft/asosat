package org.asosat.thorntail;

import java.util.Locale;
import javax.inject.Inject;
import org.asosat.kernel.resource.EnumerationResource;
import org.asosat.kernel.resource.MessageResource;
import org.eclipse.microprofile.config.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.thorntail.test.ThorntailTestRunner;

/**
 * Unit test for simple App.
 */
@RunWith(ThorntailTestRunner.class)
public class ConfigTest {

  @Inject
  Config cfg;

  @Inject
  MessageResource mr;

  @Inject
  EnumerationResource er;

  @Test
  public void main() {
    this.cfg.getConfigSources();
    this.er.getAllEnumClass();
    this.mr.getMessage(Locale.getDefault(), "xxxx", new Object[] {"xx"});
  }
}
