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

package com.github.mrbean355.bulldog.gsi

import com.github.mrbean355.bulldog.audio.SoundBitePlayer
import com.github.mrbean355.bulldog.data.AppConfig
import com.github.mrbean355.bulldog.data.SoundBitesRepository
import com.github.mrbean355.bulldog.gsi.triggers.SoundTrigger
import com.github.mrbean355.bulldog.gsi.triggers.SoundTriggers
import com.github.mrbean355.bulldog.gsi.triggers.configKey
import com.github.mrbean355.dota2.server.GameStateServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

object GameStateMonitor {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val soundBitesRepository = SoundBitesRepository()
    private val latestState = MutableStateFlow<GameState?>(null)
    private var previousState: GameState? = null

    fun getLatestState(): StateFlow<GameState?> = latestState.asStateFlow()

    fun start() {
        GameStateServer(12345 /* TODO: ConfigPersistence.getPort() */)
            .setPlayingListener { state ->
                val map = state.map
                val events = state.events
                if (map != null && events != null) {
                    processGameState(GameState(map, state.player, state.hero, state.items, events))
                }
            }
            .setSpectatingListener { state ->
                val map = state.map
                val events = state.events
                if (map != null && events != null) {
                    processGameState(GameState(map, null, null, null, events))
                }
            }
            .startAsync()
    }

    private fun processGameState(currentState: GameState) = synchronized(this) {
        val previousMatchId = previousState?.map?.matchId
        val currentMatchId = currentState.map.matchId

        // Recreate sound bites when a new match is entered:
        if (currentMatchId != previousMatchId) {
            previousState = null
            SoundTriggers.forEach(SoundTrigger::reset)
            Roshan.reset()
        }

        // Play sound bites that want to be played:
        val localPreviousState = previousState
        if (localPreviousState != null && (!localPreviousState.map.isPaused || !currentState.map.isPaused)) {
            coroutineScope.launch {
                SoundTriggers.forEach {
                    it.process(localPreviousState, currentState)
                }
            }
        }

        previousState = currentState
        latestState.value = currentState
    }

    private suspend fun SoundTrigger.process(previousState: GameState, currentState: GameState) {
        if (AppConfig.isTriggerEnabled(configKey)
            && Random.nextFloat() <= AppConfig.getTriggerChance(configKey) / 100f
            && shouldPlay(previousState, currentState)
        ) {
            soundBitesRepository.getSelectedSoundBites(configKey)
                .randomOrNull()
                ?.run(SoundBitePlayer::play)
        }
    }
}
