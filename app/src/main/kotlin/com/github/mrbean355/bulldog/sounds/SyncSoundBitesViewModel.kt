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

import com.github.mrbean355.bulldog.data.SoundBiteSyncState
import com.github.mrbean355.bulldog.data.SoundBitesRepository
import com.github.mrbean355.bulldog.localization.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor
import kotlin.math.roundToInt

class SyncSoundBitesViewModel(
    private val viewModelScope: CoroutineScope,
    private val soundBitesRepository: SoundBitesRepository = SoundBitesRepository()
) {
    private val _progress = MutableStateFlow(Float.NaN)
    private val _counter = MutableStateFlow("")
    private val _percentage = MutableStateFlow("")
    private val _updatedSounds = MutableStateFlow(emptyList<String>())

    val progress: StateFlow<Float> = _progress.asStateFlow()
    val counter: StateFlow<String> = _counter.asStateFlow()
    val percentage: StateFlow<String> = _percentage.asStateFlow()
    val updatedSounds: StateFlow<List<String>> = _updatedSounds.asStateFlow()

    fun init() {
        viewModelScope.launch {
            soundBitesRepository.synchroniseSoundBites().collect {
                when (it) {
                    SoundBiteSyncState.Start -> {
                        _progress.value = Float.NaN
                        _counter.value = ""
                        _percentage.value = ""
                        _updatedSounds.value = emptyList()
                    }

                    is SoundBiteSyncState.Progress -> {
                        val progress = it.complete / it.total.toFloat()
                        _progress.value = progress
                        _counter.value = "${it.complete} / ${it.total}"
                        _percentage.value = "${progress.formatPercentage()} %"
                    }

                    is SoundBiteSyncState.Complete -> display(it)
                    SoundBiteSyncState.Error -> {
                        // TODO
                    }
                }
            }
        }
    }

    private suspend fun display(result: SoundBiteSyncState.Complete) = withContext(Dispatchers.Default) {
        _updatedSounds.value = buildList {
            if (result.added.isNotEmpty()) {
                add(getString("sync.result.added", result.added.size))
                addAll(result.added.map { it.name }.sorted())
            }
            if (result.changed.isNotEmpty()) {
                add(getString("sync.result.changed", result.changed.size))
                addAll(result.changed.map { it.name }.sorted())
            }
            if (result.deleted.isNotEmpty()) {
                add(getString("sync.result.deleted", result.deleted.size))
                addAll(result.deleted.sorted())
            }
            if (result.failed.isNotEmpty()) {
                add(getString("sync.result.failed", result.failed.size))
                addAll(result.failed.sorted())
            }
            if (isEmpty()) {
                add(getString("sync.result.latest"))
            }
        }
    }

    private fun Float.formatPercentage(): Int {
        return floor(this * 100).roundToInt()
    }
}