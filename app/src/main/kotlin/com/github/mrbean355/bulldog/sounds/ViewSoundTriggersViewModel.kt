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

import com.github.mrbean355.bulldog.gsi.triggers.SoundTriggerType
import com.github.mrbean355.bulldog.gsi.triggers.SoundTriggerTypes
import com.github.mrbean355.bulldog.gsi.triggers.description
import com.github.mrbean355.bulldog.gsi.triggers.label
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewSoundTriggersViewModel {
    private val _items = MutableStateFlow<List<Item>>(emptyList())

    val items: StateFlow<List<Item>> = _items.asStateFlow()

    fun init() {
        _items.value = SoundTriggerTypes.map {
            Item(it, it.label, it.description)
        }
    }

    data class Item(
        val type: SoundTriggerType,
        val label: String,
        val subLabel: String
    )
}
