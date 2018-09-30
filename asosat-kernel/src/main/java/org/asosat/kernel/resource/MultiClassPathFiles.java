/*
 * Copyright (c) 2013-2018. BIN.CHEN
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
package org.asosat.kernel.resource;

import static org.asosat.kernel.util.MyClsUtils.defaultClassLoader;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemManager;
import org.asosat.kernel.util.VFSUtils;

/**
 * @author bingo 上午11:59:38
 *
 */
public class MultiClassPathFiles {

  private final static Map<String, String> SUFFIX_SCHEMA_MAP = new HashMap<>();

  private static Logger logger = Logger.getLogger(MultiClassPathFiles.class.getName());

  static {
    SUFFIX_SCHEMA_MAP.put("zip", "zip");
    SUFFIX_SCHEMA_MAP.put("jar", "jar");
    SUFFIX_SCHEMA_MAP.put("war", "jar");
    SUFFIX_SCHEMA_MAP.put("car", "jar");
    SUFFIX_SCHEMA_MAP.put("tgz", "tgz");
    SUFFIX_SCHEMA_MAP.put("tbz2", "tbz2");
    SUFFIX_SCHEMA_MAP.put("tar.gz", "tgz");
    SUFFIX_SCHEMA_MAP.put("tar.bz2", "tbz2");
    SUFFIX_SCHEMA_MAP.put("tar", "tar");
    SUFFIX_SCHEMA_MAP.put("gz", "gz");
    SUFFIX_SCHEMA_MAP.put("gzip", "gz");
    SUFFIX_SCHEMA_MAP.put("bz2", "bz2");
  }


  public static FileObject get(String path) {
    Map<String, FileObject> selectFiles = select(VFSUtils.buildSelector(path));
    if (selectFiles.isEmpty()) {
      logger.severe(() -> String.format("Can not found file with path [%s]", path));
      return null;
    } else {
      if (selectFiles.size() > 1) {
        logger.warning(() -> String.format("Found multi files with path [%s]", path));
        throw new IllegalArgumentException("Single file cannot be determined!");
      }
      return selectFiles.values().iterator().next();
    }
  }

  public static Map<String, FileObject> select(FileSelector fs) {
    // FIXME the name is uniqueness??
    final Map<String, FileObject> combined = new ConcurrentHashMap<>();
    try {
      FileSystemManager fsm = VFSUtils.getFileSystemManager();
      Enumeration<URL> currPathUrls = defaultClassLoader().getResources("");// ClassLoader.getSystemResources("")
      while (currPathUrls.hasMoreElements()) {
        URL u = currPathUrls.nextElement();
        for (FileObject fo : fsm.resolveFile(u).findFiles(fs)) {
          combined.computeIfAbsent(fo.getName().getPathDecoded(), (k) -> fo);
        }
      }
      String[] classPaths = System.getProperty("java.class.path").split(";");
      for (String pe : classPaths) {
        String schema = detectScheme(fsm.resolveFile(pe).getName().getBaseName());
        if (schema != null) {
          for (FileObject fo : fsm.resolveFile(schema + ":" + pe).findFiles(fs)) {
            combined.computeIfAbsent(fo.getName().getPathDecoded(), (k) -> fo);
          }
        }
      }
    } catch (IOException e) {
      logger.warning(() -> String.format(
          "Select class path files occur an error, the error message is %s", e.getMessage()));
    }
    return combined;
  }

  public static Collection<FileObject> select(String path) {
    return select(VFSUtils.buildSelector(path)).values();
  }

  private static String detectScheme(String name) {
    String lcName = name.toLowerCase();
    int dotPos = lcName.lastIndexOf('.');
    if (dotPos > 0 && dotPos < lcName.length()) {
      int tarPos = lcName.lastIndexOf(".tar.");
      String suffix = tarPos > 0 ? lcName.substring(tarPos + 1) : lcName.substring(dotPos + 1);
      return SUFFIX_SCHEMA_MAP.get(suffix);
    }
    return null;
  }

  private MultiClassPathFiles() {}

}
