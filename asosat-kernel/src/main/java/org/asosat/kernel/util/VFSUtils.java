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
package org.asosat.kernel.util;

import static org.asosat.kernel.util.MyObjUtils.isNonNull;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.asosat.kernel.exception.KernelRuntimeException;

/**
 * @author bingo 下午7:29:35
 *
 */
public class VFSUtils {

  private static FileSystemManager fileSystemManager;

  static {
    try {
      fileSystemManager = VFS.getManager();
    } catch (FileSystemException e) {
      e.printStackTrace();
    }
  }

  public static SimpleFileSelector buildSelector(Predicate<FileSelectInfo> p) {
    return fileInfo -> fileInfo != null && p.test(fileInfo);
  }

  public static SimpleFileSelector buildSelector(String classPath) {
    return fileInfo -> isNonNull(fileInfo)
        && fileInfo.getFile().getURL().toExternalForm().contains(classPath);
  }

  public static FileSystemManager getFileSystemManager() {
    return fileSystemManager;
  }

  public static String getPathFileName(FileObject fo) {
    try {
      return fo == null ? null : fo.getName().getPathDecoded();
    } catch (Exception e) {
      return null;
    }
  }

  public static void readTxtFile(String filePath, BiConsumer<String, Long> consumer) {
    AtomicLong lineNum = new AtomicLong();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
        Stream<String> stream = reader.lines();) {
      stream.forEach(line -> consumer.accept(line, lineNum.incrementAndGet()));
    } catch (IOException e) {
      throw new KernelRuntimeException(e);
    }
  }

  public static void saveTxtFile(String filePath, Collection<?> lines) throws IOException {
    String lineSpr = String.valueOf((char) Character.LINE_SEPARATOR);
    try (BufferedWriter writer = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(new File(filePath), true)))) {
      for (Object obj : lines) {
        writer.append(obj == null ? StringUtils.EMPTY : obj.toString());
        writer.append(lineSpr);
      }
    }
  }

  private VFSUtils() {
    super();
  }

  public static class MultiPatternFileSelector implements FileSelector {

    private List<Pattern> patterns = new ArrayList<>();

    private boolean andConjunction = false;

    public MultiPatternFileSelector(final int flags, String... regexs) {
      if (regexs.length == 0) {
        throw new IllegalArgumentException();
      }
      for (String r : regexs) {
        this.patterns.add(Pattern.compile(r, flags));
      }
    }

    public MultiPatternFileSelector(Pattern... patterns) {
      if (patterns.length == 0) {
        throw new IllegalArgumentException();
      }
      for (Pattern p : patterns) {
        this.patterns.add(p);
      }
    }

    @Override
    public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
      if (this.andConjunction) {
        return this.patterns.stream()
            .allMatch(p -> p.matcher(fileInfo.getFile().getName().getPath()).matches());
      } else {
        return this.patterns.stream()
            .anyMatch(p -> p.matcher(fileInfo.getFile().getName().getPath()).matches());
      }
    }

    @Override
    public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception {
      return true;
    }

    public MultiPatternFileSelector withConjunction(boolean and) {
      this.andConjunction = and;
      return this;
    }

  }

  @FunctionalInterface
  public static interface SimpleFileSelector extends FileSelector {
    @Override
    default boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception {
      return true;
    }
  }

}
