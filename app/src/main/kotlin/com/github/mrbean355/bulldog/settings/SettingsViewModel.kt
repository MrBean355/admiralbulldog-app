/*
 * Copyright 2024 Michael Johnston
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

package com.github.mrbean355.bulldog.settings

import com.github.mrbean355.bulldog.data.AppConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val viewModelScope: CoroutineScope
) {
    private val _dotaPath = MutableStateFlow("")

    val dotaPath: StateFlow<String> = _dotaPath.asStateFlow()

    fun init() {
        viewModelScope.launch {
            _dotaPath.value = AppConfig.getDotaPath()
        }
    }

    fun onDotaPathChange(value: String) {
        viewModelScope.launch {
            AppConfig.setDotaPath(value)
            _dotaPath.value = AppConfig.getDotaPath()
        }
    }
}