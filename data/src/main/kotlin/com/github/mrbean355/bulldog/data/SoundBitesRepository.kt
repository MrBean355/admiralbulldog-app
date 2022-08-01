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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

interface SoundBitesRepository {

    suspend fun getAllSoundBites(): List<SoundBite>

}

fun SoundBitesRepository(): SoundBitesRepository = SoundBitesRepositoryImpl()

private const val SoundsDir = "sounds"

internal class SoundBitesRepositoryImpl : SoundBitesRepository {

    override suspend fun getAllSoundBites() = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (cache.isNotEmpty()) {
                return@withLock cache
            }

            File(SoundsDir).listFiles().orEmpty()
                .map { SoundBite(it.nameWithoutExtension, it.name) }
                .also(cache::addAll)
        }
    }

    companion object {
        private val mutex = Mutex()
        private val cache = mutableListOf<SoundBite>()
    }
}