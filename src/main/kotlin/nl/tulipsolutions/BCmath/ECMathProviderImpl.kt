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

package nl.tulipsolutions.BCmath

import java.math.BigInteger
import nl.tulipsolutions.keyderivation.ECMathProvider
import nl.tulipsolutions.keyderivation.ECPointWrapper
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.crypto.params.ECDomainParameters

object ECMathProviderImpl : ECMathProvider {
    @JvmStatic
    private val curveParams: X9ECParameters = SECNamedCurves.getByName("secp256k1")

    @JvmStatic
    private val domain: ECDomainParameters =
        ECDomainParameters(curveParams.curve, curveParams.g, curveParams.n, curveParams.h)

    override val curveN: BigInteger
        get() = curveParams.n

    /**
     * Takes an BigInteger does an EC multiplication with curve Generator and creates a new ECPointWrapper
     *
     * @param value the [BigInteger] value to multiply with
     * @return [ECPointWrapperImpl]
     */
    override fun multiplyByG(value: BigInteger): ECPointWrapperImpl {
        return ECPointWrapperImpl(this.curveParams.g.multiply(value))
    }

    /**
     * Takes an encoded point converts it to a bouncycastle ECPoint and returns the handler
     *
     * @param encodedPoint encoded ECpoint
     * @return [ECPointWrapperImpl]
     */
    override fun decodePoint(encodedPoint: ByteArray): ECPointWrapper =
        ECPointWrapperImpl(this.curveParams.curve.decodePoint(encodedPoint))

    /**
     * Public point verification
     * raises the errors if any otherwise it will return the wrapper
     *
     * @param wrappedPoint wrapped ECpoint
     * @return [ECPointWrapperImpl]
     */
    override fun validatePublicPoint(wrappedPoint: ECPointWrapper): ECPointWrapper {
        try {
            this.domain.validatePublicPoint((wrappedPoint as ECPointWrapperImpl).point)
        } catch (e: RuntimeException) {
            throw e
        }
        return wrappedPoint
    }
}
