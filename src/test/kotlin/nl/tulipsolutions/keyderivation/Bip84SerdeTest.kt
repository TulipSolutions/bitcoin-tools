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

import nl.tulipsolutions.BCmath.ECMathProviderImpl
import org.assertj.core.api.Assertions
import org.bouncycastle.util.encoders.Hex
import org.junit.Test

@kotlin.ExperimentalUnsignedTypes
class Bip84SerdeTest {

    val ecMathProvider = ECMathProviderImpl

    private val rootPub = "zpub6jftahH18ngZxLmXaKw3GSZzZsszmt9WqedkyZdezFtWRFBZqsQH5hyUmb4pCEeZGmVfQuP5bedXTB8is6fTv" +
        "19U1GQRyQUKQGUTzyHACMF"
    private val rootPriv = "zprvAWgYBBk7JR8Gjrh4UJQ2uJdG1r3WNRRfURiABBE3RvMXYSrRJL62XuezvGdPvG6GFBZduosCc1YP5wixPox7" +
        "zhZLfiUm8aunE96BBa4Kei5"

    // Account 0, root = m/84'/0'/0'
    private val m84slash0slash0xPriv = "zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nv" +
        "gGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE"
    private val m84slash0slash0xPub = "zpub6rFR7y4Q2AijBEqTUquhVz398htDFrtymD9xYYfG1m4wAcvPhXNfE3EfH1r1ADqtfSdVCToUG" +
        "868RvUUkgDKf31mGDtKsAYz2oz2AGutZYs"

    // Account 0, first receiving address = m/84'/0'/0'/0/0
    private val privateKeyFirst = "KyZpNDKnfs94vbrwhJneDi77V6jF64PWPF8x5cdJb8ifgg2DUc9d"
    private val pubkeyFirst = "0330d54fd0dd420a6e5f8d3624f5f3482cae350f79d5f0753bf5beef9c2d91af3c"
    private val addressFirst = "bc1qcr8te4kr609gcawutmrza0j4xv80jy8z306fyu"

    // Account 0, second receiving address = m/84'/0'/0'/0/1
    private val pubkeySecond = "03e775fd51f0dfb8cd865d9ff1cca2a158cf651fe997fdc9fee9c1d3b5e995ea77"
    private val addressSecond = "bc1qnjg0jd8228aq7egyzacy8cys3knf9xvrerkf9g"

    // Account 0, first change address = m/84'/0'/0'/1/0
    private val pubkeyFirstChange = "03025324888e429ab8e3dbaf1f7802648b9cd01e9b418485c5fa4c1b9b5700e1a6"
    private val addressFirstChange = "bc1q8c6fshw2dlwun7ekn9qwf37cu2rn755upcp6el"
    private val serde = Bip84Serde(ecMathProvider)

    @Test
    fun testPubAndPrivSerializationRoot() {
        val serializedPubKey = serde.deSerializeExtKey(rootPub)
        val serializedPrivKey = serde.deSerializeExtKey(rootPriv)
        Assertions.assertThat(serializedPubKey.publicKey).isEqualTo(serializedPrivKey.publicKey)
    }

    @Test
    fun testPubAndPrivSerializationAccount0() {
        val serializedPubKey = serde.deSerializeExtKey(m84slash0slash0xPub)
        val serializedPrivKey = serde.deSerializeExtKey(m84slash0slash0xPriv)
        Assertions.assertThat(serializedPubKey.publicKey).isEqualTo(serializedPrivKey.publicKey)
    }

    @Test
    fun testDeriveFirst() {
        val serializedKey = serde.deSerializeExtKey(rootPriv)
        val derived = serializedKey
            .deriveChild(HARDENED_KEY_ZERO + 84).deriveChild(HARDENED_KEY_ZERO)
            .deriveChild(HARDENED_KEY_ZERO).deriveChild(0).deriveChild(0)
        Assertions.assertThat(derived.publicKey).isEqualTo(Hex.decode(pubkeyFirst))
    }

    @Test
    fun testDeriveFirstAddress() {
        val serializedKey = serde.deSerializeExtKey(rootPriv)
        val derived = serializedKey
            .deriveChild(HARDENED_KEY_ZERO + 84).deriveChild(HARDENED_KEY_ZERO)
            .deriveChild(HARDENED_KEY_ZERO).deriveChild(0).deriveChild(0)
        val resultingAddress = serde.getAddress(derived)
        Assertions.assertThat(resultingAddress).isEqualTo(addressFirst)
    }

    @Test
    fun testDeriveSecond() {
        val serializedKey = serde.deSerializeExtKey(rootPriv)
        val derived = serializedKey
            .deriveChild(HARDENED_KEY_ZERO + 84).deriveChild(HARDENED_KEY_ZERO)
            .deriveChild(HARDENED_KEY_ZERO).deriveChild(0).deriveChild(1)
        Assertions.assertThat(derived.publicKey).isEqualTo(Hex.decode(pubkeySecond))
    }

    @Test
    fun testDeriveSecondAddress() {
        val serializedKey = serde.deSerializeExtKey(rootPriv)
        val derived = serializedKey
            .deriveChild(HARDENED_KEY_ZERO + 84).deriveChild(HARDENED_KEY_ZERO)
            .deriveChild(HARDENED_KEY_ZERO).deriveChild(0).deriveChild(1)
        val resultingAddress = serde.getAddress(derived)
        Assertions.assertThat(resultingAddress).isEqualTo(addressSecond)
    }

    @Test
    fun testDeriveFirstChange() {
        val serializedKey = serde.deSerializeExtKey(rootPriv)
        val derived = serializedKey
            .deriveChild(HARDENED_KEY_ZERO + 84).deriveChild(HARDENED_KEY_ZERO)
            .deriveChild(HARDENED_KEY_ZERO).deriveChild(1).deriveChild(0)
        Assertions.assertThat(derived.publicKey).isEqualTo(Hex.decode(pubkeyFirstChange))
    }

    @Test
    fun testDeriveFirstChangeAddress() {
        val serializedKey = serde.deSerializeExtKey(rootPriv)
        val derived = serializedKey
            .deriveChild(HARDENED_KEY_ZERO + 84).deriveChild(HARDENED_KEY_ZERO)
            .deriveChild(HARDENED_KEY_ZERO).deriveChild(1).deriveChild(0)
        val resultingAddress = serde.getAddress(derived)
        Assertions.assertThat(resultingAddress).isEqualTo(addressFirstChange)
    }

    /*
        Pub key derivation tests
     */
    @Test
    fun testDeriveFirstFromPub() {
        val serializedKey = serde.deSerializeExtKey(m84slash0slash0xPub)
        val derived = serializedKey.deriveChild(0).deriveChild(0)
        Assertions.assertThat(derived.publicKey).isEqualTo(Hex.decode(pubkeyFirst))
    }

    @Test
    fun testDeriveFirstAddressFromPub() {
        val serializedKey = serde.deSerializeExtKey(m84slash0slash0xPub)
        val derived = serializedKey.deriveChild(0).deriveChild(0)
        val resultingAddress = serde.getAddress(derived)
        Assertions.assertThat(resultingAddress).isEqualTo(addressFirst)
    }

    @Test
    fun testDeriveSecondFromPub() {
        val serializedKey = serde.deSerializeExtKey(m84slash0slash0xPub)
        val derived = serializedKey.deriveChild(0).deriveChild(1)
        Assertions.assertThat(derived.publicKey).isEqualTo(Hex.decode(pubkeySecond))
    }

    @Test
    fun testDeriveSecondAddressFromPub() {
        val serializedKey = serde.deSerializeExtKey(m84slash0slash0xPub)
        val derived = serializedKey.deriveChild(0).deriveChild(1)
        val resultingAddress = serde.getAddress(derived)
        Assertions.assertThat(resultingAddress).isEqualTo(addressSecond)
    }

    @Test
    fun testDeriveFirstChangeFromPub() {
        val serializedKey = serde.deSerializeExtKey(m84slash0slash0xPub)
        val derived = serializedKey.deriveChild(1).deriveChild(0)
        Assertions.assertThat(derived.publicKey).isEqualTo(Hex.decode(pubkeyFirstChange))
    }

    @Test
    fun testDeriveFirstChangeAddressFromPub() {
        val serializedKey = serde.deSerializeExtKey(m84slash0slash0xPub)
        val derived = serializedKey.deriveChild(1).deriveChild(0)
        val resultingAddress = serde.getAddress(derived)
        Assertions.assertThat(resultingAddress).isEqualTo(addressFirstChange)
    }
    /*
        Extended Key serialization tests
     */

    @Test
    fun testDeserializeSerializeRootPub() {
        val serializedKey = serde.deSerializeExtKey(rootPub)
        val serializedString = serde.serializeExtKey(serializedKey)
        Assertions.assertThat(serializedString).isEqualTo(rootPub)
    }

    @Test
    fun testDeserializeSerializeAccount0RootPriv() {
        val serializedKey = serde.deSerializeExtKey(rootPriv)
            .deriveChild(HARDENED_KEY_ZERO + 84).deriveChild(HARDENED_KEY_ZERO).deriveChild(HARDENED_KEY_ZERO)
        val serializedString = serde.serializeExtKey(serializedKey)
        Assertions.assertThat(serializedString).isEqualTo(m84slash0slash0xPriv)
    }

    @Test
    fun testDeriveChildBIPRulesErrors() {
        val extendedKey = serde.deSerializeExtKey(rootPriv)
        Assertions.assertThatThrownBy {
            serde.deriveChildBIPRules(extendedKey, 0)
        }.hasMessage(
            HardenedDerivationRequiredException(extendedKey.depth.toInt(), 0).message
        )

        Assertions.assertThatThrownBy {
            serde.deriveChildBIPRules(extendedKey, HARDENED_KEY_ZERO + 1)
        }.hasMessage(
            InvalidPurposeException(84, HARDENED_KEY_ZERO + 1).message
        )
    }

    @Test
    fun testDeriveChildBIPRulesSuccess() {
        val extendedKey = serde.deSerializeExtKey(rootPriv)
        // derive m/84'/0'/0'/0/0 from root
        val purpose = serde.deriveChildBIPRules(extendedKey, HARDENED_KEY_ZERO + 84)
        val coinType = serde.deriveChildBIPRules(purpose, HARDENED_KEY_ZERO)
        val account = serde.deriveChildBIPRules(coinType, HARDENED_KEY_ZERO)
        val change = serde.deriveChildBIPRules(account, 0)
        val addressIndex = serde.deriveChildBIPRules(change, 0)

        // Some extra checks
        Assertions.assertThat(addressIndex.publicKey).isEqualTo(Hex.decode(pubkeyFirst))
        Assertions.assertThat(serde.getAddress(addressIndex)).isEqualTo(addressFirst)
        Assertions.assertThat(addressIndex.depth).isEqualTo(5)
    }
}
