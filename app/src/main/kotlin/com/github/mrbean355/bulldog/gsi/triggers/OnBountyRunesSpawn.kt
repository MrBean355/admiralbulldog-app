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

package com.github.mrbean355.bulldog.gsi.triggers

import com.github.mrbean355.dota2.gamestate.PlayingGameState
import com.github.mrbean355.dota2.map.MatchState
import kotlin.math.ceil

/** How often (in seconds) the runes spawn. */
private const val HOW_OFTEN = 3 * 60

/** Plays a sound shortly before the bounty runes spawn. */
class OnBountyRunesSpawn : SoundTrigger {
    /** Play a sound this many seconds before the bounty runes spawn. */
    private val warningPeriod = 15 // TODO: ConfigPersistence.getBountyRuneTimer()
    private var nextPlayTime = Uninitialized

    override fun shouldPlay(previous: PlayingGameState, current: PlayingGameState): Boolean {
        val currentMap = current.map ?: return false
        if (currentMap.matchState != MatchState.PreGame && currentMap.matchState != MatchState.GameInProgress) {
            // Don't play during the picking phase.
            return false
        }
        val currentTime = currentMap.clockTime
        if (nextPlayTime == Uninitialized) {
            nextPlayTime = findNextPlayTime(currentTime)
        }
        if (currentTime >= nextPlayTime) {
            val diff = currentTime - nextPlayTime
            nextPlayTime = findNextPlayTime(currentTime + 10)
            if (diff <= warningPeriod) {
                return true
            }
        }
        return false
    }

    private fun findNextPlayTime(clockTime: Int): Int {
        val iteration = ceil((clockTime + warningPeriod) / HOW_OFTEN.toFloat()).toInt()
        val nextPlayTime = iteration * HOW_OFTEN - warningPeriod
        return nextPlayTime.coerceAtLeast(-warningPeriod)
    }
}