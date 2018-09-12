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

import java.util.Arrays;
import org.asosat.kernel.exception.KernelRuntimeException;

/**
 * @author bingo 2015年3月23日
 */
public class BitUtils {

  private BitUtils() {
    super();
  }

  public static BitArray newBitArray(byte[] bytes) {
    return new BitArray(bytes);
  }

  public static BitArray newBitArray(int size, boolean in) {
    return new BitArray(size, in);
  }

  public static class BitArray {

    private byte[] array = new byte[0];

    private int size = 0;

    public BitArray(byte[] bytes) {
      if (bytes != null) {
        int len = bytes.length;
        this.array = Arrays.copyOf(bytes, len);
        this.size = len << 3;
      }
    }

    public BitArray(int size, boolean in) {
      if (size < 1) {
        throw new KernelRuntimeException("The size must be greater then 0 zero!");
      }
      this.array = new byte[(size >> 3) + ((size & 7) == 0 ? 0 : 1)];
      for (int i = 0; i < size; i++) {
        this.setBit(i, in);
      }
      this.size = size;
    }

    public boolean getBit(int pos) {
      return (this.array[(pos >> 3)] & (1 << (pos & 7))) != 0;
    }

    public byte[] getBytes() {
      return Arrays.copyOf(this.array, this.array.length);
    }

    public int getInitSize() {
      return this.size;
    }

    public void setBit(int pos, boolean b) {
      byte b8 = this.array[pos >> 3];
      byte posBit = (byte) (1 << (pos & 7));
      if (b) {
        b8 |= posBit;
      } else {
        b8 &= (255 - posBit);
      }
      this.array[pos >> 3] = b8;
    }
  }
}
