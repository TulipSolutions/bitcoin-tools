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

package nl.tulipsolutions.testvectors

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.Test

@Serializable
private data class EnglishVectorsJson(
    val english: List<List<String>>
)

private val json = Json(JsonConfiguration.Stable)
private val englishVectorsJson = Files.readString(
    // Be aware this file is bazel managed / downloaded from github. See WORKSPACE
    Paths.get("external/english_mnemonic_json/file/downloaded"), StandardCharsets.UTF_8
)
private val englishVectors = json.parse(EnglishVectorsJson.serializer(), englishVectorsJson)

// Structured as follows: [ {"entropy" : "", "mnemonic": "", "seed": "", "xpriv": ""}, {...} ]
@Serializable
private data class JapaneseVectorJson(
    val entropy: String,
    val mnemonic: String,
    val passphrase: String,
    val seed: String,
    val bip32_xprv: String
)

private val japaneseVectorJson = Files.readString(
    // Be aware this file is bazel managed / downloaded from github. See WORKSPACE
    Paths.get("external/japanese_mnemonic_json/file/downloaded"), StandardCharsets.UTF_8
)
private val japaneseVectors = json.parse(ListSerializer(JapaneseVectorJson.serializer()), japaneseVectorJson)

// Our common object to make testing easier
@Serializable
data class MnemonicTestVectorEntry(
    val entropy: String,
    val mnemonic: String,
    val passphrase: String,
    val seed: String,
    val bip32_xprv: String
)

val mnemonicTestVectors = mapOf<String, List<MnemonicTestVectorEntry>>(
    "English" to englishVectors.english.map { entry: List<String> ->
        MnemonicTestVectorEntry(
            entropy = entry[0],
            mnemonic = entry[1],
            passphrase = "TREZOR",
            seed = entry[2],
            bip32_xprv = entry[3]

        )
    }.toList(),
    "Japanese" to japaneseVectors.map { entry: JapaneseVectorJson ->
        MnemonicTestVectorEntry(
            entropy = entry.entropy,
            mnemonic = entry.mnemonic,
            passphrase = entry.passphrase,
            seed = entry.seed,
            bip32_xprv = entry.bip32_xprv
        )
    }.toList()
)

// TODO: work around till proper bazel test library option is found
// Currently marked as kt_jvm_test instead of library
class MnemonicVectorsTest {

    @Test
    fun testMapping() {
        mnemonicTestVectors
    }
}
