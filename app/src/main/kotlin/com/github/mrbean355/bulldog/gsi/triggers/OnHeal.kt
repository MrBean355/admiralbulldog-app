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
import kotlin.random.Random

/** Must have healed at least this much percentage. */
private const val MIN_HP_PERCENTAGE = 4

/** Health required for max chance to play the sound. */
private const val MAX_HEAL = 400

/**
 * Play a sound when the hero is healed.
 *
 * The chance increases as the heal amount increases.
 * A heal amount of [MAX_HEAL] or more gives a 100% chance to play the sound.
 * The heal amount must be at least [MIN_HP_PERCENTAGE] percent of the hero's max HP.
 */
object OnHeal : SoundTrigger {

    override fun shouldPlay(previous: PlayingGameState, current: PlayingGameState): Boolean {
        val previousHero = previous.hero ?: return false
        val currentHero = current.hero ?: return false

        if (previousHero.health <= 0) {
            // We get healed on respawn; ignore.
            return false
        }
        if (currentHero.maxHealth != previousHero.maxHealth) {
            // Ignore heals caused by increasing max HP.
            return false
        }
        if (currentHero.healthPercent - previousHero.healthPercent < MIN_HP_PERCENTAGE) {
            // Small heal; ignore.
            return false
        }
        return true
    }

    fun doesSmartChanceProc(previous: PlayingGameState, current: PlayingGameState): Boolean {
        val previousHero = previous.hero ?: return false
        val currentHero = current.hero ?: return false

        return Random.nextFloat() <= (currentHero.health - previousHero.health) / MAX_HEAL
    }
}