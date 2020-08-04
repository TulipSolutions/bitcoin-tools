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

package nl.tulipsolutions.mnemonic

import nl.tulipsolutions.byteutils.Hex
import nl.tulipsolutions.mnemonic.wordlist.Languages
import nl.tulipsolutions.mnemonic.wordlist.normalize
import nl.tulipsolutions.testvectors.MnemonicTestVectorEntry
import nl.tulipsolutions.testvectors.mnemonicTestVectors
import org.assertj.core.api.Assertions
import org.junit.Test

class Bip39Test {

    // words to bytes tests
    @Test
    fun testEnglishWordToBytes() {
        mnemonicTestVectors["English"]!!.forEach { entry: MnemonicTestVectorEntry ->
            Assertions.assertThat(
                Hex.toHexString(entry.mnemonic.split(" ").toTypedArray().toEntropyCheckSumPair(Languages.English).first)
                    .toLowerCase()
            ).isEqualTo(entry.entropy)
        }
    }

    @Test
    fun testJapaneseWordToBytes() {
        mnemonicTestVectors["Japanese"]!!.forEach { entry: MnemonicTestVectorEntry ->
            Assertions.assertThat(
                Hex.toHexString(
                    entry.mnemonic.split("　").toTypedArray().toEntropyCheckSumPair(Languages.Japanese).first
                ).toLowerCase()
            ).isEqualTo(entry.entropy)
        }
    }

    // bytes to words tests
    @Test
    fun testBytesToEnglishWords() {
        mnemonicTestVectors["English"]!!.forEach { entry: MnemonicTestVectorEntry ->
            Assertions.assertThat(
                Hex.decode(entry.entropy).toMnemonicWords(Languages.English).joinToString(" ")
            ).isEqualTo(entry.mnemonic)
        }
    }

    @Test
    fun testBytesToJapaneseWords() {
        mnemonicTestVectors["Japanese"]!!.forEach { entry: MnemonicTestVectorEntry ->
            Assertions.assertThat(
                Hex.decode(entry.entropy).toMnemonicWords(Languages.Japanese).joinToString(" ")
                // TODO: verify whether we should test against normalized separator
            ).isEqualTo(entry.mnemonic.normalize())
        }
    }

    // wordlist to seed tests with PBKDF2WithHmacSHA512"
    @Test
    fun testEnglishEncryptedWordlistToSeed() {
        mnemonicTestVectors["English"]!!.forEach { entry: MnemonicTestVectorEntry ->
            Assertions.assertThat(
                Hex.toHexString(entry.mnemonic.split(" ").toSeed(entry.passphrase)).toLowerCase()
            ).isEqualTo(entry.seed)
        }
    }

    @Test
    fun testJapaneseEncryptedWordlistToSeed() {

        mnemonicTestVectors["Japanese"]!!.forEach { entry: MnemonicTestVectorEntry ->
            Assertions.assertThat(
                Hex.toHexString(entry.mnemonic.split("　").toSeed(entry.passphrase, "　")).toLowerCase()
            ).isEqualTo(entry.seed)
        }
    }
}
