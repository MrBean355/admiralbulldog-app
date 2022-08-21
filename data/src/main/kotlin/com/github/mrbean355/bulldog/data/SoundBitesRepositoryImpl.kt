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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

private const val SoundsDir = "sounds"
private const val ConcurrentDownloads = 50

internal class SoundBitesRepositoryImpl : SoundBitesRepository {

    override suspend fun getAllSoundBites() = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (cache.isNotEmpty()) {
                return@withLock cache
            }

            AppStorage.getDirectory(SoundsDir).listFiles().orEmpty()
                .map { SoundBite(it.nameWithoutExtension, it.absolutePath) }
                .also(cache::addAll)
        }
    }

    override suspend fun getSelectedSoundBites(soundTrigger: String) = withContext(Dispatchers.IO) {
        val selected = AppConfig.getTriggerSounds(soundTrigger)
        getAllSoundBites().filter { it.name in selected }
    }

    override fun synchroniseSoundBites() = channelFlow {
        val soundsResponse = BulldogService.listSoundBites()
        if (soundsResponse !is Response.Success) {
            send(SoundBiteSyncState.Error)
            return@channelFlow
        }

        val downloadQueue = ConcurrentLinkedQueue(soundsResponse.data.keys)
        val parentDir = AppStorage.getDirectory(SoundsDir)

        val totalFiles = soundsResponse.data.size
        val complete = AtomicInteger(0)
        val added = CopyOnWriteArrayList<SoundBite>()
        val changed = CopyOnWriteArrayList<SoundBite>()
        val deleted = mutableListOf<String>()
        val failed = CopyOnWriteArrayList<String>()

        // Delete files which don't exist remotely.
        parentDir.listFiles().orEmpty()
            .filterNot { soundsResponse.data.keys.contains(it.name) }
            .onEach { deleted += it.name }
            .forEach { it.deleteRecursively() }

        send(SoundBiteSyncState.Progress(0, totalFiles))

        // Download files which are missing locally, or have different checksums.
        coroutineScope {
            repeat(ConcurrentDownloads) {
                launch {
                    while (true) {
                        val sound = downloadQueue.poll() ?: break
                        val target = File(parentDir, sound)
                        val exists = target.exists()
                        if (!exists || !verifyChecksum(target, soundsResponse.data.getValue(sound))) {
                            if (BulldogService.downloadSoundBite(sound, target) is Response.Success) {
                                (if (exists) changed else added) += SoundBite(sound.substringBeforeLast('.'), target.absolutePath)
                            } else {
                                failed += sound
                            }
                        }
                        send(SoundBiteSyncState.Progress(complete.incrementAndGet(), totalFiles))
                    }
                }
            }
        }

        send(SoundBiteSyncState.Complete(added, changed, deleted, failed))

    }.flowOn(Dispatchers.IO)

    companion object {
        private val mutex = Mutex()
        private val cache = mutableListOf<SoundBite>()

        /** @return whether the SHA-512 hash of the [file] equals (case-insensitive) the given [checksum]. */
        private fun verifyChecksum(file: File, checksum: String): Boolean {
            return try {
                val messageDigest = MessageDigest.getInstance("SHA-512")
                val result = messageDigest.digest(file.readBytes())
                BigInteger(1, result)
                    .toString(16)
                    .padStart(32, padChar = '0')
                    .equals(checksum, ignoreCase = true)
            } catch (e: NoSuchAlgorithmException) {
                true
            }
        }
    }
}