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

package com.github.mrbean355.bulldog.sounds

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.github.mrbean355.bulldog.components.rememberViewModel
import com.github.mrbean355.bulldog.gsi.triggers.SoundTrigger

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun ViewSoundTriggersScreen() {
    val viewModel = rememberViewModel(::ViewSoundTriggersViewModel)
    val items by viewModel.items.collectAsState()
    val listState = rememberLazyListState()

    var clickedItem by remember { mutableStateOf<SoundTrigger?>(null) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            items(items) { item ->
                ListItem(
                    text = { Text(text = item.label) },
                    modifier = Modifier
                        .clickable { clickedItem = item.trigger }
                        .alpha(if (item.enabled) 1f else 0.5f)
                )
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState = listState),
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
        )
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    clickedItem?.let {
        ConfigureSoundTriggerScreen(it, onCloseRequest = {
            viewModel.onConfigureScreenClose()
            clickedItem = null
        })
    }
}