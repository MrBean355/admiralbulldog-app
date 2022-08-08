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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.github.mrbean355.bulldog.components.AppWindow
import com.github.mrbean355.bulldog.gsi.triggers.SoundTriggerType
import com.github.mrbean355.bulldog.gsi.triggers.description
import com.github.mrbean355.bulldog.gsi.triggers.label
import com.github.mrbean355.bulldog.localization.getString

@Composable
fun ConfigureSoundTriggerScreen(
    triggerType: SoundTriggerType,
    onCloseRequest: () -> Unit
) = AppWindow(
    title = getString("triggers.configure.title"),
    size = DpSize(400.dp, 600.dp),
    onCloseRequest = onCloseRequest
) {
    val viewModel = remember { ConfigureSoundTriggerViewModel(triggerType) }

    var showChooseSoundsScreen by remember { mutableStateOf(false) }

    Scaffold {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = triggerType.label,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = triggerType.description,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Divider(modifier = Modifier.padding(top = 8.dp))

            // Field: enabled
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = getString("triggers.configure.enabled"))
                Spacer(modifier = Modifier.weight(1f))
                Switch(checked = viewModel.isEnabled.value, onCheckedChange = viewModel::onCheckChanged)
            }

            // Field: sound bites
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = getString("triggers.configure.sounds"))
                    Text(
                        text = getString("triggers.configure.sounds.selected", viewModel.selectedSounds.value),
                        style = MaterialTheme.typography.body2
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(onClick = { showChooseSoundsScreen = true }) {
                    Text(getString("triggers.configure.sounds.change"))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    if (showChooseSoundsScreen) {
        ChooseSoundBitesScreen(triggerType, onCloseRequest = {
            viewModel.onChooseSoundBitesWindowClose()
            showChooseSoundsScreen = false
        })
    }
}