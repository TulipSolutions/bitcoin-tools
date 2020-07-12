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

class KeyPath {
    constructor(path: String) {
        val pathParts = path.split("/")
        if (pathParts[0] != "m") {
            throw RuntimeException("expecting path format m/0h/1 or m/2'/2 ")
        }
    }
}
