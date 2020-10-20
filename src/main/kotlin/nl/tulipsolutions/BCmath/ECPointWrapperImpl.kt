package nl.tulipsolutions.BCmath

import nl.tulipsolutions.keyderivation.ECPointWrapper
import org.bouncycastle.math.ec.ECPoint

data class ECPointWrapperImpl(val point: ECPoint) : ECPointWrapper {
    /**
     * Takes a ECPointHandler, bouncycastle EC sum and returns a new ECPointWrapper
     *
     * @param wrappedPoint encoded ECpoint
     * @return [ECPointWrapperImpl]
     */
    override fun add(wrappedPoint: ECPointWrapper): ECPointWrapper =
        ECPointWrapperImpl(point.add((wrappedPoint as ECPointWrapperImpl).point))

    /**
     * Returns the ECPoint in encoded format
     *
     * @param derEncoded whether or not to use DER encoding
     * @return [ByteArray]
     */
    override fun getEncoded(derEncoded: Boolean): ByteArray = point.getEncoded(derEncoded)
}
