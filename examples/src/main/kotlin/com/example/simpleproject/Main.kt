package com.example.simpleproject

import nl.tulipsolutions.keyderivation.ExtendedKeyWrapper
import nl.tulipsolutions.keyderivation.HARDENED_KEY_ZERO

fun main(args: Array<String>) {
    if (args.isEmpty() || args[0] == "help") {
        println("Derives children from provided xpub/xpriv or zpub/zpriv")
        println("Print the address, serialized extend key from the last derivation.")
        println("Be aware purpose and hardened key requirements are checked.")
        println("Usage:  <xpub> <childNr> <childNr> <childNr> ...")
        println("Example: zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nvgGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE H84 H0")
    } else {

        val key = ExtendedKeyWrapper(args[0])
        val leafChild = deriveChildren(args.drop(1), key)
        println("Address: ${leafChild.getAddress()}")
        println("ExtendedKey: ${leafChild.serializeExtKey()}")
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
