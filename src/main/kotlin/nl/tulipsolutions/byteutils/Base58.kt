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

package nl.tulipsolutions.byteutils

const val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
val ALPHABET_SEQUENCE = ALPHABET.asSequence()
const val ZERO_BYTE = 0x00.toByte()

fun String.decodeBase58(): ByteArray {
    // For each input byte in the array of input bytes:
    val base58Numbers = this.map { c ->
        // If a mapping does not exist, return an error code.
        if (!ALPHABET_SEQUENCE.contains(c)) {
            throw RuntimeException("Found non-base58 character while decoding")
        }
        ALPHABET_SEQUENCE.indexOf(c)
    }.toMutableList()
    // Count leading zeroes
    var leadingZeroCount = 0
    while (leadingZeroCount < base58Numbers.size && base58Numbers[leadingZeroCount] == 0) {
        ++leadingZeroCount
    }
    val temp = base58Numbers.mapIndexed { index, _ ->
        var carry = 0
        for (i in index + leadingZeroCount until base58Numbers.size) {
            val digit58 = base58Numbers[i] and 0xFF
            val temp = carry * 58 + digit58
            base58Numbers[i] = (temp / 256)
            carry = temp % 256
        }
        carry.toByte()
    }.toByteArray()
    // deduct the amount of consecutive trailing zeros to get the actual length
    var actualLength = this.length - 1
    while (actualLength > 0 && temp[actualLength] == ZERO_BYTE) {
        actualLength--
    }
    // copy output with actual length as the initial reserved length is always larger
    return ByteArray(leadingZeroCount) + temp.copyOf(actualLength + 1).reversedArray()
}

// converted from : https://gist.github.com/vrotaru/1753908
fun ByteArray.encodeBase58(): String {

    // Count leading zeroes
    var leadingZeroCount = 0
    while (leadingZeroCount < this.size && this[leadingZeroCount] == ZERO_BYTE) {
        ++leadingZeroCount
    }

    var temp = ByteArray(this.size * 2)
    var j = temp.size
    var startAt = leadingZeroCount

    while (startAt < this.size) {
        var carry = 0
        for (i in startAt until this.size) {
            val digit256 = this[i].toInt() and 0xFF
            val temp = carry * 256 + digit256
            this[i] = (temp / 58).toByte()
            carry = temp % 58
        }
        if (this[startAt] == ZERO_BYTE) {
            startAt++
        }
        temp[--j] = ALPHABET[
            carry
        ].toByte()
    }
    // strip extra 1
    while (j < temp.size && temp[j] == ALPHABET[0].toByte()) {
        ++j
    }

    while (--leadingZeroCount >= 0) {
        temp[--j] = ALPHABET[0].toByte()
    }
    return String(temp.copyOfRange(j, temp.size))
}
