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

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.github.mrbean355.bulldog.data.AppConfig
import com.github.mrbean355.bulldog.gsi.triggers.SoundTriggerType
import com.github.mrbean355.bulldog.gsi.triggers.configKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ConfigureSoundTriggerViewModel(
    private val viewModelScope: CoroutineScope,
    private val triggerType: SoundTriggerType
) {
    private val _isEnabled = mutableStateOf(false)
    private val _selectedSounds = mutableStateOf(0)

    val isEnabled: State<Boolean> = _isEnabled
    val selectedSounds: State<Int> = _selectedSounds

    fun init() {
        viewModelScope.launch {
            _isEnabled.value = AppConfig.isTriggerEnabled(triggerType.configKey)
        }
        refreshSelectionCount()
    }

    fun onCheckChanged(value: Boolean) {
        _isEnabled.value = value
        viewModelScope.launch {
            AppConfig.setTriggerEnabled(triggerType.configKey, value)
        }
    }

    fun onChooseSoundBitesWindowClose() {
        refreshSelectionCount()
    }

    private fun refreshSelectionCount() {
        viewModelScope.launch {
            _selectedSounds.value = AppConfig.getTriggerSounds(triggerType.configKey).size
        }
    }
}