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

import java.security.MessageDigest
import nl.tulipsolutions.byteutils.decodeBase58
import nl.tulipsolutions.byteutils.encodeBase58
import org.bouncycastle.crypto.digests.RIPEMD160Digest

open class Bip32Serde(
    private val MAINNET_PUBLIC_CODE: ByteArray = this.MAINNET_PUBLIC_CODE,
    private val MAINNET_PRIVATE_CODE: ByteArray = this.MAINNET_PRIVATE_CODE,
    private val TESTNET_PUBLIC_CODE: ByteArray = this.TESTNET_PUBLIC_CODE,
    private val TESTNET_PRIVATE_CODE: ByteArray = this.TESTNET_PRIVATE_CODE,
    private val MAINNET_ADDRESS_CODE: ByteArray = byteArrayOf(0x00),
    private val TESTNET_ADDRESS_CODE: ByteArray = byteArrayOf(0x6F)
) : ExtendedKeySerdeInterface {

    companion object {
        private val MAINNET_CODE = byteArrayOf(0x04.toByte(), 0x88.toByte())
        private val TESTNET_CODE = byteArrayOf(0x04.toByte(), 0x35.toByte())

        @JvmStatic
        val MAINNET_PUBLIC_CODE = MAINNET_CODE + byteArrayOf(0xB2.toByte(), 0x1E.toByte())

        @JvmStatic
        val MAINNET_PRIVATE_CODE = MAINNET_CODE + byteArrayOf(0xAD.toByte(), 0xE4.toByte())

        @JvmStatic
        val TESTNET_PUBLIC_CODE = TESTNET_CODE + byteArrayOf(0x87.toByte(), 0xCF.toByte())

        @JvmStatic
        val TESTNET_PRIVATE_CODE = TESTNET_CODE + byteArrayOf(0x83.toByte(), 0x94.toByte())
    }

    override fun getNetCodeAndType(isMain: Boolean, isPrivate: Boolean): ByteArray =
        if (isMain) {
            when (isPrivate) {
                true -> MAINNET_PRIVATE_CODE
                false -> MAINNET_PUBLIC_CODE
            }
        } else when (isPrivate) {
            true -> TESTNET_PRIVATE_CODE
            false -> TESTNET_PUBLIC_CODE
        }

    // BIP32 does not have specific rules on how to derive children
    override fun deriveChildBIPRules(extendedKey: ExtendedKey, sequence: Int) = extendedKey.deriveChild(sequence)

    override fun toHexString(extendedKey: ExtendedKey) = extendedKey.toString()

    override fun deSerializeExtKey(extKeyEncoded: String): ExtendedKey {
        return deSerializeExtKey(extKeyEncoded.decodeBase58())
    }

    private fun calculateExtendedCheckSum(extendedKey: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashedExtendKey = digest.digest(
            extendedKey
        )
        return digest.digest(hashedExtendKey).copyOfRange(0, 4)
    }

    fun hash160(extendedKey: ExtendedKey): ByteArray {
        val sha256Digest = MessageDigest.getInstance("SHA-256")
            .digest(extendedKey.publicKey)
        val digest = RIPEMD160Digest()
        val retValue = ByteArray(digest.digestSize)
        digest.update(sha256Digest, 0, sha256Digest.size)
        digest.doFinal(retValue, 0)
        return retValue
    }

    override fun getAddress(extendedKey: ExtendedKey): String {
        val hash160 = hash160(extendedKey)
        val extendedKeyBytes = when {
            extendedKey.netAndTypeCode.contentEquals(MAINNET_PUBLIC_CODE) ||
                extendedKey.netAndTypeCode.contentEquals(MAINNET_PRIVATE_CODE) ->
                MAINNET_ADDRESS_CODE.plus(hash160)
            extendedKey.netAndTypeCode.contentEquals(TESTNET_PRIVATE_CODE) ||
                extendedKey.netAndTypeCode.contentEquals(TESTNET_PUBLIC_CODE) ->
                TESTNET_ADDRESS_CODE.plus(hash160)
            else -> throw InvalidNetCodeException(extendedKey.netAndTypeCode)
        }
        return extendedKeyBytes.plus(calculateExtendedCheckSum(extendedKeyBytes)).encodeBase58()
    }

    override fun serializeExtKey(extendedKey: ExtendedKey): String {
        return extendedKey.toExtendedKeyBytes().encodeBase58()
    }

    override fun deSerializeExtKey(extKeyBytes: ByteArray): ExtendedKey {
        var offset = IntRange(0, 4)
        val netAndTypeCode = extKeyBytes.copyOfRange(offset.first, offset.last)
        val isPrivate = when {
            netAndTypeCode.contentEquals(MAINNET_PUBLIC_CODE) ||
                netAndTypeCode.contentEquals(TESTNET_PUBLIC_CODE) -> false
            netAndTypeCode.contentEquals(MAINNET_PRIVATE_CODE) ||
                netAndTypeCode.contentEquals(TESTNET_PRIVATE_CODE) -> true
            else ->
                throw throw InvalidNetCodeException(netAndTypeCode)
        }
        offset = offset.addN(1)
        val depth = extKeyBytes.copyOfRange(offset.first, offset.last)[0]
        offset = offset.addN(4)
        val parentFingerprint = extKeyBytes.copyOfRange(offset.first, offset.last)
        offset = offset.addN(4)
        val childNumber = extKeyBytes.copyOfRange(offset.first, offset.last)
        offset = offset.addN(32)
        val chainCode = extKeyBytes.copyOfRange(offset.first, offset.last)
        offset = offset.addN(33)
        val key = extKeyBytes.copyOfRange(offset.first, offset.last)
        // TODO check on curve

        return ExtendedKey(
            null,
            netAndTypeCode,
            depth,
            parentFingerprint,
            childNumber,
            chainCode,
            if (!isPrivate) key else null,
            if (isPrivate) key else null
        )
    }
}

fun IntRange.addN(n: Int) = IntRange(this.last, this.last + n)
