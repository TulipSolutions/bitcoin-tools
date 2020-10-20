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
import nl.tulipsolutions.byteutils.Hex
import org.assertj.core.api.Assertions
import org.junit.Test

@kotlin.ExperimentalUnsignedTypes
class Bip32SerdeTest {

    val ecMathProvider = ECMathProviderImpl

    // Test vectors from https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#test-vector-1
    private val testVec1MxPrivEncoded = "xprv9s21ZrQH143K3QTDL4LXw2F7HEK3wJUD2nW2nRk4stbPy6cq3jPPqjiChkVvvNKmPGJxWUtg" +
        "6LnF5kejMRNNU3TGtRBeJgk33yuGBxrMPHi"
    private val testVec1MxPubEncoded = "xpub661MyMwAqRbcFtXgS5sYJABqqG9YLmC4Q1Rdap9gSE8NqtwybGhePY2gZ29ESFjqJoCu1Rupj" +
        "e8YtGqsefD265TMg7usUDFdp6W1EGMcet8"
    private val testVec1xPubHex = "0488B21E000000000000000000873DFF81C02F525623FD1FE5167EAC3A55A049DE3D314BB42EE" +
        "227FFED37D5080339A36013301597DAEF41FBE593A02CC513D0B55527EC2DF1050E2E8FF49C85C2AB473B21"
    private val testVec1MXPubExtendedKey = ExtendedKey(
        parentKey = null,
        netAndTypeCode = byteArrayOf(0x04.toByte(), 0x88.toByte(), 0xB2.toByte(), 0x1E.toByte()),
        depth = 0x00.toByte(),
        parentFingerprint = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        childNumber = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        chainCode = Hex.decode("873DFF81C02F525623FD1FE5167EAC3A55A049DE3D314BB42EE227FFED37D508"),
        _publicKey = Hex.decode("0339A36013301597DAEF41FBE593A02CC513D0B55527EC2DF1050E2E8FF49C85C2"),
        privateKey = null,
        ecMathProvider = ecMathProvider
    )
    private val testVec1M0HXPrivEncoded = "xprv9uHRZZhk6KAJC1avXpDAp4MDc3sQKNxDiPvvkX8Br5ngLNv1TxvUxt4cV1rGL5hj6KCesn" +
        "DYUhd7oWgT11eZG7XnxHrnYeSvkzY7d2bhkJ7"
    private val testVec1M0HXPubEncoded = "xpub68Gmy5EdvgibQVfPdqkBBCHxA5htiqg55crXYuXoQRKfDBFA1WEjWgP6LHhwBZeNK1VTsfT" +
        "FUHCdrfp1bgwQ9xv5ski8PX9rL2dZXvgGDnw"
    private val testVec1M0HSlash1XPrivEncoded =
        "xprv9wTYmMFdV23N2TdNG573QoEsfRrWKQgWeibmLntzniatZvR9BmLnvSxqu53Kw1UmY" +
            "PxLgboyZQaXwTCg8MSY3H2EU4pWcQDnRnrVA1xe8fs"
    private val testVec1M0HSlash1XPubEncoded = "xpub6ASuArnXKPbfEwhqN6e3mwBcDTgzisQN1wXN9BJcM47sSikHjJf3UFHKkN" +
        "AWbWMiGj7Wf5uMash7SyYq527Hqck2AxYysAA7xmALppuCkwQ"
    private val testVec1M0HSlash1Slash2HxPriv =
        "xprv9z4pot5VBttmtdRTWfWQmoH1taj2axGVzFqSb8C9xaxKymcFzXBDptWmT7FwuEzG3" +
            "ryjH4ktypQSAewRiNMjANTtpgP4mLTj34bhnZX7UiM"
    private val testVec1M0HSlash1Slash2HxPubHex =
        "0488B21E03BEF5A2F98000000204466B9CC8E161E966409CA52986C584F07E9DC81F7" +
            "35DB683C3FF6EC7B1503F0357BFE1E341D01C69FE5654309956CBEA516822FBA8A601743A012A7896EE8DC2A5162AFA"
    private val testVec1M0Slash1Slash2HxPubEncoded =
        "xpub6D4BDPcP2GT577Vvch3R8wDkScZWzQzMMUm3PWbmWvVJrZwQY4VUNgqFJPMM3N" +
            "o2dFDFGTsxxpG5uJh7n7epu4trkrX7x7DogT5Uv6fcLW5"
    private val testVec1M0Slash1Slash2HSlash2xPriv = "xprvA2JDeKCSNNZky6uBCviVfJSKyQ1mDYahRjijr5idH2WwLsEd4Hsb2Tyh8Rf" +
        "QMuPh7f7RtyzTtdrbdqqsunu5Mm3wDvUAKRHSC34sJ7in334"
    private val testVec1M0Slash1Slash2HSlash2xPubHex =
        "0488B21E04EE7AB90C00000002CFB71883F01676F587D023CC53A35BC7F88F72" +
            "4B1F8C2892AC1275AC822A3EDD02E8445082A72F29B75CA48748A914DF60622A609CACFCE8ED0E35804560741D2942D0ACB8"
    private val testVec1M0Slash1Slash2HSlash2Slash1BillionXpriv =
        "xprvA41z7zogVVwxVSgdKUHDy1SKmdb533PjDz7J6N6mV6uS3ze" +
            "1ai8FHa8kmHScGpWmj4WggLyQjgPie1rFSruoUihUZREPSL39UNdE3BBDu76"
    private val testVec1M0Slash1Slash2HSlash2Slash1BillionXpubHex =
        "0488B21E05D880D7D83B9ACA00C783E67B921D2BEB8F6B389CC646D7263B4145701DADD2161548A8B078E65E9E022A471424DA5E6574" +
            "99D1FF51CB43C47481A03B1E77F951FE64CEC9F5A48F701118" +
            "D3A268"

    private val serde = Bip32Serde(ecMathProvider)

    @Test
    fun testMasterSerializeKey() {
        val serializedKey = serde.serializeExtKey(testVec1MXPubExtendedKey)
        Assertions.assertThat(serializedKey).isEqualTo(testVec1MxPubEncoded)
    }

    @Test
    fun testMasterDecodedKey() {
        val master = serde.deSerializeExtKey(Hex.decode(testVec1xPubHex))
        Assertions.assertThat(master).isEqualTo(testVec1MXPubExtendedKey)
    }

    @Test
    fun testMasterEncodedKey() {
        val master = serde.deSerializeExtKey(testVec1MxPubEncoded)
        Assertions.assertThat(master).isEqualTo(testVec1MXPubExtendedKey)
    }

    @Test
    fun testSerializedKeyString() {
        val master = serde.getAddress(testVec1MXPubExtendedKey)
        val expected = "15mKKb2eos1hWa6tisdPwwDC1a5J1y9nma"
        Assertions.assertThat(master).isEqualTo(expected)
    }

    // test vector from:
    // https://en.bitcoin.it/wiki/Technical_background_of_version_1_Bitcoin_addresses#How_to_create_Bitcoin_Address
    @Test
    fun TestAddress() {
        val testKey = ExtendedKey(
            parentKey = null,
            netAndTypeCode = byteArrayOf(0x04.toByte(), 0x88.toByte(), 0xB2.toByte(), 0x1E.toByte()),
            depth = 0x00.toByte(),
            parentFingerprint = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
            childNumber = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
            chainCode = Hex.decode("0000000000000000000000000000000000000000000000000000000000000000"),
            // Note invalid and unused chaincode
            _publicKey = Hex.decode("0250863ad64a87ae8a2fe83c1af1a8403cb53f53e486d8511dad8a04887e5b2352"),
            privateKey = null,
            ecMathProvider = ecMathProvider
        )
        val master = serde.getAddress(testKey)
        Assertions.assertThat(
            master
        ).isEqualTo("1PMycacnJaSqwwJqjawXBErnLsZ7RkXUAs")
    }

    @Test
    // Test vector 1 m/0H
    fun testDerive0DecodedKeyFromPub() {
        val master = serde.deSerializeExtKey(testVec1MxPubEncoded)
        val expected = ExtendedKey(
            parentKey = master,
            netAndTypeCode = byteArrayOf(0x04.toByte(), 0x88.toByte(), 0xB2.toByte(), 0x1E.toByte()),
            depth = 0x01.toByte(),
            parentFingerprint = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte()),
            childNumber = byteArrayOf(0x80.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
            chainCode = Hex.decode("47fdacbd0f1097043b78c63c20c34ef4ed9a111d980047ad16282c7ae6236141"),
            _publicKey = Hex.decode("035a784662a4a20a65bf6aab9ae98a6c068a81c52e4b032c0fb5400c706cfccc56"),
            privateKey = null,
            ecMathProvider = ecMathProvider
        )
        Assertions.assertThatThrownBy { master.deriveChild(HARDENED_KEY_ZERO) }
            .hasMessage("Deriving a hardened key requires a private key")
    }

    @Test
    // Test vector 1 m/0H
    fun testDerive0DecodedKeyFromPrivate() {
        val master = serde.deSerializeExtKey(testVec1MxPrivEncoded)
        val expected = ExtendedKey(
            parentKey = master,
            netAndTypeCode = Bip32Serde.MAINNET_PRIVATE_CODE,
            depth = 0x01.toByte(),
            parentFingerprint = byteArrayOf(0x34.toByte(), 0x42.toByte(), 0x19.toByte(), 0x3e.toByte()),
            childNumber = byteArrayOf(0x80.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
            chainCode = Hex.decode("47fdacbd0f1097043b78c63c20c34ef4ed9a111d980047ad16282c7ae6236141"),
            _publicKey = Hex.decode("035a784662a4a20a65bf6aab9ae98a6c068a81c52e4b032c0fb5400c706cfccc56"),
            privateKey = Hex.decode("00edb2e14f9ee77d26dd93b4ecede8d16ed408ce149b6cd80b0715a2d911a0afea"),
            ecMathProvider = ecMathProvider
        )
        Assertions.assertThat(master.deriveChild(HARDENED_KEY_ZERO)).isEqualToComparingFieldByField(expected)
    }

    @Test
    // Test vector 1 xpub(m/0H) /1
    fun testDerive1FromM0xPub() {
        val master = serde.deSerializeExtKey(testVec1M0HXPubEncoded)
        val expected = ExtendedKey(
            parentKey = master,
            netAndTypeCode = Bip32Serde.MAINNET_PUBLIC_CODE,
            depth = 0x02.toByte(),
            parentFingerprint = byteArrayOf(0x5c.toByte(), 0x1b.toByte(), 0xd6.toByte(), 0x48.toByte()),
            childNumber = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte()),
            chainCode = Hex.decode("2a7857631386ba23dacac34180dd1983734e444fdbf774041578e9b6adb37c19"),
            _publicKey = Hex.decode("03501e454bf00751f24b1b489aa925215d66af2234e3891c3b21a52bedb3cd711c"),
            privateKey = null,
            ecMathProvider = ecMathProvider
        )
        Assertions.assertThat(master.deriveChild(1)).isEqualToComparingFieldByField(expected)
    }

    @Test
    // Test vector 1 xpub(m/0H) /1
    fun testDerive1FromM0XPriv() {
        val master = serde.deSerializeExtKey(testVec1M0HXPrivEncoded)
        val expected = ExtendedKey(
            parentKey = master,
            netAndTypeCode = Bip32Serde.MAINNET_PRIVATE_CODE,
            depth = 0x02.toByte(),
            parentFingerprint = byteArrayOf(0x5c.toByte(), 0x1b.toByte(), 0xd6.toByte(), 0x48.toByte()),
            childNumber = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte()),
            chainCode = Hex.decode("2a7857631386ba23dacac34180dd1983734e444fdbf774041578e9b6adb37c19"),
            _publicKey = Hex.decode("03501e454bf00751f24b1b489aa925215d66af2234e3891c3b21a52bedb3cd711c"),
            privateKey = Hex.decode("003c6cb8d0f6a264c91ea8b5030fadaa8e538b020f0a387421a12de9319dc93368"),
            ecMathProvider = ecMathProvider
        )
        Assertions.assertThat(master.deriveChild(1)).isEqualToComparingFieldByField(expected)
    }

    @Test
    // test vector1 xpub(m/0H/1)/2H
    fun testDeriveM0Slash1Slash2() {
        val master = serde.deSerializeExtKey(testVec1M0HSlash1XPubEncoded)
        Assertions.assertThatThrownBy { master.deriveChild(HARDENED_KEY_ZERO + 2) }
            .hasMessage("Deriving a hardened key requires a private key")
    }

    @Test
    // test vector1 m/0H/1/2H/2
    fun testDeriveM0Slash0HSlash1Slash2HSlash2xPub() {
        val master = serde.deSerializeExtKey(testVec1M0Slash1Slash2HxPubEncoded)
        val expected = ExtendedKey(
            parentKey = master,
            netAndTypeCode = byteArrayOf(0x04.toByte(), 0x88.toByte(), 0xB2.toByte(), 0x1E.toByte()),
            depth = 0x04.toByte(),
            parentFingerprint = Hex.decode("ee7ab90c"),
            childNumber = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x02.toByte()),
            chainCode = Hex.decode("cfb71883f01676f587d023cc53a35bc7f88f724b1f8c2892ac1275ac822a3edd"),
            _publicKey = Hex.decode("02e8445082a72f29b75ca48748a914df60622a609cacfce8ed0e35804560741d29"),
            privateKey = null,
            ecMathProvider = ecMathProvider
        )
        Assertions.assertThat(master.deriveChild(2)).isEqualTo(expected)
    }

    @Test
    // test vector1 (m/0H/1/2H/2)/1000000000
    fun testDeriveM0Slash0HSlash1Slash2HSlash2SlashOneBillion() {
        val master = serde.deSerializeExtKey(Hex.decode(testVec1M0Slash1Slash2HSlash2xPubHex))
        val expected = ExtendedKey(
            parentKey = null,
            netAndTypeCode = byteArrayOf(0x04.toByte(), 0x88.toByte(), 0xB2.toByte(), 0x1E.toByte()),
            depth = 0x05.toByte(),
            parentFingerprint = Hex.decode("d880d7d8"),
            childNumber = Hex.decode("3b9aca00"),
            chainCode = Hex.decode("c783e67b921d2beb8f6b389cc646d7263b4145701dadd2161548a8b078e65e9e"),
            _publicKey = Hex.decode("022a471424da5e657499d1ff51cb43c47481a03b1e77f951fe64cec9f5a48f7011"),
            privateKey = null,
            ecMathProvider = ecMathProvider
        )
        Assertions.assertThat(master.deriveChild(1000000000)).isEqualTo(expected)
    }

    // Test vectors from https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#test-vector-3
    // Test vector 3 leading test for zeros
    private val testVec3ExtendedPublicKeyEncoded = "xpub661MyMwAqRbcEZVB4dScxMAdx6d4nFc9nvyvH3v4gJL378CSRZiYmh" +
        "RoP7mBy6gSPSCYk6SzXPTf3ND1cZAceL7SfJ1Z3GC8vBgp2epUt13"
    private val testVec3ExtendedPrivateKeyEncoded = "xprv9s21ZrQH143K25QhxbucbDDuQ4naNntJRi4KUfWT7xo4EKsHt2QJDu" +
        "7KXp1A3u7Bi1j8ph3EGsZ9Xvz9dGuVrtHHs7pXeTzjuxBrCmmhgC6"
    private val testVec3PubExtendedKey = ExtendedKey(
        parentKey = null,
        netAndTypeCode = byteArrayOf(0x04.toByte(), 0x88.toByte(), 0xB2.toByte(), 0x1E.toByte()),
        depth = 0x00.toByte(),
        parentFingerprint = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        childNumber = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        chainCode = Hex.decode("01D28A3E53CFFA419EC122C968B3259E16B65076495494D97CAE10BBFEC3C36F"),
        _publicKey = Hex.decode("03683AF1BA5743BDFC798CF814EFEEAB2735EC52D95ECED528E692B8E34C4E5669"),
        privateKey = null,
        ecMathProvider = ecMathProvider
    )
    private val testVec3PrivateExtendedKey = ExtendedKey(
        parentKey = null,
        netAndTypeCode = byteArrayOf(0x04.toByte(), 0x88.toByte(), 0xAD.toByte(), 0xE4.toByte()),
        depth = 0x00.toByte(),
        parentFingerprint = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        childNumber = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
        chainCode = Hex.decode("01d28a3e53cffa419ec122c968b3259e16b65076495494d97cae10bbfec3c36f"),
        _publicKey = null,
        privateKey = Hex.decode("0000ddb80b067e0d4993197fe10f2657a844a384589847602d56f0c629c81aae32"),
        ecMathProvider = ecMathProvider
    )

    val testVec3Master0HxPubEncoded = "xpub68NZiKmJWnxxS6aaHmn81bvJeTESw724CRDs6HbuccFQN9Ku14VQrADWgqbhhTHBaohPX4CjNL" +
        "f9fq9MYo6oDaPPLPxSb7gwQN3ih19Zm4Y"
    private val testVec3Master0HxPrivEncoded = "xprv9uPDJpEQgRQfDcW7BkF7eTya6RPxXeJCqCJGHuCJ4GiRVLzkTXBAJMu2qaMWPrS7A" +
        "ANYqdq6vcBcBUdJCVVFceUvJFjaPdGZ2y9WACViL4L"

    @Test
    fun testLeadingZerosMasterDecodedPubKey() {
        val master = serde.deSerializeExtKey(testVec3ExtendedPublicKeyEncoded)
        Assertions.assertThat(master).isEqualTo(testVec3PubExtendedKey)
    }

    @Test
    fun testLeadingZerosMasterDecodedPrivKey() {
        val master = serde.deSerializeExtKey(testVec3ExtendedPrivateKeyEncoded)
        Assertions.assertThat(master).isEqualToComparingFieldByField(testVec3PrivateExtendedKey)
    }

    @Test
    fun testLeadingZerosMasterSerializexPubKey() {
        val master = serde.serializeExtKey(testVec3PubExtendedKey)
        Assertions.assertThat(master).isEqualTo(testVec3ExtendedPublicKeyEncoded)
    }

    @Test
    fun testLeadingZerosDeriveMaster0PubFail() {
        val master = serde.deSerializeExtKey(testVec3ExtendedPublicKeyEncoded)
        Assertions.assertThatThrownBy { master.deriveChild(HARDENED_KEY_ZERO) }
            .hasMessage("Deriving a hardened key requires a private key")
    }

    @Test
    fun testLeadingZerosDeriveMaster0PrivateSuccess() {
        val master = serde.deSerializeExtKey(testVec3ExtendedPrivateKeyEncoded)
        val derivedSerialized = serde.serializeExtKey(master.deriveChild(HARDENED_KEY_ZERO))
        Assertions.assertThat(derivedSerialized).isEqualTo(testVec3Master0HxPrivEncoded)
    }
}
