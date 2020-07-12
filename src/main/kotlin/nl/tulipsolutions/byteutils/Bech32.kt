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

import java.util.Vector

// code below is based on the go samples of tnakagawa
// https://github.com/sipa/bech32/blob/master/ref/go/src/bech32/bech32.go
val CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"
val GEN = arrayOf(0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3)
val SIX_ZEROS_LIST = IntArray(6).toList()

data class HrpDataContainer(
    val hrp: String,
    val data: List<Int>
)

fun List<Int>.bech32Polymod(): Int {
    var chk = 1
    this.forEach { v ->
        val top = chk shr 25
        chk = ((chk and 0x1ffffff) shl 5) xor v
        for (i in 0 until 5) {
            if ((top shr i) and 1 > 0) {
                chk = chk xor GEN[i]
            }
        }
    }
    return chk
}

fun String.bech32HRPExpand(): List<Int> = (
    this.map { it.toInt() shr 5 } +
        listOf(0) +
        this.map { it.toInt() and 31 }
    )

fun bech32VerifyChecksum(hrp: String, data: List<Int>) =
    (hrp.bech32HRPExpand() + data).bech32Polymod() == 1

fun bech32CreateChecksum(hrp: String, data: List<Int>): List<Int> {
    val polymod = ((hrp.bech32HRPExpand() + data + SIX_ZEROS_LIST)
        .bech32Polymod()) xor 1
    return (0..5).map { index ->
        (polymod shr (5 * (5 - index))) and 31
    }
}

fun encodeBech32(hrp: String, data: List<Int>): String {
    if (hrp.length + data.size + 7 > 90) {
        throw RuntimeException("too long : hrp length=${hrp.length}, data length=${data.size}")
    }
    if (hrp.isEmpty()) {
        throw RuntimeException("invalid hrp : hrp=$hrp")
    }
    hrp.forEachIndexed { index, value ->
        val pCharCode = value.toInt()
        if (pCharCode < 33 || pCharCode > 126) {
            throw RuntimeException("invalid character human-readable part : bechString[$index]=$value")
        }
    }
    if (hrp.toLowerCase() != hrp && hrp.toUpperCase() != hrp) {
        throw RuntimeException("Mixed character casing")
    }
    val lowerHrp = hrp.toLowerCase()
    val isLower = lowerHrp == hrp

    val combined = data + bech32CreateChecksum(lowerHrp, data)
    val ret = hrp + '1' + combined.map {
        CHARSET[it]
    }.joinToString("")
    return if (isLower) ret else ret.toUpperCase()
}

fun String.decodeBech32(): HrpDataContainer {
    if (this.length > 90) {
        throw RuntimeException("too long : len=${this.length}")
    }
    if (this.toLowerCase() != this && this.toUpperCase() != this) {
        throw RuntimeException("Mixed character casing")
    }
    val lowerBechString = this.toLowerCase()
    val isLower = lowerBechString == this
    val pos = lowerBechString.lastIndexOf('1')
    if (pos < 1 || pos + 7 > lowerBechString.length) {
        throw RuntimeException("separator '1' at invalid position : pos=$pos , len=${lowerBechString.length}")
    }
    val hrp = lowerBechString.substring(0, pos)
    hrp.forEachIndexed { index, value ->
        val pCharCode = value.toInt()
        if (pCharCode < 33 || pCharCode > 126) {
            throw RuntimeException("invalid character human-readable part : bechString[$index]=$value")
        }
    }
    val data = Vector<Int>()
    for (p in pos + 1 until lowerBechString.length) {
        val d = CHARSET.indexOf(lowerBechString[p])
        if (d == -1) {
            throw RuntimeException("invalid character data part : bechString[$p]=${lowerBechString[p]}")
        }
        data.addElement(d)
    }
    if (!bech32VerifyChecksum(hrp, data)) {
        throw RuntimeException("invalid checksum")
    }
    return HrpDataContainer(hrp = if (isLower) hrp else hrp.toUpperCase(), data = data.subList(0, data.size - 6))
}
