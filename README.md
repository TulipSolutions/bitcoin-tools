# Bitcoin library

[![Build Status](https://travis-ci.org/TulipSolutions/bitcoin-tools.svg?branch=master)](https://travis-ci.org/TulipSolutions/bitcoin-tools)

A kotlin library mainly focused on key derivation and implementation of required encodings (Base58 and Bech32).

## Background

This library was started as an internal project to contain the basic tools for working with HDWallets and Bitcoin 
Addresses without having to import large third-party dependencies.

## Currently implemented

* [Bip32](src/main/kotlin/nl/tulipsolutions/keyderivation/Bip32Serde.kt) HD key derivation
* [Bip39](src/main/kotlin/nl/tulipsolutions/mnemonic/Bip39.kt) Entropy to Mnemonic and Mnemonic to seed conversion
* [Bip44](src/main/kotlin/nl/tulipsolutions/keyderivation/Bip44Serde.kt) Purpose value derivation
* [Bip84](src/main/kotlin/nl/tulipsolutions/keyderivation/Bip84Serde.kt) Segwit key derivation and address generation
* [BIP173](src/main/kotlin/nl/tulipsolutions/keyderivation/BIP173.kt) Decode/encoding Base32/Bech32

## Structure

The main class that binds the respective BIP to key derivation is:
[ExtendedKeyWrapper](src/main/kotlin/nl/tulipsolutions/keyderivation/ExtendedKeyWrapper.kt)

Based on the extendedKey byteArray or String that is provided it either uses:
[BIP32Serde](src/main/kotlin/nl/tulipsolutions/keyderivation/Bip32Serde.kt)
or
[BIP84Serde](src/main/kotlin/nl/tulipsolutions/keyderivation/Bip84Serde.kt)

With the main differences being that [BIP32Serde](src/main/kotlin/nl/tulipsolutions/keyderivation/Bip32Serde.kt)
uses [Base58](src/main/kotlin/nl/tulipsolutions/byteutils/Base58.kt) to encode the address 

and
 
[BIP84Serde](src/main/kotlin/nl/tulipsolutions/keyderivation/Bip84Serde.kt)
using [BIP173](src/main/kotlin/nl/tulipsolutions/keyderivation/BIP173.kt) to encode the address and 
enforces BIP44 with purpose 84' when deriving children.

## Usage with Bazel

To install Bazel itself:

* [Install Bazel's dependencies](https://docs.bazel.build/install.html)
* [Install Bazelisk](https://github.com/bazelbuild/bazelisk/releases) (optional, but recommended)

## How to use in your project

1. Add the following to your Bazel workspace:
```
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

git_repository(
    name = "nl_tulipsolutions_bitcoin_tools",
    commit = "b25a2615d94d7d18c46ec27a16330f7891b8b893",
    remote = "https://github.com/TulipSolutions/bitcoin-tools",
)
```
See also [example WORKSPACE](examples/WORKSPACE)

2. Add a dependency in the Build file of your project:
```
deps = [
        "@nl_tulipsolutions_bitcoin_tools//src/main/kotlin/nl/tulipsolutions/keyderivation",
    ],
``` 
or if you only need the byteutils:
```
deps = [
        "@nl_tulipsolutions_bitcoin_tools//src/main/kotlin/nl/tulipsolutions/byteutils",
    ],
``` 
See also [example Build.bazel](examples/src/main/kotlin/com/example/simpleproject/BUILD.bazel)

3. The extended key wrapper can be used as follow:

```kotlin
// m
val zpriv = "zprvAdG4iTXWBoARxkkzNpNh8r6Qag3irQB8PzEMkAFeTRXxHpbF9z4QgEvBRmfvqWvGp42t42nvgGpNgYSJA9iefm1yYNZKEm7z6qUWCroSQnE"
// The wrapper currently supports xpriv, xpub, tpub, tpriv which will enforce BIP32Serde
// It also supports zpriv, zpub, vpriv, vpub which will enforce BIP84Serde
val extendedKey = ExtendedKeyWrapper(zpriv)
println(extendedKey.getAddress())
println(extendedKey.serializeExtKey())
println(extendedKey.getPublicKey())

// Be aware hardened children are > 0x80000000 and will require a private key
// note this is also defined in keyderivation/ExtendedKey.kt
const val HARDENED_KEY_ZERO = 0x80000000.toInt()
// also note the when using a zpriv, zpub, vpriv, vpub the first child from the master should be H84 due to purpose spec
// m/H84
val child = extendedKey.deriveChild(HARDENED_KEY_ZERO + 84)
println(child.getAddress())
println(child.serializeExtKey())
println(child.getPublicKey())

// m/H84/0
val childOfChild = child.deriveChild(HARDENED_KEY_ZERO + 0)
println(childOfChild.getAddress())
println(childOfChild.serializeExtKey())
println(childOfChild.getPublicKey())

// ExtendedKey From seed
val mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
val passphrase = ""
val extendedKeyFromMnemonic = ExtendedKeyWrapper(
    seed = vector.mnemonic.split(" ").toSeed(passphrase),
    serde = Bip32Serde(),
    isMainNet = true
)
println(extendedKeyFromMnemonic.serializeExtKey().decodeBase58())
```

See also [example Main.kt](examples/src/main/kotlin/com/example/simpleproject/Main.kt)

## TODO / currently not implemented

- New private key generation: As you can do many things wrong and was not required for the use case I was working on.  
- Signing: Same reason why we did not implement new private key generation
- Implementation of derivation based on a keypath string
- p2wpkh in P2SH
- Move EC math to a separate class and use an interface to allow the developer to choose his preferred implementation.
