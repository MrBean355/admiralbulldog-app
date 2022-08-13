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

import com.github.mrbean355.bulldog.data.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object Dota2 {

    suspend fun isValidPathSaved(): Boolean = withContext(Dispatchers.IO) {
        val path = AppConfig.getDotaPath()
        if (path.isNotBlank()) {
            File(path, GameInfoFile).let {
                it.exists() && it.isFile
            }
        } else {
            false
        }
    }

    suspend fun installGameStateIntegration(): Unit = withContext(Dispatchers.IO) {
        if (!isValidPathSaved()) {
            return@withContext
        }
        val directory = File(AppConfig.getDotaPath(), GsiDirectory)
            .also(File::mkdirs)

        val file = File(directory, GsiFile)
        val contents = if (file.exists()) file.readText() else null
        val newContents = GsiFileContent.format(12345).trim() // TODO: Make port configurable.
        if (newContents != contents) {
            file.writeText(newContents)
        }
    }
}

private const val GameInfoFile = "game/dota/gameinfo.gi"
private const val GsiDirectory = "game/dota/cfg/gamestate_integration"
private const val GsiFile = "gamestate_integration_bulldog.cfg"
private const val GsiFileContent = """
"AdmiralBulldog Sounds"
{
    "uri"           "http://localhost:%d"
    "timeout"       "5.0"
    "buffer"        "0.5"
    "throttle"      "0.5"
    "heartbeat"     "30.0"
    "data"
    {
        "provider"      "0"
        "map"           "1"
        "player"        "1"
        "hero"          "1"
        "abilities"     "0"
        "items"         "1"
        "buildings"     "0"
        "draft"         "0"
        "wearables"     "0"
    }
}
"""