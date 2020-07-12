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

class Bech32Test {
    // Taken from: https://github.com/sipa/bech32/blob/master/ref/javascript/tests.js
    private val validChecksumTestVectors = listOf(
        "A12UEL5L",
        "an83characterlonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1tt5tgs",
        "abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw",
        "11qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqc8247j",
        "split1checkupstagehandshakeupstreamerranterredcaperred2y9e3w"
    )
    private val invalidChecksumTestVectors = listOf(
        " 1nwldj5",
        "\\x7F" + "1axkwrx",
        "an84characterslonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1569pvx",
        "pzry9x0s0muk",
        "1pzry9x0s0muk",
        "x1b4n0q5v",
        "li1dgmt3",
        "de1lg7wt" + "\\xFF"
    )

    @Test
    fun testBech32ValidChecksum() {
        validChecksumTestVectors.forEach {
            Assertions.assertThat(it.decodeBech32()).isNotNull
        }
    }

    @Test
    fun testBech32InvalidChecksum() {
        invalidChecksumTestVectors.forEach {
            Assertions.assertThatThrownBy { it.decodeBech32() }
        }
    }
}
