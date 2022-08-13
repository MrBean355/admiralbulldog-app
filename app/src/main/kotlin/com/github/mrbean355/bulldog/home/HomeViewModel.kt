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

package com.github.mrbean355.bulldog.home

import com.github.mrbean355.bulldog.gsi.Dota2
import com.github.mrbean355.bulldog.gsi.GameStateMonitor
import com.github.mrbean355.bulldog.localization.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val viewModelScope: CoroutineScope
) {
    private val _showInstall = MutableStateFlow(false)

    val showInstall: StateFlow<Boolean> = _showInstall.asStateFlow()
    val header = MutableStateFlow(getString("home.header.waiting"))
    val showProgressIndicator = MutableStateFlow(true)
    val subHeader = MutableStateFlow(getString("home.subheader.waiting"))

    fun init() {
        viewModelScope.launch {
            val installed = Dota2.isValidPathSaved()
            _showInstall.value = !installed
            if (installed) {
                Dota2.installGameStateIntegration()
                GameStateMonitor.getLatestState().filterNotNull().first()
                header.value = getString("home.header.connected")
                showProgressIndicator.value = false
                subHeader.value = getString("home.subheader.connected")
            }
        }
    }
}
