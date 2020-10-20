package nl.tulipsolutions.keyderivation

/**
 * Abstract ECPoint interface
 * Used to separate EC math implementation to allow a selection of libraries E.G: LibSecp256k1, BouncyCastle etc
 */
interface ECPointWrapper {

    fun add(wrappedPoint: ECPointWrapper): ECPointWrapper
    fun getEncoded(derEncoded: Boolean): ByteArray
}
