package org.asosat.ddd.storage;

import org.asosat.shared.FileRelevance;
import org.corant.shared.util.Resources.Resource;

public interface StorageFile extends Resource {

  String getId();

  long getCreatedTime();

  long getLength();

  String getContentType();

  /** 获取实际处理类 */
  <T> T unwrap();

  default FileRelevance toFileRelevance() {
    return new FileRelevance(getId(), getName());
  }
}