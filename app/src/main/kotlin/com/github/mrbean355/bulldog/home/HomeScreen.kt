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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mrbean355.bulldog.components.rememberViewModel
import com.github.mrbean355.bulldog.localization.getString

@Composable
fun HomeScreen() {
    val viewModel = rememberViewModel { HomeViewModel(it) }
    val showInstall by viewModel.showInstall.collectAsState()
    val header by viewModel.header.collectAsState()
    val showProgressIndicator by viewModel.showProgressIndicator.collectAsState()
    val subHeader by viewModel.subHeader.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        if (showInstall) {
            InstallScreen()
        } else {
            Text(header, fontSize = 24.sp)
            if (showProgressIndicator) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Text(subHeader)
        }
        Text("Version 2.0.0", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.weight(1f))
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }
}

@Composable
private fun InstallScreen() {
    Text(getString("home.header.not_installed"), fontSize = 24.sp)
    Text(getString("home.subheader.not_installed"))
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(onClick = { /* TODO: Navigate to docs */ }) {
            Text(getString("action.more_info"))
        }
        Button(onClick = { /* TODO: Prompt user to choose Dota path */ }) {
            Text(getString("home.action.install"))
        }
    }
}