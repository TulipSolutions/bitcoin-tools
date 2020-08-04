// Copyright 2020 Tulip Solutions B.V.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package nl.tulipsolutions.byteutils

import org.assertj.core.api.Assertions
import org.junit.Test

class HexTest {
    val hexTestMap = mapOf(
        "00000000" to byteArrayOf(0, 0, 0, 0),
        "11111111" to byteArrayOf(17, 17, 17, 17),
        "01010101" to byteArrayOf(1, 1, 1, 1),
        "12121212" to byteArrayOf(18, 18, 18, 18),
        "FFFFFFFF" to byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte()),
        "F0F0F0F0" to byteArrayOf(0xf0.toByte(), 0xf0.toByte(), 0xf0.toByte(), 0xf0.toByte()),
        "F1F1F1F1" to byteArrayOf(0xf1.toByte(), 0xf1.toByte(), 0xf1.toByte(), 0xf1.toByte()),
        // Mix casing first lower case
        "f1F1f1F1" to byteArrayOf(0xf1.toByte(), 0xf1.toByte(), 0xf1.toByte(), 0xf1.toByte()),
        // secondary lower case
        "1f1F1f1F" to byteArrayOf(0x1f.toByte(), 0x1f.toByte(), 0x1f.toByte(), 0x1f.toByte())
    )

    @Test
    fun testHexToByteArray() {
        hexTestMap.forEach { (s: String, bytes: ByteArray) ->
            Assertions.assertThat(
                Hex.decode(s)
            ).isEqualTo(bytes)
        }
    }

    @Test
    fun testByteArrayToHex() {
        hexTestMap.forEach { (s: String, bytes: ByteArray) ->
            Assertions.assertThat(
                Hex.toHexString(bytes)
            ).isEqualTo(s.toUpperCase())
        }
    }
}
