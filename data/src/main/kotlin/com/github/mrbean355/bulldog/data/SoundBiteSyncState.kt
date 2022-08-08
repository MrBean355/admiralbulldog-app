/*
 * Copyright 2022 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.bulldog.data

sealed interface SoundBiteSyncState {

    class Progress(val complete: Int, val total: Int) : SoundBiteSyncState

    class Complete(
        val added: List<SoundBite>,
        val changed: List<SoundBite>,
        val deleted: List<String>,
        val failed: List<String>,
    ) : SoundBiteSyncState

    object Error : SoundBiteSyncState

}