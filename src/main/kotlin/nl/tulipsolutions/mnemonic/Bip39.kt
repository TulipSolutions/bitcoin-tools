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

package nl.tulipsolutions.mnemonic

import java.lang.RuntimeException
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import nl.tulipsolutions.mnemonic.wordlist.Languages
import nl.tulipsolutions.mnemonic.wordlist.languageWordListMap
import nl.tulipsolutions.mnemonic.wordlist.mapValuesToMnemonicWords
import nl.tulipsolutions.mnemonic.wordlist.normalize

val validMnemonicLength = arrayOf(12, 15, 18, 21, 24)
val checksumEntropyByteArrayLengthMap = mapOf<Int, Int>(
    12 to 128 / 8,
    15 to 160 / 8,
    18 to 192 / 8,
    21 to 224 / 8,
    24 to 256 / 8
)

fun Array<String>.toEntropyCheckSumPair(language: Languages): Pair<ByteArray, Byte> {
    val wordListMap = languageWordListMap[language]
    if (this.size !in validMnemonicLength) {
        throw RuntimeException("Unsupported mnemonic sentence length, length was ${this.size}")
    }
    var temp: Int = 0
    val byteArray = ByteArray(checksumEntropyByteArrayLengthMap[this.size] ?: error("Should never happen"))
    if (wordListMap == null) {
        throw RuntimeException("WordListMap was null")
    }
    val integerValues = this.map {
        if (wordListMap.containsKey(it.normalize())) wordListMap[it.normalize()]
        else throw WordNotFoundInWordList(it, language)
    }
    var byteArrayOffset = 0
    // Counter to keep track how many bits we added to temp
    var bitsInTemp = 0
    integerValues.forEach { binaryValue ->
        if (binaryValue == null) {
            throw RuntimeException("BinaryValue was empty this should never happen")
        }
        // iterate over 11 bits (.. operator is inclusive)
        for (bitNr in 0..10) {
            // When our temp variable is full we can add it to the array
            if (bitsInTemp == 8) {
                byteArray[byteArrayOffset] = temp.toByte()
                byteArrayOffset += 1
                temp = 0
                bitsInTemp = 0
            }
            temp = (temp shl 1).or(
                // select the bit we want to add to temp starting with the most significant
                (binaryValue shr 10 - bitNr).and(1)
                // shift the bit 1 to the left to make room for the next one
            )
            bitsInTemp += 1
        }
    }
    // Last check either returns the pair or throws an exception if it is not valid
    return Pair(byteArray, temp.toByte()).checkBip39CheckSum()
}

// takes byte input and converts it to 11 bit ints
fun ByteArray.toMnemonicValues(): List<Int> {
    this.checkBip39Checksum()
    val valuesArray: MutableList<Int> = MutableList((this.size * 8) / 11) { 0 }
    val checkSumBits = (this.size - 1) * 8 / 32
    // Counter to keep track how many bits we added to temp
    var bitsSet: Int = 0
    var offset = 0
    this.sliceArray(0 until this.size - 1).forEach { byte ->
        // if we are at the last element we only need to add Entropy / 32 bits
        val bitsToAdd = kotlin.math.min(11 - bitsSet, 8)
        // shift left to make room for new bits
        valuesArray[offset] = (valuesArray[offset] shl bitsToAdd).or(
            // select the bits we need
            byte.toInt().and(0xff).shr(8 - bitsToAdd)
        )
        bitsSet += bitsToAdd
        // when we add 11 bits move to the next integer
        if (bitsSet == 11) {
            offset += 1
            bitsSet = 0
            // Add the remaining least significant bits from the byte to the new offset
            if (bitsToAdd in 1..7) {
                valuesArray[offset] = byte.toInt().and(0xff).and(255 ushr bitsToAdd)
                bitsSet = (8 - bitsToAdd)
            }
        }
    }
    valuesArray[valuesArray.lastIndex] = valuesArray[valuesArray.lastIndex].shl(checkSumBits) +
        this.last().toInt().and(0xff)
    return valuesArray
}

// Returns a bytearray with the added checksum
fun ByteArray.addBip39Checksum() = this + (MessageDigest.getInstance("SHA-256")
    .digest(this)[0].toInt().and(0xff)
    // mask the bits we do not need
    .shr(8 - (this.size * 8 / 32)).toByte())

fun Pair<ByteArray, Byte>.checkBip39CheckSum() =
    if (this.second == this.first.addBip39Checksum().last()) this
    else throw(InvalidMnemonicCheckSum(this.second.toInt(), this.first.addBip39Checksum().last().toInt()))

// Expects a bytearray that includes the checksum
fun ByteArray.checkBip39Checksum() =
    if (this.sliceArray(0 until this.size - 1).addBip39Checksum().last() == this.last()) this
    else throw InvalidMnemonicCheckSum(
        this.last().toInt(),
        this.sliceArray(0 until this.size - 1).addBip39Checksum().last().toInt()
    )

fun ByteArray.toMnemonicWords(language: Languages) =
    this.addBip39Checksum().toMnemonicValues().mapValuesToMnemonicWords(language)

// A user may decide to protect their mnemonic with a passphrase.
// If a passphrase is not present, an empty string "" is used instead.
fun List<String>.toSeed(passphrase: String = "", separator: String = " "): ByteArray {
    val salt = ("mnemonic" + passphrase).normalize().toByteArray()
    val spec = PBEKeySpec(
        this.joinToString(separator).normalize().toCharArray(),
        salt,
        // The iteration count is set to 2048 and HMAC-SHA512 is used as the pseudo-random function.
        2048,
        // The length of the derived key is 512 bits (= 64 bytes).
        64 * 8
    )
    val f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")

    return f.generateSecret(spec).encoded
}
