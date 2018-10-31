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
import static org.asosat.kernel.util.MyStrUtils.split;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.UriParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asosat.kernel.util.VFSUtils;

/**
 * @author bingo 上午11:59:38
 *
 */
public class MultiClassPathFiles {

  private static Logger logger = LogManager.getLogger(MultiClassPathFiles.class.getName());
  static final String JAR_URL_PREFIX = "jar:";
  static final String FILE_URL_PREFIX = "file:";
  static final String JAR_URL_SEPARATOR = "!/";
  static final Map<String, String> SUFFIX_SCHEMA_MAP = new HashMap<>();

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

  public static List<FileObject> select(FileObject fo, FileSelector fs) {
    List<FileObject> list = new ArrayList<>();
    final DefaultFileSelectorInfo info = new DefaultFileSelectorInfo();
    int level = 0;
    info.baseFolder(fo).depth(level).file(fo);
    traverse(fo, level, (f, l) -> {
      try {
        info.file(f).depth(l);
        boolean traverse = fs.traverseDescendents(info);
        if (traverse && fs.includeFile(info)) {
          list.add(f);
        }
        return traverse;
      } catch (Exception e) {
        logger.warn(String.format("Visit %s occurred error, the error message is %s",
            f.getPublicURIString(), e.getMessage()));
        return false;
      }
    });
    return list;
  }

  public static List<FileObject> select(String classPath, FileSelector fs) {
    final List<FileObject> result = new ArrayList<>();
    final String path = classPath == null ? "" : classPath.replaceAll("\\.", "/");
    ClassLoader classLoader = defaultClassLoader();
    try {
      Enumeration<URL> currPathUrls = classLoader != null ? classLoader.getResources(path)
          : ClassLoader.getSystemResources(path);
      while (currPathUrls.hasMoreElements()) {
        URL u = currPathUrls.nextElement();
        String us = u.toExternalForm();
        logger.info(String.format(
            "Select file from class path use [defaultClassLoader().getResources('%s')], url is %s.",
            path, us));
        for (FileObject sf : select(u, fs)) {
          if (!result.contains(sf)) {
            result.add(sf);
          }
        }
        // append other resources META-INF...
        String usx = us.substring(0, us.lastIndexOf(path));
        usx = usx.endsWith(JAR_URL_SEPARATOR) ? usx : usx + JAR_URL_SEPARATOR;
        for (FileObject sf : select(new URL(usx), fs)) {
          if (!result.contains(sf)) {
            result.add(sf);
          }
        }
      }
    } catch (IOException e) {
      logger.warn(() -> String.format(
          "Select file from class path use [defaultClassLoader().getResources('%s')], the error message is %s.",
          path, e.getMessage()));
    }
    if ("".equals(path)) {
      traverseSelect(classLoader, fs, result);
    }
    return result;
  }

  public static List<FileObject> select(URL url, FileSelector fs) {
    List<FileObject> list = new ArrayList<>();
    try {
      list = select(VFSUtils.getFileSystemManager().resolveFile(url), fs);
    } catch (FileSystemException e) {
      logger.warn(
          String.format("Select %s occurred error, the error message is %s", url, e.getMessage()));
    }
    return list;
  }

  public static FileObject single(String classPath, FileSelector fs) {
    List<FileObject> list = select(classPath, fs);
    if (list.size() == 1) {
      return list.get(0);
    }
    logger.warn(() -> String.format("Found multi files with path [%s]", classPath));
    throw new IllegalArgumentException("Single file cannot be determined!");
  }

  @SuppressWarnings("resource")
  public static void traverse(FileObject fo, int level, Visitor... visitors) {
    if (fo == null) {
      return;
    }
    FileObject file = fo;
    List<Visitor> visitorList = new ArrayList<>();
    for (Visitor visitor : visitors) {
      if (visitor.visit(file, level)) {
        visitorList.add(visitor);
      }
    }
    if (visitorList.isEmpty()) {
      return;
    }
    try {
      FileSystemManager vfs = VFS.getManager();
      if (!file.getType().hasChildren()) {
        String scheme = schemeWithSuffix(file.getName().getBaseName());
        if (scheme != null) {
          if ("zip".equalsIgnoreCase(scheme) || "jar".equalsIgnoreCase(scheme)) {
            file = vfs.createFileSystem(file);
          } else {
            FileName name = file.getName();
            String uri = scheme + ":" + name.getURI();
            if ("tgz".equalsIgnoreCase(scheme) || "tbz2".equalsIgnoreCase(scheme)) {
              if (!uri.endsWith(JAR_URL_SEPARATOR)) {
                uri = uri + JAR_URL_SEPARATOR;
              }
            }
            file = vfs.resolveFile(uri);
          }
        } else {
          return;
        }
      }
      if (file != null) {
        if (file.getType().hasChildren()) {
          Arrays.stream(file.getChildren()).forEach(child -> {
            int nextLevel = level + 1;
            traverse(child, nextLevel, visitorList.toArray(new Visitor[0]));
          });
        }
      }
    } catch (Exception e) {
      logger.warn(String.format("Visit %s occurred error, the error message is %s",
          file.getPublicURIString(), e.getMessage()));
    }
  }

  public static void traverseSelect(ClassLoader classLoader, FileSelector fs,
      List<FileObject> result) {
    if (classLoader instanceof URLClassLoader) {
      for (URL u : URLClassLoader.class.cast(classLoader).getURLs()) {
        try {
          for (FileObject fo : select(new URL(JAR_URL_PREFIX + u + JAR_URL_SEPARATOR), fs)) {
            if (!result.contains(fo)) {
              result.add(fo);
            }
          }
        } catch (MalformedURLException e) {
          logger.debug(() -> String.format(
              "Traverse select class path files with URLClassLoader error, the error message is %s.",
              e.getMessage()));
        }
      }
    }
    if (classLoader == ClassLoader.getSystemClassLoader()) {
      for (String cp : split(System.getProperty("java.class.path"),
          System.getProperty("path.separator"))) {
        String filePath = new File(cp).getAbsolutePath();
        try {
          for (FileObject fo : select(
              new URL(JAR_URL_PREFIX + FILE_URL_PREFIX + filePath + JAR_URL_SEPARATOR), fs)) {
            if (!result.contains(fo)) {
              result.add(fo);
            }
          }
        } catch (MalformedURLException e) {
          logger.debug(() -> String.format(
              "Traverse select class path files with SystemClassLoader, the error message is %s",
              e.getMessage()));
        }
      }
    }
    if (classLoader != null) {
      try {
        traverseSelect(classLoader.getParent(), fs, result);
      } catch (Exception ex) {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Traverse select class path files occur an error,cannot introspect jar files in parent ClassLoader since [%s] does not support 'getParent()': %s.",
              classLoader, ex));
        }
      }
    }
  }

  static String schemeWithPrefix(String name) {
    return UriParser.extractScheme(name);
  }

  static String schemeWithSuffix(String name) {
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

  @FunctionalInterface
  public static interface Visitor {
    boolean visit(FileObject fo, int level);
  }

  static class DefaultFileSelectorInfo implements FileSelectInfo {

    private FileObject baseFolder;
    private FileObject file;
    private int depth;

    public DefaultFileSelectorInfo baseFolder(final FileObject baseFolder) {
      this.baseFolder = baseFolder;
      return this;
    }

    public DefaultFileSelectorInfo depth(final int depth) {
      this.depth = depth;
      return this;
    }

    public DefaultFileSelectorInfo file(final FileObject file) {
      this.file = file;
      return this;
    }

    @Override
    public FileObject getBaseFolder() {
      return this.baseFolder;
    }

    @Override
    public int getDepth() {
      return this.depth;
    }

    @Override
    public FileObject getFile() {
      return this.file;
    }

    @Override
    public String toString() {
      return super.toString() + " [baseFolder=" + this.baseFolder + ", file=" + this.file
          + ", depth=" + this.depth + "]";
    }
  }

}
