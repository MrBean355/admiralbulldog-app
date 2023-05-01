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

package com.github.mrbean355.bulldog.gsi.triggers

import com.github.mrbean355.bulldog.gsi.GameState
import com.github.mrbean355.dota2.item.Item
import com.github.mrbean355.dota2.item.Items

private const val HandOfMidasName = "item_hand_of_midas"

object OnMidasReady : SoundTrigger {

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        previous.items ?: return false
        current.items ?: return false

        return previous.items.isMidasOnCooldown() && current.items.isMidasOffCooldown()
    }

    private fun Items.isMidasOnCooldown(): Boolean {
        return inventory.any { it.isMidasOnCooldown() }
    }

    private fun Items.isMidasOffCooldown(): Boolean {
        return inventory.any { it.isMidasOffCooldown() }
    }

    private fun Item.isMidasOffCooldown(): Boolean {
        return name == HandOfMidasName && cooldown == 0
    }

    private fun Item.isMidasOnCooldown(): Boolean {
        val cd = cooldown
        return name == HandOfMidasName && cd != null && cd > 0
    }
}