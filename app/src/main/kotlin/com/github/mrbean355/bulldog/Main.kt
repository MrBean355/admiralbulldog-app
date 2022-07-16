package com.github.mrbean355.bulldog

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import com.github.mrbean355.bulldog.components.AppWindow
import com.github.mrbean355.bulldog.localization.getString

fun main() = application {
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
}

private enum class MainTab(
    val label: String,
    val content: @Composable () -> Unit
) {
    Home(label = "home.tab.home", content = { Placeholder() }),
    Sounds(label = "home.tab.sounds", content = { Placeholder() }),
    DiscordBot(label = "home.tab.discord", content = { Placeholder() }),
    Settings(label = "home.tab.settings", content = { Placeholder() }),
}

@Composable
private fun Placeholder() {
    Text(
        text = "This is not the tab you're looking for.",
        modifier = Modifier.padding(16.dp)
    )
}