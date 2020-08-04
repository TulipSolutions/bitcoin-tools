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

import java.text.Normalizer

enum class Languages { Czech, English, French, Italian, Japanese, Korean, SimplifiedChinese, Spanish, TraditionalChinese }

val languageListMap = mapOf<Languages, Array<String>>(
    Languages.Czech to czechWordList,
    Languages.English to englishWordList,
    Languages.French to frenchWordList,
    Languages.Italian to italianWordList,
    Languages.Japanese to japaneseWordList,
    Languages.Korean to koreanWordList,
    Languages.SimplifiedChinese to simplifiedChineseWordList,
    Languages.Spanish to spanishWordList,
    Languages.TraditionalChinese to traditionalChineseWordList
)

val checkedLanguageListMap = mapOf<Languages, Array<String>>(
    Languages.Czech to checkedCzechWordList,
    Languages.English to checkedEnglishWordList,
    Languages.French to checkedFrenchWordList,
    Languages.Italian to checkedItalianWordList,
    Languages.Japanese to checkedJapaneseWordList,
    Languages.Korean to checkedKoreanWordList,
    Languages.SimplifiedChinese to checkedSimplifiedChineseWordList,
    Languages.Spanish to checkedSpanishWordList,
    Languages.TraditionalChinese to checkedTraditionalChineseWordList
)

val languageWordListMap = Languages.values().map {
    Pair(it, languageListMap[it]?.toWordListHashMap())
}.toMap()

fun Array<String>.toWordListHashMap(): Map<String, Int> {
    if (this.size != 2048) {
        throw IncorrectWordListSize(this.size)
    }
    return this.mapIndexed { index: Int, s: String ->
        s.normalize() to index
        s to index
    }.toList().toMap()
}

fun List<Int>.mapValuesToMnemonicWords(language: Languages): List<String> = this.map {
    // The wordlist can contain native characters,
    // but they must be encoded in UTF-8 using Normalization Form Compatibility Decomposition (NFKD).
    languageListMap[language]?.get(it)!!.normalize()
}

fun String.normalize(): String = Normalizer.normalize(this, Normalizer.Form.NFKD)
