package nl.tulipsolutions.keyderivation

import java.math.BigInteger

interface ECMathProvider {
    fun multiplyByG(value: BigInteger): ECPointWrapper
    val curveN: BigInteger
    fun decodePoint(encodedPoint: ByteArray): ECPointWrapper
    fun validatePublicPoint(wrappedPoint: ECPointWrapper): ECPointWrapper
}
