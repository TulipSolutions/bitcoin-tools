# Bitcoin library

A kotlin library mainly focused on key derivation and implementation of required encodings (Base58 and Bech32).

## Background

This library was started as an internal project to contain the basic tools for working with Bitcoin Addresses without 
having to import large third-party dependencies. 
Our use case was deriving public keys / addresses in a HSM compatible way.

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
      
## TODO / not implemented

- [] New private key generation: As you can do many things wrong and it was not required for our use case.
- [] Signing: Same reason why we did not implement new private key generation
- [] Implementation of derivation based on a keypath string
- [] p2wpkh in P2SH

## Licence

TODO(Apache 2.0) 