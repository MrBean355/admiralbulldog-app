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

package com.github.mrbean355.bulldog.sounds

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.github.mrbean355.bulldog.components.AppWindow
import com.github.mrbean355.bulldog.components.rememberViewModel
import com.github.mrbean355.bulldog.gsi.triggers.SoundTrigger
import com.github.mrbean355.bulldog.localization.getString

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun ChooseSoundBitesScreen(
    soundTrigger: SoundTrigger,
    onCloseRequest: () -> Unit
) = AppWindow(
    title = getString("triggers.sounds.title"),
    size = DpSize(400.dp, 800.dp),
    onCloseRequest = onCloseRequest
) {
    val viewModel = rememberViewModel { ChooseSoundBitesViewModel(it, soundTrigger) }
    val sounds by viewModel.sounds.collectAsState(emptyList())
    val query by viewModel.query.collectAsState()

    Scaffold(
        topBar = {
            SearchField(
                query = query,
                onQueryChange = viewModel::onSearchQueryChange,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) {
        Box {
            val listState = rememberLazyListState()

            LazyColumn(state = listState) {
                items(sounds) { sound ->
                    Row {
                        Checkbox(
                            checked = viewModel.getSoundSelectionState(sound).value,
                            onCheckedChange = { viewModel.onCheckChange(sound, it) },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        ListItem {
                            Text(sound)
                        }
                    }
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState = listState),
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        label = { Text(text = getString("triggers.sounds.search")) },
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        trailingIcon = {
            IconButton(onClick = { onQueryChange("") }) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.Close),
                    contentDescription = getString("triggers.sounds.clear.description")
                )
            }
        }
    )
}