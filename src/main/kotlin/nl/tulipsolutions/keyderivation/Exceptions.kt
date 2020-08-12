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

import java.lang.RuntimeException
import nl.tulipsolutions.byteutils.Hex
import nl.tulipsolutions.byteutils.toByteArray

class InvalidDepthException(requiredDepth: Int, depth: Int) : RuntimeException() {
    override val message = "Depth should be $requiredDepth but was $depth"
}

class InvalidPurposeException(requiredPurpose: Int, purpose: Int) : RuntimeException() {
    override val message =
        "BIP$requiredPurpose purpose should be $requiredPurpose' but was ${Hex.toHexString(purpose.toByteArray())}"
}

class InvalidNetCodeException(netCode: ByteArray) : RuntimeException() {
    override val message = "NetCode: ${Hex.toHexString(netCode)} is not supported"
}

class HardenedDerivationRequiredException(currentDepth: Int, sequence: Int) : RuntimeException() {
    override val message = "Hardened key derivation required on depth $currentDepth " +
        "sequence was ${Hex.toHexString(sequence.toByteArray())}"
}

class InvalidSeedSizeException(size: Int) : RuntimeException() {
    override val message = "Seed should be 64bytes but was: $size"
}
