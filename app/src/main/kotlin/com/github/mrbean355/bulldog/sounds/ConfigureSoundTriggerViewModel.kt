/*
 * Copyright 2023 Michael Johnston
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

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.github.mrbean355.bulldog.data.AppConfig
import com.github.mrbean355.bulldog.gsi.triggers.SoundTrigger
import com.github.mrbean355.bulldog.gsi.triggers.configKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.ceil

class ConfigureSoundTriggerViewModel(
    private val viewModelScope: CoroutineScope,
    private val soundTrigger: SoundTrigger
) {
    private val _isEnabled = mutableStateOf(false)
    private val _chance = mutableStateOf(0f)
    private val _selectedSounds = mutableStateOf(0)

    val isEnabled: State<Boolean> = _isEnabled
    val chance: State<Float> = _chance
    val selectedSounds: State<Int> = _selectedSounds

    fun init() {
        viewModelScope.launch {
            _isEnabled.value = AppConfig.isTriggerEnabled(soundTrigger.configKey)
            _chance.value = AppConfig.getTriggerChance(soundTrigger.configKey)
        }
        refreshSelectionCount()
    }

    fun onCheckChanged(value: Boolean) {
        _isEnabled.value = value
        viewModelScope.launch {
            AppConfig.setTriggerEnabled(soundTrigger.configKey, value)
        }
    }

    fun onChanceChanged(value: Float) {
        _chance.value = ceil(value)
        viewModelScope.launch {
            AppConfig.setTriggerChance(soundTrigger.configKey, chance.value)
        }
    }

    fun onChooseSoundBitesWindowClose() {
        refreshSelectionCount()
    }

    private fun refreshSelectionCount() {
        viewModelScope.launch {
            _selectedSounds.value = AppConfig.getTriggerSounds(soundTrigger.configKey).size
        }
    }
}