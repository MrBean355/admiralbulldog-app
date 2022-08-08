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

class ConfigureSoundTriggerViewModel(
    private val triggerType: SoundTriggerType
) {
    private val _isEnabled = mutableStateOf(AppConfig.isTriggerEnabled(triggerType.configKey))
    private val _selectedSounds = mutableStateOf(0)

    val isEnabled: State<Boolean> = _isEnabled
    val selectedSounds: State<Int> = _selectedSounds

    fun init() {
        refreshSelectionCount()
    }

    fun onCheckChanged(value: Boolean) {
        AppConfig.setTriggerEnabled(triggerType.configKey, value)
        _isEnabled.value = value
    }

    fun onChooseSoundBitesWindowClose() {
        refreshSelectionCount()
    }

    private fun refreshSelectionCount() {
        _selectedSounds.value = AppConfig.getTriggerSounds(triggerType.configKey).size
    }
}