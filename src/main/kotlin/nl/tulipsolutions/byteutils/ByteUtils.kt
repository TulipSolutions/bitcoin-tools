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

fun IntRange.addN(n: Int) = IntRange(this.last, this.last + n)

fun Int.toByteArray() =
    byteArrayOf(
        this.shr(24).and(0xff).toByte(),
        this.shr(16).and(0xff).toByte(),
        this.shr(8).and(0xff).toByte(),
        this.and(0xff).toByte()
    )

fun ByteArray.getIntAt(idx: Int) =
    (this[idx].toInt().and(0xff) shl 24) or
        (this[idx + 1].toInt() shl 16) or
        (this[idx + 2].toInt().and(0xff).and(0xff) shl 8) or
        this[idx + 3].toInt().and(0xff)

@kotlin.ExperimentalUnsignedTypes
fun UByteArray.getUIntAt(idx: Int) =
    (this[idx].toUInt() shl 24) or
        (this[idx + 1].toUInt() shl 16) or
        (this[idx + 2].toUInt() shl 8) or
        this[idx + 3].toUInt()

@kotlin.ExperimentalUnsignedTypes
fun UInt.toUByteArray() =
    ubyteArrayOf(
        (this shr 24).toUByte(),
        (this shr 16).toUByte(),
        (this shr 8).toUByte(),
        this.toUByte()
    )
