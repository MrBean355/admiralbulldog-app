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
import kotlin.random.Random

object Periodically : SoundTrigger {
    private var nextPlayClockTime = Uninitialized

    override fun shouldPlay(previous: GameState, current: GameState): Boolean {
        if (nextPlayClockTime == Uninitialized) {
            nextPlayClockTime = current.map.clockTime + randomiseDelay()
        } else if (current.map.clockTime >= nextPlayClockTime) {
            nextPlayClockTime += randomiseDelay()
            return true
        }
        return false
    }

    override fun reset() {
        nextPlayClockTime = Uninitialized
    }

    private fun randomiseDelay(): Int {
        // Cast to double so that we can randomise a non-whole amount of minutes.
        val minQuietTime = 1.0 // TODO: ConfigPersistence.getMinPeriod().toDouble()
        val maxQuietTime = 5.0 // TODO: ConfigPersistence.getMaxPeriod().toDouble()
        return if (minQuietTime != maxQuietTime) {
            Random.nextDouble(minQuietTime, maxQuietTime)
        } else {
            minQuietTime
        }.toInt() * 60
    }
}