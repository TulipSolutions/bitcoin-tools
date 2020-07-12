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
import org.bouncycastle.util.encoders.Hex
import org.junit.Test

class ExtendedKeyWrapperTest {
    // Test vectors from https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#test-vector-1
    private val testVec1MxPrivEncoded = "xprv9s21ZrQH143K3QTDL4LXw2F7HEK3wJUD2nW2nRk4stbPy6cq3jPPqjiChkVvvNKmPGJxWUtg" +
        "6LnF5kejMRNNU3TGtRBeJgk33yuGBxrMPHi"
    private val testVec1MxPubEncoded = "xpub661MyMwAqRbcFtXgS5sYJABqqG9YLmC4Q1Rdap9gSE8NqtwybGhePY2gZ29ESFjqJoCu1Rupj" +
        "e8YtGqsefD265TMg7usUDFdp6W1EGMcet8"
    private val testVec1xPubHex = "0488B21E000000000000000000873DFF81C02F525623FD1FE5167EAC3A55A049DE3D314BB42EE" +
        "227FFED37D5080339A36013301597DAEF41FBE593A02CC513D0B55527EC2DF1050E2E8FF49C85C2AB473B21"
    private val testVec1M0HXPrivEncoded = "xprv9uHRZZhk6KAJC1avXpDAp4MDc3sQKNxDiPvvkX8Br5ngLNv1TxvUxt4cV1rGL5hj6KCesn" +
        "DYUhd7oWgT11eZG7XnxHrnYeSvkzY7d2bhkJ7"

    private val BIP84rootPub = "zpub6jftahH18ngZxLmXaKw3GSZzZsszmt9WqedkyZdezFtWRFBZqsQH5hyUmb4pCEeZGmVfQuP5bedXTB8is" +
        "6fTv19U1GQRyQUKQGUTzyHACMF"
    private val BIP84rootPriv = "zprvAWgYBBk7JR8Gjrh4UJQ2uJdG1r3WNRRfURiABBE3RvMXYSrRJL62XuezvGdPvG6GFBZduosCc1YP5wix" +
        "Pox7zhZLfiUm8aunE96BBa4Kei5"

    // Account 0, root = m/84'/0'/0'
    private val m84slash0slash0xPriv = "zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nv" +
        "gGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE"

    // Account 0, first receiving address = m/84'/0'/0'/0/0
    private val m84PubkeyFirst = "0330d54fd0dd420a6e5f8d3624f5f3482cae350f79d5f0753bf5beef9c2d91af3c"
    private val m84AddressFirst = "bc1qcr8te4kr609gcawutmrza0j4xv80jy8z306fyu"

    @Test
    fun testBytesConstructor() {
        val buildBip32FromBytes = ExtendedKeyWrapper(Hex.decode(testVec1xPubHex))
        Assertions.assertThat(buildBip32FromBytes.serializeExtKey()).isEqualTo(testVec1MxPubEncoded)
    }

    @Test
    fun testXpubStringConstructor() {
        val buildBip32FromString = ExtendedKeyWrapper(testVec1MxPubEncoded)
        Assertions.assertThat(buildBip32FromString.serializeExtKey()).isEqualTo(testVec1MxPubEncoded)

        val buildBip32Priv = ExtendedKeyWrapper(testVec1MxPrivEncoded)
        Assertions.assertThat(buildBip32Priv.serializeExtKey()).isEqualTo(testVec1MxPrivEncoded)
    }

    @Test
    fun testZpubStringConstructor() {
        val buildBip84FromString = ExtendedKeyWrapper(BIP84rootPub)
        Assertions.assertThat(buildBip84FromString.serializeExtKey()).isEqualTo(BIP84rootPub)

        val buildBip84Priv = ExtendedKeyWrapper(BIP84rootPriv)
        Assertions.assertThat(buildBip84Priv.serializeExtKey()).isEqualTo(BIP84rootPriv)
    }

    @Test
    fun testDeriveBip32Child() {
        val buildBip32FromString = ExtendedKeyWrapper(testVec1MxPrivEncoded)
        val derived = buildBip32FromString.deriveChild(HARDENED_KEY_ZERO)
        Assertions.assertThat(derived.serializeExtKey()).isEqualTo(testVec1M0HXPrivEncoded)
    }

    @Test
    fun testDeriveBip84Child() {
        val buildBip84FromString = ExtendedKeyWrapper(m84slash0slash0xPriv)
        val derived = buildBip84FromString.deriveChild(0).deriveChild(0)
        Assertions.assertThat(derived.getPublicKey()).isEqualTo(Hex.decode(m84PubkeyFirst))
    }

    @Test
    fun getAddressBip84() {
        val buildBip84FromString = ExtendedKeyWrapper(m84slash0slash0xPriv)
        val derived = buildBip84FromString.deriveChild(0).deriveChild(0)
        Assertions.assertThat(derived.getAddress()).isEqualTo(m84AddressFirst)
    }
}
