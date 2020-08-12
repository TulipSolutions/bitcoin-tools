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

import java.lang.RuntimeException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/*
   Glues the respective Bip serde with the resulting extended key
 */
class ExtendedKeyWrapper {

    private var serde: ExtendedKeySerdeInterface
    private var extendedKey: ExtendedKey

    constructor(serde: ExtendedKeySerdeInterface, extendedKey: ExtendedKey) {
        this.serde = serde
        this.extendedKey = extendedKey
    }

    constructor(extKeyString: String) {
        when (
            val netAndTypeCode = extKeyString.substring(0, 4)) {
            "xprv",
            "xpub",
            "tprv",
            "tpub" -> {
                serde = Bip32Serde()
            }
            "zprv",
            "zpub",
            "vprv",
            "vpub" -> {
                serde = Bip84Serde()
            }
            else -> throw RuntimeException("Extended Key type: $netAndTypeCode, not supported")
        }
        extendedKey = serde.deSerializeExtKey(extKeyString)
    }

    constructor(extKeyBytes: ByteArray) {
        val netAndTypeCode = extKeyBytes.copyOfRange(0, 4)
        when {
            netAndTypeCode.contentEquals(Bip32Serde.MAINNET_PUBLIC_CODE) ||
                netAndTypeCode.contentEquals(Bip32Serde.MAINNET_PRIVATE_CODE) ||
                netAndTypeCode.contentEquals(Bip32Serde.TESTNET_PUBLIC_CODE) ||
                netAndTypeCode.contentEquals(Bip32Serde.TESTNET_PRIVATE_CODE) -> {
                serde = Bip32Serde()
            }
            netAndTypeCode.contentEquals(Bip84Serde.MAINNET_PUBLIC_CODE) ||
                netAndTypeCode.contentEquals(Bip84Serde.MAINNET_PRIVATE_CODE) ||
                netAndTypeCode.contentEquals(Bip84Serde.TESTNET_PUBLIC_CODE) ||
                netAndTypeCode.contentEquals(Bip84Serde.TESTNET_PRIVATE_CODE) -> {
                serde = Bip84Serde()
            }
            else -> throw RuntimeException("Type and net code: $netAndTypeCode, not supported")
        }
        extendedKey = serde.deSerializeExtKey(extKeyBytes)
    }

    /**
     * Constructs an extended key from a seed
     *
     * @param seed the chaincode + keybytes
     * @param serde the serializer-deserializer to use for derivation
     * @param isMainNet whether it should generate mainnet keys (if false it generates testnet keys)
     * @return [ExtendedKey]
     */
    constructor(
        seed: ByteArray,
        serde: ExtendedKeySerdeInterface,
        isMainNet: Boolean = false
    ) {
        if (seed.size != 64) {
            throw InvalidSeedSizeException(seed.size)
        }
        // hmac the seed as specified by: https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#master-key-generation
        val mac = Mac.getInstance("HmacSHA512")
        mac.reset()
        val key = SecretKeySpec("Bitcoin seed".toByteArray(), "HmacSHA512")
        mac.init(key)
        val hmacSeed = mac.doFinal(seed)

        this.serde = serde
        this.extendedKey = ExtendedKey(
            parentKey = null,
            parentFingerprint = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
            netAndTypeCode = serde.getNetCodeAndType(isMainNet, true),
            depth = 0,
            childNumber = byteArrayOf(0x00, 0x00, 0x00, 0x00),
            // Pad the private key part
            privateKey = byteArrayOf(0x00) + hmacSeed.sliceArray(0..31),
            _publicKey = null,
            chainCode = hmacSeed.sliceArray(32..63)
        )
    }

    fun deriveChild(sequence: Int) = ExtendedKeyWrapper(
        this.serde,
        this.serde.deriveChildBIPRules(this.extendedKey, sequence)
    )

    fun getAddress() = serde.getAddress(this.extendedKey)
    fun getPublicKey() = this.extendedKey.publicKey
    fun serializeExtKey() = serde.serializeExtKey(this.extendedKey)
}
