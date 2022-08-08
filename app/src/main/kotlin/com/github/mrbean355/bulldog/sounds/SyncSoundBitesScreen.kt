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

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mrbean355.bulldog.components.AppWindow
import com.github.mrbean355.bulldog.localization.getString

@Composable
fun SyncSoundBitesScreen(
    onCloseRequest: () -> Unit
) = AppWindow(
    title = getString("sync.title"),
    onCloseRequest = onCloseRequest
) {
    val scope = rememberCoroutineScope()
    val viewModel = remember { SyncSoundBitesViewModel(scope) }
    val progress by viewModel.progress.collectAsState()
    val counter by viewModel.counter.collectAsState()
    val percentage by viewModel.percentage.collectAsState()
    val items by viewModel.updatedSounds.collectAsState()

    Scaffold {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = getString("sync.subtitle"))
            if (progress.isNaN()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(text = counter)
                Text(text = percentage, modifier = Modifier.align(Alignment.TopEnd))
            }
            Box {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(items) {
                        Text(it)
                    }
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(listState),
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }
}
