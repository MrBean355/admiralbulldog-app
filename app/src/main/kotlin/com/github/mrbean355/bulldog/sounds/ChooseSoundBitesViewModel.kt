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

package com.github.mrbean355.bulldog.sounds

import com.github.mrbean355.bulldog.data.SoundBite
import com.github.mrbean355.bulldog.data.SoundBitesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ChooseSoundBitesViewModel(
    private val viewModelScope: CoroutineScope
) {
    private val soundBitesRepository = SoundBitesRepository()

    private val _sounds = MutableStateFlow<List<String>>(emptyList())
    private val _query = MutableStateFlow("")

    val sounds: Flow<List<String>> = _sounds.combine(_query) { items, query ->
        val trimmed = query.trim()
        items.filter { it.contains(trimmed, ignoreCase = true) }
    }
    val query: StateFlow<String> = _query.asStateFlow()

    fun init() {
        viewModelScope.launch(Dispatchers.Default) {
            _sounds.value = soundBitesRepository.getAllSoundBites().map(SoundBite::name).sorted()
        }
    }

    fun onSearchQueryChange(value: String) {
        _query.value = value
    }
}