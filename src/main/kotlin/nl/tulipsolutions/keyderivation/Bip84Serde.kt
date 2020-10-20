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

/*
    Bip84 uses the same underlying structure as bip32 it only encodes addresses differently ( bech32 instead of base58 ).
    The BIP is specifically aimed a derivation structures for segwit.
 */
open class Bip84Serde(
    ecMathProvider: ECMathProvider
) : Bip44Serde(ecMathProvider, 84) {

    companion object {
        val MAINNET_CODE = byteArrayOf(0x04.toByte(), 0xb2.toByte())
        val TESTNET_CODE = byteArrayOf(0x04.toByte(), 0x5f.toByte())
        val MAINNET_PUBLIC_CODE = MAINNET_CODE + byteArrayOf(0x47.toByte(), 0x46.toByte())
        val MAINNET_PRIVATE_CODE = MAINNET_CODE + byteArrayOf(0x43.toByte(), 0x0C.toByte())
        val TESTNET_PUBLIC_CODE = TESTNET_CODE + byteArrayOf(0x1C.toByte(), 0xF6.toByte())
        val TESTNET_PRIVATE_CODE = TESTNET_CODE + byteArrayOf(0x18.toByte(), 0xbc.toByte())
    }

    override val MAINNET_CODE = Companion.MAINNET_CODE
    override val TESTNET_CODE = Companion.TESTNET_CODE
    override val MAINNET_PUBLIC_CODE = Companion.MAINNET_PUBLIC_CODE
    override val MAINNET_PRIVATE_CODE = Companion.MAINNET_PRIVATE_CODE
    override val TESTNET_PUBLIC_CODE = Companion.TESTNET_PUBLIC_CODE
    override val TESTNET_PRIVATE_CODE = Companion.TESTNET_PRIVATE_CODE

    override fun getNetCodeAndType(isMainNet: Boolean, isPrivate: Boolean): ByteArray =
        if (isMainNet) {
            when (isPrivate) {
                true -> MAINNET_PRIVATE_CODE
                false -> MAINNET_PUBLIC_CODE
            }
        } else when (isPrivate) {
            true -> TESTNET_PRIVATE_CODE
            false -> TESTNET_PUBLIC_CODE
        }

    override fun getAddress(extendedKey: ExtendedKey): String {
        if (extendedKey.depth.toInt() != 5) {
            throw InvalidDepthException(5, extendedKey.depth.toInt())
        }
        val program = super.hash160(extendedKey).toList().map { it.toInt() and 0xff }
        val version = 0.toByte()
        val netCodeAndType = extendedKey.netAndTypeCode
        return when {
            netCodeAndType.contentEquals(MAINNET_PUBLIC_CODE) ||
                netCodeAndType.contentEquals(MAINNET_PRIVATE_CODE) ->
                encodeSegWitAddress("bc", version, program)
            netCodeAndType.contentEquals(TESTNET_PRIVATE_CODE) ||
                netCodeAndType.contentEquals(TESTNET_PUBLIC_CODE) ->
                encodeSegWitAddress("tb", version, program)
            else -> throw InvalidNetCodeException(netCodeAndType)
        }
    }
}
