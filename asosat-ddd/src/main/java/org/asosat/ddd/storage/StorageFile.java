package org.asosat.ddd.storage;

import org.corant.shared.util.Resources.Resource;

public interface StorageFile extends Resource {

  long getCreatedTime();

  String getId();

  long getLength();

  String getContentType();

  /** 获取实际处理类 */
  <T> T unwrap();
}