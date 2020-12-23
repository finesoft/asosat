package org.asosat.ddd.storage.aliyun;

import org.corant.config.declarative.ConfigKeyItem;
import org.corant.config.declarative.ConfigKeyRoot;
import org.corant.config.declarative.DeclarativeConfig;
import org.corant.context.Qualifiers.NamedQualifierObjectManager.AbstractNamedObject;
import org.eclipse.microprofile.config.Config;

/**
 * 阿里云OSS配置项
 * @author don
 * @date 2020/12/16
 */
@ConfigKeyRoot("cloud.aliyun.oss")
public class OSSConfig extends AbstractNamedObject implements DeclarativeConfig {

  private static final long serialVersionUID = -6648331753827582002L;

  @ConfigKeyItem(defaultValue = "false")
  protected boolean enable;

  @ConfigKeyItem
  protected String endpoint;

  @ConfigKeyItem
  protected String accessKeyId;

  @ConfigKeyItem
  protected String accessKeySecret;

  /** 支持多个用逗号分隔 */
  @ConfigKeyItem
  protected String bucket;

  public boolean isEnable() {
    return enable;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getAccessKeyId() {
    return accessKeyId;
  }

  public String getAccessKeySecret() {
    return accessKeySecret;
  }

  public String getBucket() {
    return bucket;
  }

  @Override
  public void onPostConstruct(Config config, String key) {
    setName(key);
  }
}
