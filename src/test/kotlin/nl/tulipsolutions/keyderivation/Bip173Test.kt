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

package nl.tulipsolutions.keyderivation

import org.assertj.core.api.Assertions
import org.junit.Test

class Bip173Test {
    private val validAddresses = mapOf(
        "BC1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7KV8F3T4" to
            intArrayOf(
                0x00, 0x14, 0x75, 0x1e, 0x76, 0xe8, 0x19, 0x91, 0x96, 0xd4, 0x54,
                0x94, 0x1c, 0x45, 0xd1, 0xb3, 0xa3, 0x23, 0xf1, 0x43, 0x3b, 0xd6
            ),
        "tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sl5k7" to
            intArrayOf(
                0x00, 0x20, 0x18, 0x63, 0x14, 0x3c, 0x14, 0xc5, 0x16, 0x68, 0x04,
                0xbd, 0x19, 0x20, 0x33, 0x56, 0xda, 0x13, 0x6c, 0x98, 0x56, 0x78,
                0xcd, 0x4d, 0x27, 0xa1, 0xb8, 0xc6, 0x32, 0x96, 0x04, 0x90, 0x32,
                0x62
            ),
        "bc1pw508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7k7grplx" to
            intArrayOf(
                0x51, 0x28, 0x75, 0x1e, 0x76, 0xe8, 0x19, 0x91, 0x96, 0xd4, 0x54,
                0x94, 0x1c, 0x45, 0xd1, 0xb3, 0xa3, 0x23, 0xf1, 0x43, 0x3b, 0xd6,
                0x75, 0x1e, 0x76, 0xe8, 0x19, 0x91, 0x96, 0xd4, 0x54, 0x94, 0x1c,
                0x45, 0xd1, 0xb3, 0xa3, 0x23, 0xf1, 0x43, 0x3b, 0xd6
            ),
        "BC1SW50QA3JX3S" to
            intArrayOf(
                0x60, 0x02, 0x75, 0x1e
            ),
        "bc1zw508d6qejxtdg4y5r3zarvaryvg6kdaj" to
            intArrayOf(
                0x52, 0x10, 0x75, 0x1e, 0x76, 0xe8, 0x19, 0x91, 0x96, 0xd4, 0x54,
                0x94, 0x1c, 0x45, 0xd1, 0xb3, 0xa3, 0x23
            ),
        "tb1qqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsesrxh6hy" to
            intArrayOf(
                0x00, 0x20, 0x00, 0x00, 0x00, 0xc4, 0xa5, 0xca, 0xd4, 0x62, 0x21,
                0xb2, 0xa1, 0x87, 0x90, 0x5e, 0x52, 0x66, 0x36, 0x2b, 0x99, 0xd5,
                0xe9, 0x1c, 0x6c, 0xe2, 0x4d, 0x16, 0x5d, 0xab, 0x93, 0xe8, 0x64,
                0x33
            )
    )
    private val invalidAddresses = listOf(
        "tc1qw508d6qejxtdg4y5r3zarvary0c5xw7kg3g4ty",
        "bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t5",
        "BC13W508D6QEJXTDG4Y5R3ZARVARY0C5XW7KN40WF2",
        "bc1rw5uspcuh",
        "bc10w508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kw5rljs90",
        "BC1QR508D6QEJXTDG4Y5R3ZARVARYV98GJ9P",
        "tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sL5k7",
        "bc1zw508d6qejxtdg4y5r3zarvaryvqyzf3du",
        "tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3pjxtptv",
        "bc1gmk9yu"
    )

    private fun SegWitProgram.segwitScriptPubKey(): List<Int> =
        listOf(
            if (this.version != 0x00.toByte()) this.version + 0x50 else this.version.toInt(),
            this.program.size
        ) + this.program

    @Test
    fun testBech32ValidAddressDecode() {
        validAddresses.forEach { (address, scriptPubKey) ->
            println(address)
            val hrp = when (address.substring(0, 2).toLowerCase()) {
                "bc" -> address.substring(0, 2)
                "tb" -> address.substring(0, 2)
                else -> throw RuntimeException("Invalid address")
            }
            val ret = address.decodeSegWitAddress(hrp)
            val output = ret.segwitScriptPubKey()
            Assertions.assertThat(output).isEqualTo(scriptPubKey.toList())

            val recreate = encodeSegWitAddress(hrp, ret.version, ret.program)
            Assertions.assertThat(recreate).isEqualTo(address)
            println("success")
        }
    }

    @Test
    fun testBech32InvalidAddressDecode() {
        invalidAddresses.forEach { s: String ->
            Assertions.assertThatThrownBy { s.decodeSegWitAddress("bc") }
            Assertions.assertThatThrownBy { s.decodeSegWitAddress("tb") }
        }
    }
}
