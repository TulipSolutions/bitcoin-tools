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

package com.example.simpleproject

import java.lang.RuntimeException
import nl.tulipsolutions.keyderivation.Bip32Serde
import nl.tulipsolutions.keyderivation.Bip44Serde
import nl.tulipsolutions.keyderivation.Bip84Serde
import nl.tulipsolutions.keyderivation.ExtendedKeySerdeInterface
import nl.tulipsolutions.keyderivation.ExtendedKeyWrapper
import nl.tulipsolutions.keyderivation.HARDENED_KEY_ZERO
import nl.tulipsolutions.mnemonic.toSeed

val availableSerdes = listOf("bip32", "bip44", "bip84")
val network = listOf("main", "test")

fun help() {
    println("Derives children from provided xpub/xpriv or zpub/zpriv")
    println("Print the address, serialized extend key from the last derivation.")
    println("Be aware purpose and hardened key requirements are checked.")
    println("   Derivation:")
    println("Usage: derive <xpub> <childNr> <childNr> <childNr> ...")
    println("Example: derive zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nvgGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE H84 H0")
    println("   Using a seed:")
    println("Usage: fromSeed <${availableSerdes.joinToString(" | ")} > < ${network.joinToString(" | ")} > <passphrase> <Mnemonic>  ...")
    println("Example: fromSeed bip32 main TREZOR \"abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about\"")
}

fun main(args: Array<String>) {

    if (args.isEmpty() || args[0] == "help") {
        help()
    } else if (args[0] == "derive") {
        val key = ExtendedKeyWrapper(args[0])
        val leafChild = deriveChildren(args.drop(1), key)
        println("Address: ${leafChild.getAddress()}")
        println("ExtendedKey: ${leafChild.serializeExtKey()}")
    } else if (args[0] == "fromSeed") {
        val serde: Pair<ExtendedKeySerdeInterface, Int> = when (args[1]) {
            "bip32" -> Pair(Bip32Serde(), 0)
            "bip44" -> Pair(Bip44Serde(), Bip44Serde().purpose)
            "bip84" -> Pair(Bip84Serde(), Bip84Serde().purpose)
            else -> throw RuntimeException("Invalid arg expected one of ${availableSerdes.joinToString(" | ")} was $args[1]")
        }
        val isMain = when (args[2]) {
            "main" -> true
            "test" -> false
            else -> throw RuntimeException("Invalid arg expected one of ${network.joinToString(" | ")} was $args[1]")
        }
        var path = "m/"
        val rootKey = ExtendedKeyWrapper(args[4].split(" ").toSeed(args[3]), serde.first, isMain)
        println("Path: $path")
        println("Master ExtendedKey: ${rootKey.serializeExtKey()}")

        val purposeChild = rootKey.deriveChild(HARDENED_KEY_ZERO + serde.second)
        path += "${serde.second}'/"
        println("Path: $path")
        println("Account purpose ${serde.second} ExtendedKey: ${purposeChild.serializeExtKey()}")

        val coinTypeChild = purposeChild.deriveChild(HARDENED_KEY_ZERO + if (isMain) 0 else 1)
        path += "${(if (isMain) 0 else 1)}'/"
        println("Path: $path")
        println("coinType Child derived ExtendedKey: ${coinTypeChild.serializeExtKey()}")

        val account0Child = coinTypeChild.deriveChild(HARDENED_KEY_ZERO + 0)
        path += "0'/"
        println("Path: $path")
        println("Account 0 Child derived ExtendedKey: ${account0Child.serializeExtKey()}")

        val firstReceivingKey = account0Child.deriveChild(0).deriveChild(0)
        path += "0/0"
        println("Path: $path")
        println("First receiving key ExtendedKey: ${firstReceivingKey.serializeExtKey()}")
        println("First receiving address: ${firstReceivingKey.getAddress()}")
    } else {
        help()
    }
}

fun deriveChildren(args: List<String>, key: ExtendedKeyWrapper): ExtendedKeyWrapper {
    return if (args.isEmpty()) {
        key
    } else {
        val childNumer =
            if (args.first().toUpperCase().startsWith("H") || args.first().startsWith("'"))
                HARDENED_KEY_ZERO + args.first().substring(1).toInt()
            else
                args.first().toInt()
        deriveChildren(args.drop(1), key.deriveChild(childNumer))
    }
}
