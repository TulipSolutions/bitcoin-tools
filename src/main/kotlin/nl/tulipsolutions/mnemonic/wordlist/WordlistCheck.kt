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

package nl.tulipsolutions.mnemonic.wordlist

import java.security.MessageDigest
import nl.tulipsolutions.byteutils.Hex

fun Array<String>.checkWordList(fileHash: String, filename: String): Array<String> {
    val hash =
        Hex.toHexString(
            MessageDigest.getInstance("SHA-256")
                .digest((this.joinToString("\n") + "\n").toByteArray())
        ).toLowerCase()
    return if (fileHash == hash) this else throw WordListCheckSumFailedException(fileHash, hash, filename)
}
