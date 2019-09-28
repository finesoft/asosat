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

import static org.corant.kernel.util.Instances.resolveAccept;
import static org.corant.kernel.util.Instances.resolveApply;
import static org.corant.shared.util.Assertions.shouldBeTrue;
import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.ConversionUtils.toLong;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.corant.shared.exception.CorantRuntimeException;
import org.corant.shared.util.Resources.Resource;

/**
 * asosat-ddd
 * @author bingo 上午10:36:11
 */
public interface StorageService {

    static void get(Long id) {
        resolveAccept(GridFSStorageService.class, t -> t.removeFile(shouldNotNull(id)));
    }

    static Long store(Resource resource) {
        try (InputStream is = shouldNotNull(resource).openStream()) {
            return resolveApply(GridFSStorageService.class,
                    t -> t.putFile(is, resource.getLocation(), resource.getMetadata()));
        } catch (IOException e) {
            throw new CorantRuntimeException(e);
        }
    }

    StorageFile getFile(Long id);

    Long putFile(InputStream is, String filename, Map<String, Object> metadata);

    void removeFile(Long id);

    interface StorageFile extends Resource {

        Long getId();

        long getCreatedTime();

        long getLength();

        /**获取实际处理类*/
        <T> T unwrap();
    }
}
