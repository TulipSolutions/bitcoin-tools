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

import java.util.Vector
import nl.tulipsolutions.byteutils.decodeBech32
import nl.tulipsolutions.byteutils.encodeBech32

/*
    SegWit
 */
data class SegWitProgram(
    val version: Byte,
    val program: List<Int>
)

// Implemented based on:
// https://github.com/sipa/bech32/blob/master/ref/javascript/segwit_addr.js#L28
// and https://github.com/sipa/bech32/blob/master/ref/go/src/bech32/bech32.go#L141
fun List<Int>.convertBits(fromBits: Int, toBits: Int, pad: Boolean): List<Int> {
    var acc = 0
    var bits = 0
    val ret = Vector<Int>()
    val maxv = (1 shl toBits) - 1
    this.forEachIndexed { index, value ->
        if (value < 0 || (value shr fromBits) != 0) {
            throw RuntimeException("invalid data range : data[$index]=$value (fromBits=$fromBits)")
        }
        acc = (acc shl fromBits) or value
        bits += fromBits
        while (bits >= toBits) {
            bits -= toBits
            ret.addElement((acc shr bits) and maxv)
        }
    }
    if (pad) {
        if (bits > 0) {
            ret.addElement((acc shl (toBits - bits)) and maxv)
        }
    } else if (bits >= fromBits) {
        throw RuntimeException("illegal zero padding")
    } else if (((acc shl (toBits - bits)) and maxv) != 0) {
        throw RuntimeException("non-zero padding")
    }
    return ret
}

fun String.decodeSegWitAddress(hrp: String): SegWitProgram {
    val dec = this.decodeBech32()
    if (dec.hrp != hrp) {
        throw RuntimeException("invalid human-readable part: $hrp != ${dec.hrp}")
    }
    if (dec.data.isEmpty()) {
        throw RuntimeException("invalid decode data length: ${dec.data.size}")
    }
    if (dec.data[0] > 16) {
        throw RuntimeException("invalid witness version: ${dec.data[0]}")
    }
    if (this.length > 74) {
        throw RuntimeException(
            "invalid program length: ${dec.data.size} " +
                "addresses are always between 14 and 74 characters long"
        )
    }
    val res = dec.data.subList(1, dec.data.size)
        .convertBits(5, 8, false)
    if (res.size < 2 || res.size > 40) {
        throw RuntimeException("invalid convertBits length : ${res.size}")
    }
    if (dec.data[0] == 0 && res.size != 20 && res.size != 32) {
        throw RuntimeException("invalid program length for witness version 0 (per BIP141) : ${res.size}")
    }
    return SegWitProgram(version = dec.data[0].toByte(), program = res)
}

fun encodeSegWitAddress(hrp: String, version: Byte, program: List<Int>): String {
    if (version < 0 || version > 16) {
        throw RuntimeException("invalid witness version : $version")
    }
    if (program.size < 2 || program.size > 40) {
        throw RuntimeException("invalid program length : ${program.size}")
    }
    if (version == 0x00.toByte() && program.size != 20 && program.size != 32) {
        throw RuntimeException("invalid program length for witness version 0 (per BIP141) : ${program.size}")
    }
    return encodeBech32(hrp, listOf(version.toInt()) + program.convertBits(8, 5, true))
}
