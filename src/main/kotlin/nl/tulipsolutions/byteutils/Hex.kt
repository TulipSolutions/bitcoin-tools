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

import java.lang.RuntimeException

class Hex {
    companion object {
        private const val hexCharToValueMapping = "0123456789ABCDEF"

        @JvmStatic
        fun toHexString(byteArray: ByteArray) = byteArray.map { byte ->
            val intValue = byte.toInt()
            listOf(
                // first convert the most significant bits
                hexCharToValueMapping[intValue.ushr(4).and(0x0F)],
                // convert the least significant bits
                hexCharToValueMapping[intValue.and(0x0F)]
            )
        }.flatten().joinToString("")

        @JvmStatic
        fun decode(string: String) =
            if (string.length % 2 == 0) (0..string.length - 2 step 2)
                .map {
                    string[it].decodeHexChar().shl(
                        4
                    ).or(string[it + 1].decodeHexChar()).toByte()
                }.toByteArray()
            else throw RuntimeException("Odd length hex string not supported")

        @JvmStatic
        fun Char.decodeHexChar(): Int = when (val stringIntValue = this.toShort()) {
            in 48..57 -> stringIntValue - 48
            // - 65 ascii value + 10 offset for first hex letter
            in 65..70 -> stringIntValue - 55
            // - 97 ascii value + 10 offset for first hex letter
            in 97..102 -> stringIntValue - 87
            else -> throw RuntimeException("Invalid hex character with code: $stringIntValue")
        }
    }
}
