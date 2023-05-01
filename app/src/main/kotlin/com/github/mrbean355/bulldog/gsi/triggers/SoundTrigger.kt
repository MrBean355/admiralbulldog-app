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
import com.github.mrbean355.bulldog.localization.getString

val SoundTriggers: Set<SoundTrigger> = setOf(
    OnBountyRunesSpawn,
    OnWisdomRunesSpawn,
    OnKill,
    OnDeath,
    OnRespawn,
    OnHeal,
    OnSmoked,
    OnMidasReady,
    RoshanTimer,
    OnPause,
    OnMatchStart,
    OnVictory,
    OnDefeat,
    Periodically,
)

const val Uninitialized: Int = -1

sealed interface SoundTrigger {

    /** Examine the states and decide if a sound should be played. */
    fun shouldPlay(previous: GameState, current: GameState): Boolean

    /** Reset any state being managed. */
    fun reset(): Unit = Unit

}

val SoundTrigger.configKey: String
    get() = requireNotNull(this::class.simpleName)

val SoundTrigger.label: String
    get() = when (this) {
        OnBountyRunesSpawn -> getString("trigger_name_bounty_runes")
        OnDeath -> getString("trigger_name_death")
        OnDefeat -> getString("trigger_name_defeat")
        OnHeal -> getString("trigger_name_heal")
        OnKill -> getString("trigger_name_kill")
        OnMatchStart -> getString("trigger_name_match_start")
        OnMidasReady -> getString("trigger_name_midas_ready")
        OnPause -> getString("trigger_name_paused")
        OnRespawn -> getString("trigger_name_respawn")
        OnSmoked -> getString("trigger_name_smoked")
        OnVictory -> getString("trigger_name_victory")
        OnWisdomRunesSpawn -> getString("trigger_name_wisdom_runes")
        Periodically -> getString("trigger_name_periodically")
        RoshanTimer -> getString("trigger_name_roshan")
        is RunesSpawnTrigger -> error("Implementation not catered for")
    }

val SoundTrigger.description: String
    get() = when (this) {
        OnBountyRunesSpawn -> getString("trigger_desc_bounty_runes")
        OnDeath -> getString("trigger_desc_death")
        OnDefeat -> getString("trigger_desc_defeat")
        OnHeal -> getString("trigger_desc_heal")
        OnKill -> getString("trigger_desc_kill")
        OnMatchStart -> getString("trigger_desc_match_start")
        OnMidasReady -> getString("trigger_desc_midas_ready")
        OnPause -> getString("trigger_desc_paused")
        OnRespawn -> getString("trigger_desc_respawn")
        OnSmoked -> getString("trigger_desc_smoked")
        OnVictory -> getString("trigger_desc_victory")
        OnWisdomRunesSpawn -> getString("trigger_desc_wisdom_runes")
        Periodically -> getString("trigger_desc_periodically")
        RoshanTimer -> getString("trigger_desc_roshan")
        is RunesSpawnTrigger -> error("Implementation not catered for")
    }