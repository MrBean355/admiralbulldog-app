package com.github.mrbean355.bulldog.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.bulldog.data.AppConfig
import com.github.mrbean355.bulldog.localization.getString

@Composable
fun SettingsScreen() {
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
}
