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

import com.github.mrbean355.dota2.gamestate.PlayingGameState

object OnKill : SoundTrigger {

    override fun shouldPlay(previous: PlayingGameState, current: PlayingGameState): Boolean {
        val previousPlayer = previous.player ?: return false
        val currentPlayer = current.player ?: return false

        return currentPlayer.kills > previousPlayer.kills
    }
}