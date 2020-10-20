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

interface ExtendedKeySerdeInterface {

    fun getAddress(extendedKey: ExtendedKey): String
    fun serializeExtKey(extendedKey: ExtendedKey): String
    fun toHexString(extendedKey: ExtendedKey): String
    fun deSerializeExtKey(extKeyBytes: ByteArray): ExtendedKey
    fun deSerializeExtKey(extKeyEncoded: String): ExtendedKey
    fun deriveChildBIPRules(extendedKey: ExtendedKey, sequence: Int): ExtendedKey
    fun hash160(extendedKey: ExtendedKey): ByteArray

    fun getNetCodeAndType(isMainNet: Boolean, isPrivate: Boolean): ByteArray

    val MAINNET_CODE: ByteArray
    val TESTNET_CODE: ByteArray

    val MAINNET_PUBLIC_CODE: ByteArray

    val MAINNET_PRIVATE_CODE: ByteArray

    val TESTNET_PUBLIC_CODE: ByteArray

    val TESTNET_PRIVATE_CODE: ByteArray
}
