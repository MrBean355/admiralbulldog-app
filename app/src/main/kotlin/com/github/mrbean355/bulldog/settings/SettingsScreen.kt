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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.bulldog.components.rememberViewModel
import com.github.mrbean355.bulldog.data.AppConfig
import com.github.mrbean355.bulldog.localization.getString

@Composable
fun SettingsScreen() {
    val viewModel = rememberViewModel { SettingsViewModel(it) }
    val dotaPath by viewModel.dotaPath.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = getString("settings.title"),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = getString("settings.subtitle"),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = dotaPath,
            onValueChange = viewModel::onDotaPathChange,
            label = { Text(text = getString("settings.dota_path.label")) },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = getString("settings.storage.label"),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = AppConfig.getStoragePath(),
            style = MaterialTheme.typography.body2
        )
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }
}
