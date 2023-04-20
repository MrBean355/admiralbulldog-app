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

package com.github.mrbean355.bulldog

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import com.github.mrbean355.bulldog.components.AppWindow
import com.github.mrbean355.bulldog.components.rememberViewModel
import com.github.mrbean355.bulldog.data.AppConfig
import com.github.mrbean355.bulldog.home.HomeScreen
import com.github.mrbean355.bulldog.localization.getString
import com.github.mrbean355.bulldog.settings.SettingsScreen
import com.github.mrbean355.bulldog.sounds.SyncSoundBitesScreen
import com.github.mrbean355.bulldog.sounds.ViewSoundTriggersScreen

fun main() = application {
    AppConfig.initAsync()

    AppWindow(
        title = getString("home.title"),
        escapeClosesWindow = false,
        onCloseRequest = ::exitApplication
    ) {
        var activeTab by remember { mutableStateOf(MainTab.Home) }

        Scaffold(
            topBar = {
                TabRow(
                    selectedTabIndex = activeTab.ordinal,
                    modifier = Modifier.height(48.dp)
                ) {
                    MainTab.values().forEach {
                        Tab(
                            selected = activeTab == it,
                            onClick = { activeTab = it },
                            content = { Text(getString(it.label)) }
                        )
                    }
                }
            }
        ) {
            activeTab.content()
        }
    }

    val viewModel = rememberViewModel { MainViewModel(it) }
    val showSyncScreen by viewModel.showSyncScreen.collectAsState()
    if (showSyncScreen) {
        SyncSoundBitesScreen(onCloseRequest = viewModel::onSyncScreenClose)
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }
}

private enum class MainTab(
    val label: String,
    val content: @Composable () -> Unit
) {
    Home(label = "home.tab.home", content = { HomeScreen() }),
    Sounds(label = "home.tab.sounds", content = { ViewSoundTriggersScreen() }),
    DiscordBot(label = "home.tab.discord", content = { Placeholder() }),
    Settings(label = "home.tab.settings", content = { SettingsScreen() }),
}

@Composable
private fun Placeholder() {
    Text(
        text = "This is not the tab you're looking for.",
        modifier = Modifier.padding(16.dp)
    )
}