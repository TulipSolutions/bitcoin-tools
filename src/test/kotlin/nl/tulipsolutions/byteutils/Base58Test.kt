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
import org.bouncycastle.util.encoders.Hex
import org.junit.Test

class Base58Test {
    val testVectors = mapOf(
        "Hello World!" to "2NEpo7TZRRrLZSi2U",
        "The quick brown fox jumps over the lazy dog." to "USm3fpXnKG5EUBx2ndxBDMPVciP5hGey2Jh4NDv6gmeo1LkMeiKrLJUUBk6Z"
    )
    val hexTestVector = "000000287fb4cd" to "111233QC4"

    @Test
    fun testStringEncode() {
        testVectors.entries.forEach { entry: Map.Entry<String, String> ->
            Assertions.assertThat(entry.key.toByteArray().encodeBase58())
                .isEqualTo(entry.value)
        }
    }

    @Test
    fun testBase58EncodedStringDecode() {
        testVectors.entries.forEach { entry: Map.Entry<String, String> ->
            Assertions.assertThat(entry.value.decodeBase58())
                .isEqualTo(entry.key.toByteArray())
        }
    }

    @Test
    fun testBase58EncodedStringDecodeFail() {
        testVectors.entries.forEach { entry: Map.Entry<String, String> ->
            Assertions.assertThatThrownBy { entry.key.decodeBase58() }
                .hasMessage("Found non-base58 character while decoding")
        }
    }

    @Test
    fun testHexDecode() {
        val bytes = hexTestVector.second
        val expected = Hex.decode(hexTestVector.first)
        Assertions.assertThat(bytes.decodeBase58()).isEqualTo(expected)
    }

    @Test
    fun testHexEncode() {
        val bytes = Hex.decode(hexTestVector.first)
        val expected = hexTestVector.second
        Assertions.assertThat(bytes.encodeBase58()).isEqualTo(expected)
    }
}
