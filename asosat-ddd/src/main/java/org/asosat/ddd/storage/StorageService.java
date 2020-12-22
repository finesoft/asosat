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
package org.asosat.ddd.storage;

import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.Strings.defaultString;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.corant.context.Instances;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.shared.util.Resources.Resource;

/**
 * asosat-ddd
 * @author bingo 上午10:36:11
 */
public interface StorageService {

  static StorageFile get(String id) {
    return Instances.resolveApply(StorageService.class, t -> t.getFile(shouldNotNull(id)));
  }

  static String store(Resource resource) {
    return Instances.resolveApply(StorageService.class, t -> t.putResource(resource));
  }


  String putFile(InputStream is, String filename, Map<String, Object> metadata);

  StorageFile getFile(String id);

  void removeFile(String id);

  default String putResource(Resource resource) {
    shouldNotNull(resource);
    try (InputStream is = resource.openStream()) {
      String filename = defaultString(resource.getName(), defaultString(resource.getLocation()));
      return putFile(is, filename, resource.getMetadata());// filename should not null
    } catch (IOException e) {
      throw new CorantRuntimeException(e);
    }
  }
}
