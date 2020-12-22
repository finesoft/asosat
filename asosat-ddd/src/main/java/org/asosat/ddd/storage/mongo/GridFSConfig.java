package org.asosat.ddd.storage.mongo;

import org.corant.config.declarative.ConfigKeyItem;
import org.corant.config.declarative.ConfigKeyRoot;
import org.corant.config.declarative.DeclarativeConfig;
import org.corant.context.Qualifiers.NamedQualifierObjectManager.AbstractNamedObject;
import org.eclipse.microprofile.config.Config;

/**
 * @author don
 * @date 2020/12/15
 */
@ConfigKeyRoot("storage.gridfs")
public class GridFSConfig extends AbstractNamedObject implements DeclarativeConfig {

  private static final long serialVersionUID = 3310587859441223727L;

  @ConfigKeyItem(defaultValue = "true")
  protected boolean enable;

  @ConfigKeyItem(value = "database-bucket")
  protected String qualifier;

  public String getQualifier() {
    return qualifier;
  }

  public boolean isEnable() {
    return enable;
  }

  @Override
  public void onPostConstruct(Config config, String key) {
    setName(key);
  }
}
