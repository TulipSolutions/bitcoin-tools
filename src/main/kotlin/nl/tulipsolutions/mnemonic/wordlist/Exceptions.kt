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

class WordListCheckSumFailedException(expected: String, was: String, filename: String) : RuntimeException() {
    override val message =
        "${filename.capitalize()} word list  hash was invalid expected hash : $expected but was: $was"
}

class IncorrectWordListSize(size: Int) : RuntimeException() {
    override val message = "Incorrect word list size expected 2048 was $size"
}
