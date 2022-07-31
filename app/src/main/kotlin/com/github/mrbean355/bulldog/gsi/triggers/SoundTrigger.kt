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

import com.github.mrbean355.bulldog.localization.getString
import com.github.mrbean355.dota2.gamestate.PlayingGameState
import kotlin.reflect.KClass

val SoundTriggerTypes: Set<SoundTriggerType> = setOf(
    OnBountyRunesSpawn::class,
    OnKill::class,
    OnDeath::class,
    OnRespawn::class,
    OnHeal::class,
    OnSmoked::class,
    OnMidasReady::class,
    OnMatchStart::class,
    OnVictory::class,
    OnDefeat::class,
    Periodically::class
)

typealias SoundTriggerType = KClass<out SoundTrigger>

const val Uninitialized: Int = -1

interface SoundTrigger {

    /** Examine the states and decide if a sound should be played. */
    fun shouldPlay(previous: PlayingGameState, current: PlayingGameState): Boolean

}

val SoundTriggerType.label: String
    get() = when (this) {
        OnBountyRunesSpawn::class -> getString("trigger_name_bounty_runes")
        OnDeath::class -> getString("trigger_name_death")
        OnDefeat::class -> getString("trigger_name_defeat")
        OnHeal::class -> getString("trigger_name_heal")
        OnKill::class -> getString("trigger_name_kill")
        OnMatchStart::class -> getString("trigger_name_match_start")
        OnMidasReady::class -> getString("trigger_name_midas_ready")
        OnRespawn::class -> getString("trigger_name_respawn")
        OnSmoked::class -> getString("trigger_name_smoked")
        OnVictory::class -> getString("trigger_name_victory")
        Periodically::class -> getString("trigger_name_periodically")
        else -> throw IllegalArgumentException("Unexpected type: $this")
    }

val SoundTriggerType.description: String
    get() = when (this) {
        OnBountyRunesSpawn::class -> getString("trigger_desc_bounty_runes")
        OnDeath::class -> getString("trigger_desc_death")
        OnDefeat::class -> getString("trigger_desc_defeat")
        OnHeal::class -> getString("trigger_desc_heal")
        OnKill::class -> getString("trigger_desc_kill")
        OnMatchStart::class -> getString("trigger_desc_match_start")
        OnMidasReady::class -> getString("trigger_desc_midas_ready")
        OnRespawn::class -> getString("trigger_desc_respawn")
        OnSmoked::class -> getString("trigger_desc_smoked")
        OnVictory::class -> getString("trigger_desc_victory")
        Periodically::class -> getString("trigger_desc_periodically")
        else -> throw IllegalArgumentException("Unexpected type: $this")
    }