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

import nl.tulipsolutions.byteutils.ZERO_BYTE

// See: https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki
open class Bip44Serde(
    private val purpose: Int = 44,
    private val MAINNET_PUBLIC_CODE: ByteArray = this.MAINNET_PUBLIC_CODE,
    private val MAINNET_PRIVATE_CODE: ByteArray = this.MAINNET_PRIVATE_CODE,
    private val TESTNET_PUBLIC_CODE: ByteArray = this.TESTNET_PUBLIC_CODE,
    private val TESTNET_PRIVATE_CODE: ByteArray = this.TESTNET_PRIVATE_CODE
) : Bip32Serde(
    MAINNET_PUBLIC_CODE, MAINNET_PRIVATE_CODE,
    TESTNET_PUBLIC_CODE, TESTNET_PRIVATE_CODE
) {

    private val PURPOSE_VALUE = HARDENED_KEY_ZERO + purpose
    // BIP44 only introduces a derivation path spec m / purpose' / coin_type' / account' / change / address_index
    override fun deriveChildBIPRules(extendedKey: ExtendedKey, sequence: Int): ExtendedKey {
        if (extendedKey.depth.toInt() in 0..2 && sequence.and(HARDENED_KEY_ZERO) == 0) {
            throw HardenedDerivationRequiredException(extendedKey.depth.toInt(), sequence)
        } else if (extendedKey.depth == ZERO_BYTE && sequence != PURPOSE_VALUE) {
            throw InvalidPurposeException(purpose, sequence)
        }
        return extendedKey.deriveChild(sequence)
    }

    override fun getAddress(extendedKey: ExtendedKey): String {
        if (extendedKey.depth.toInt() != 5) {
            throw InvalidDepthException(5, extendedKey.depth.toInt())
        }
        return super.getAddress(extendedKey)
    }
}
