/*
 * Copyright 2022 Dynatrace LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dynatrace.hash4j.hashing;

import static com.dynatrace.hash4j.testutils.TestUtils.hash128ToByteArray;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.dynatrace.hash4j.testutils.TestUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;

class Murmur3_128Test extends AbstractHashStream128Test {

  private static final List<Hasher128> HASHERS =
      Arrays.asList(Hashing.murmur3_128(), Hashing.murmur3_128(0xfc64a346));

  @Override
  protected List<Hasher128> getHashers() {
    return HASHERS;
  }

  /**
   * The C reference implementation does not define the hash value computation of byte sequences
   * longer than {@link Integer#MAX_VALUE} as it uses a native unsigned {@code int} for the length
   * of the byte array. This test verifies that a predefined byte sequence longer than {@link
   * Integer#MAX_VALUE} always results in the same hash value.
   */
  @Test
  public void testLongInput() {
    long len = 1L + Integer.MAX_VALUE;
    HashStream stream = Hashing.murmur3_128().hashStream();
    LongStream.range(0, len).forEach(i -> stream.putByte((byte) (i & 0xFF)));
    byte[] hashValueBytes = hash128ToByteArray(stream.get());
    byte[] expected = TestUtils.hexStringToByteArray("4b32a2e0240ee13e2b5a84668f916ce2");
    assertArrayEquals(expected, hashValueBytes);
  }

  @Override
  protected List<ReferenceTestRecord128> getReferenceTestRecords() {
    List<ReferenceTestRecord128> referenceTestRecords = new ArrayList<>();
    for (Murmur3_128ReferenceData.ReferenceRecord r : Murmur3_128ReferenceData.get()) {
      referenceTestRecords.add(
          new ReferenceTestRecord128(Hashing.murmur3_128(), r.getData(), r.getHash0()));
      referenceTestRecords.add(
          new ReferenceTestRecord128(Hashing.murmur3_128(r.getSeed()), r.getData(), r.getHash1()));
    }
    return referenceTestRecords;
  }
}
