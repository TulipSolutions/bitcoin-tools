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

import java.math.BigInteger
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import nl.tulipsolutions.byteutils.getIntAt
import nl.tulipsolutions.byteutils.toByteArray
import org.bouncycastle.asn1.sec.SECNamedCurves
import org.bouncycastle.crypto.digests.RIPEMD160Digest
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.math.ec.ECPoint
import org.bouncycastle.util.encoders.Hex

const val HARDENED_KEY_ZERO = -Int.MAX_VALUE - 1

data class ExtendedKey(
    val parentKey: ExtendedKey?,
    val netAndTypeCode: ByteArray,
    val depth: Byte,
    var parentFingerprint: ByteArray,
    val childNumber: ByteArray,
    val chainCode: ByteArray,
    // This allows us to derive the public key from the private key if it is provided
    val _publicKey: ByteArray?,
    val privateKey: ByteArray?
) {

    private val curveParams = SECNamedCurves.getByName("secp256k1")
    private val domain = ECDomainParameters(curveParams.curve, curveParams.g, curveParams.n, curveParams.h)
    val publicKey: ByteArray =
        if (privateKey != null) calculateAndCheckPublicKeyFromPrivate(privateKey).getEncoded(true) else _publicKey!!

    init {
        if (privateKey != null && privateKey.size != 33) {
            throw RuntimeException("private key should be 33 bytes long")
        }
    }

    private fun nonChecksumBytes() =
        (netAndTypeCode + depth + parentFingerprint + childNumber + chainCode + (privateKey ?: publicKey))

    private fun calculateCheckSum(): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        val sha256Digest = digest.digest(
            nonChecksumBytes()
        )
        return digest.digest(sha256Digest).copyOfRange(0, 4)
    }

    private fun calculateKeyHashFingerprint(key: ByteArray): ByteArray {
        val sha256Digest = MessageDigest.getInstance("SHA-256")
            .digest(key)
        val digest = RIPEMD160Digest()
        val retValue = ByteArray(digest.digestSize)
        digest.update(sha256Digest, 0, sha256Digest.size)
        digest.doFinal(retValue, 0)

        return retValue.slice(0..3).toByteArray()
    }

    private fun calculateAndCheckPublicKeyFromPrivate(privateBytes: ByteArray): ECPoint {
        val kpar = privateBytes.copyOfRange(1, privateBytes.size)
        val parentPub = this.curveParams.g.multiply(
            BigInteger(1, kpar).mod(this.curveParams.n)
        )
        return try {
            // Bouncy castle does all the checks for us.
            this.domain.validatePublicPoint(parentPub)
        } catch (e: RuntimeException) {
            throw RuntimeException(
                "the resulting key is invalid, and one should proceed with the next value for sequence"
            )
        }
    }

    private fun isHardenedKey() = (this.childNumber.getIntAt(0).and(0x80000000.toInt()) != 0)

    fun deriveChild(sequence: Int) =
        if (sequence.and(0x80000000.toInt()) != 0) {
            // Neutered key derivation path
            if (this.privateKey == null || this.privateKey.isEmpty()) {
                throw RuntimeException("Deriving a hardened key requires a private key")
            }
            // Derive a private child with neutered sequence
            derivePrivateChild(sequence)
            // If There is a private key we always derive the private child and get the pubkey from there
            // Private parent key → private child key
        } else if (this.privateKey != null && this.privateKey.isNotEmpty()) {
            derivePrivateChild(sequence)
            // If we do not have a private key we take the public derivation path
            // Public parent key → public child key
        } else {
            derivePublicChildFromPublic(sequence)
        }

    // Private parent key → private child key
    private fun derivePrivateChild(sequence: Int): ExtendedKey {
        val kpar = this.privateKey!!.copyOfRange(1, this.privateKey.size)
        val parentPub = this.curveParams.g.multiply(
            BigInteger(1, kpar).mod(this.curveParams.n)
        )
        val mac = Mac.getInstance("HmacSHA512")
        mac.reset()
        val key = SecretKeySpec(this.chainCode, "HmacSHA512")
        mac.init(key)
        // HMAC-SHA512(Key = cpar, Data)
        val I = mac.doFinal(
            // Check whether i ≥ 2^31 (whether the child is a hardened key)
            if (sequence.and(0x80000000.toInt()) != 0) {
                // Data = 0x00 || ser256(kpar) || ser32(i)
                byteArrayOf(0x00) + kpar + sequence.toByteArray()
            } else {
                // Use pub key instead of serP(point(kpar))
                val serP = parentPub
                    // serP(..)
                    .getEncoded(true)
                // Data = serP(point(kpar)) || ser32(i)
                serP + sequence.toByteArray()
            }
        )
        // Split I into two 32-byte sequences, IL and IR
        val IL = I.copyOf(32)
        val IR = I.copyOfRange(32, 64)
        // parse256(IL)
        val parse256IL = BigInteger(1, IL)
        if (parse256IL >= this.curveParams.n) {
            throw RuntimeException("generated left bits for child are larger than curveN")
        }
        // parse256(IL) + kpar (mod n)
        val ki = parse256IL.add(BigInteger(1, kpar)).mod(this.curveParams.n)
        // The returned chain code ci is IR.
        val ci = IR
        // In case parse256(IL) ≥ n or ki = 0
        if (parse256IL >= this.domain.n || ki == BigInteger.ZERO) {
            throw RuntimeException(
                "the resulting key is invalid, and one should proceed with the next value for sequence"
            )
        }
        // Also add the pubkey
        val pub = this.curveParams.g.multiply(ki).getEncoded(true)
        val keyBytes =
            (if (ki.toByteArray().size == 32) byteArrayOf(0x00) else byteArrayOf()) + ki.toByteArray()
        return this.copy(
            parentKey = this,
            depth = (this.depth.toInt() + 1).toByte(),
            childNumber = sequence.toByteArray(),
            chainCode = ci,
            _publicKey = pub,
            privateKey = keyBytes,
            parentFingerprint = calculateKeyHashFingerprint(parentPub.getEncoded(true))
        )
    }

    private fun derivePublicChildFromPublic(sequence: Int): ExtendedKey {
        val mac = Mac.getInstance("HmacSHA512")
        mac.reset()
        // Calculate I = HMAC-SHA512(Key = cpar, Data = serP(Kpar) || ser32(i)).
        val key = SecretKeySpec(this.chainCode, "HmacSHA512")
        mac.init(key)
        val Kpar = this.curveParams.curve.decodePoint(this.publicKey)
        // serP(Kpar) || ser32(i))
        val data = Kpar.getEncoded(true) + sequence.toByteArray()
        val I = mac.doFinal(
            data
        )
        // Split I into two 32-byte sequences, IL and IR.
        val IL = I.copyOf(32)
        val IR = I.copyOfRange(32, 64)
        // parse256(IL)
        val parse256IL = BigInteger(1, IL)
        if (parse256IL >= this.curveParams.n) {
            throw RuntimeException("generated left bits for child are large than curveN")
        }
        // point(parse256(IL))
        val intermediate = parse256IL.mod(this.curveParams.n)
        // point()
        val point = this.curveParams.g.multiply(intermediate)
        // point(parse256(IL)) + Kpar
        val ki = point.add(Kpar)
        // The returned chain code ci is IR.
        val ci = IR
        // In case parse256(IL) ≥ n or Ki is the point at infinity, the resulting key is invalid,
        // and one should proceed with the next value for i.
        val kiChecked = try {
            // Bouncy castle does all the checks for us.
            this.domain.validatePublicPoint(ki)
        } catch (e: RuntimeException) {
            throw RuntimeException(
                "the resulting key is invalid, and one should proceed with the next value for sequence"
            )
        }
        val pub = kiChecked.getEncoded(true)
        return this.copy(
            parentKey = this,
            depth = (this.depth.toInt() + 1).toByte(),
            childNumber = sequence.toByteArray(),
            chainCode = ci,
            _publicKey = pub,
            parentFingerprint = calculateKeyHashFingerprint(this.publicKey)
        )
    }

    fun toExtendedKeyBytes() = nonChecksumBytes().plus(calculateCheckSum())

    override fun toString() =
        Hex.toHexString(
            nonChecksumBytes().plus(calculateCheckSum())
        ).toUpperCase()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExtendedKey
        // Note: left out parentKey comparison as this is not known on initial extended key deserialization
        if (!netAndTypeCode.contentEquals(other.netAndTypeCode)) return false
        if (depth != other.depth) return false
        if (!parentFingerprint.contentEquals(other.parentFingerprint)) return false
        if (!childNumber.contentEquals(other.childNumber)) return false
        if (!chainCode.contentEquals(other.chainCode)) return false
        if (privateKey != null) {
            if (other.privateKey == null) return false
            if (!privateKey.contentEquals(other.privateKey)) return false
        } else if (other.privateKey != null) return false
        if (!publicKey.contentEquals(other.publicKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = parentKey?.hashCode() ?: 0
        result = 31 * result + netAndTypeCode.contentHashCode()
        result = 31 * result + depth
        result = 31 * result + parentFingerprint.contentHashCode()
        result = 31 * result + childNumber.contentHashCode()
        result = 31 * result + chainCode.contentHashCode()
        result = 31 * result + (privateKey?.contentHashCode() ?: 0)
        result = 31 * result + publicKey.contentHashCode()
        return result
    }
}
