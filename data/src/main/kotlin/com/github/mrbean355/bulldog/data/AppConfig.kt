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

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

private const val ConfigVersion = 1

object AppConfig {
    private val lock = ReentrantReadWriteLock()
    private val json = Json {
        encodeDefaults = true
        prettyPrint = true
    }
    private lateinit var file: File
    private lateinit var data: Data

    fun initAsync() {
        GlobalScope.launch {
            lock.write {
                init()
            }
        }
    }

    fun getStoragePath(): String = AppStorage.getRootPath()

    fun getLastSyncTime(): Long = lock.read {
        data.lastSync
    }

    fun setLastSyncTimeToNow(): Unit = lock.write {
        data.lastSync = System.currentTimeMillis()
        persist()
    }

    fun isTriggerEnabled(trigger: String): Boolean = lock.read {
        data.triggers[trigger]?.enabled ?: false
    }

    fun setTriggerEnabled(trigger: String, enabled: Boolean): Unit = lock.write {
        getTriggerConfig(trigger).enabled = enabled
        persist()
    }

    private fun getTriggerConfig(trigger: String): TriggerConfig {
        return data.triggers.getOrPut(trigger, ::TriggerConfig)
    }

    private fun persist() {
        file.writeText(json.encodeToString(data))
    }

    private fun init() {
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
    )
}