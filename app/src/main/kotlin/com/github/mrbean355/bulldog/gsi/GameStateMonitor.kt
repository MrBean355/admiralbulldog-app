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

package com.github.mrbean355.bulldog.gsi

import com.github.mrbean355.bulldog.audio.SoundBitePlayer
import com.github.mrbean355.bulldog.data.AppConfig
import com.github.mrbean355.bulldog.data.SoundBitesRepository
import com.github.mrbean355.bulldog.gsi.triggers.SoundTrigger
import com.github.mrbean355.bulldog.gsi.triggers.SoundTriggerTypes
import com.github.mrbean355.bulldog.gsi.triggers.configKey
import com.github.mrbean355.dota2.gamestate.PlayingGameState
import com.github.mrbean355.dota2.server.GameStateServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.reflect.full.createInstance

object GameStateMonitor {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val soundBitesRepository = SoundBitesRepository()
    private val latestState = MutableStateFlow<PlayingGameState?>(null)
    private val soundTriggers = mutableListOf<SoundTrigger>()
    private var previousState: PlayingGameState? = null

    fun getLatestState(): StateFlow<PlayingGameState?> = latestState.asStateFlow()

    fun start() {
        coroutineScope.launch {
            GameStateServer(12345 /* TODO: ConfigPersistence.getPort() */)
                .setPlayingListener {
                    processGameState(it)
                    latestState.value = it
                }
                .startAsync()
        }
    }

    private fun processGameState(currentState: PlayingGameState) = synchronized(soundTriggers) {
        val previousMatchId = previousState?.map?.matchId
        val currentMatchId = currentState.map?.matchId

        // Recreate sound bites when a new match is entered:
        if (currentMatchId != previousMatchId) {
            previousState = null
            soundTriggers.clear()
            soundTriggers.addAll(SoundTriggerTypes.map { it.createInstance() })
        }

        // Play sound bites that want to be played:
        val localPreviousState = previousState
        if (localPreviousState != null && currentState.map?.isPaused == false) {
            coroutineScope.launch {
                soundTriggers.forEach {
                    it.process(localPreviousState, currentState)
                }
            }
        }
        previousState = currentState
    }

    private suspend fun SoundTrigger.process(previousState: PlayingGameState, currentState: PlayingGameState) {
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
