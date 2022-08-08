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

package com.github.mrbean355.bulldog.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

private const val ConfigVersion = 1

object AppConfig {
    private val mutex = Mutex()
    private val json = Json {
        encodeDefaults = true
        prettyPrint = true
    }
    private lateinit var file: File
    private lateinit var data: Data

    fun initAsync() {
        GlobalScope.launch(Dispatchers.IO) {
            init()
        }
    }

    fun getStoragePath(): String = AppStorage.getRootPath()

    suspend fun getLastSyncTime(): Long = mutex.withLock {
        data.lastSync
    }

    suspend fun setLastSyncTimeToNow(): Unit = mutex.withLock {
        data.lastSync = System.currentTimeMillis()
        persist()
    }

    suspend fun isTriggerEnabled(trigger: String): Boolean = mutex.withLock {
        data.triggers[trigger]?.enabled ?: false
    }

    suspend fun setTriggerEnabled(trigger: String, enabled: Boolean): Unit = mutex.withLock {
        getTriggerConfig(trigger).enabled = enabled
        persist()
    }

    suspend fun getTriggerSounds(trigger: String): Collection<String> = mutex.withLock {
        data.triggers[trigger]?.sounds?.toList().orEmpty()
    }

    suspend fun setTriggerSoundSelected(trigger: String, sound: String, enabled: Boolean) = mutex.withLock {
        val config = getTriggerConfig(trigger)
        if (enabled) config.sounds += sound else config.sounds -= sound
        persist()
    }

    private fun getTriggerConfig(trigger: String): TriggerConfig {
        return data.triggers.getOrPut(trigger, ::TriggerConfig)
    }

    private suspend fun persist() = withContext(Dispatchers.IO) {
        file.writeText(json.encodeToString(data))
    }

    private suspend fun init() = mutex.withLock {
        file = AppStorage.getFile("config.json")
        data = if (file.exists()) {
            json.decodeFromString(file.readText())
        } else {
            Data()
        }
        if (data.version > ConfigVersion) {
            error("Config file was modified by a newer app: ${data.version}")
        }
        if (data.installationId.isEmpty()) {
            data.installationId = UUID.randomUUID().toString()
        }
        persist()
    }

    @Serializable
    private data class Data(
        val version: Int = ConfigVersion,
        var lastSync: Long = 0L,
        var installationId: String = "",
        val triggers: MutableMap<String, TriggerConfig> = mutableMapOf(),
    )

    @Serializable
    private data class TriggerConfig(
        var enabled: Boolean = false,
        var sounds: Set<String> = emptySet(),
    )
}