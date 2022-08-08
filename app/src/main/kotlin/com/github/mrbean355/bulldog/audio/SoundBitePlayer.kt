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

package com.github.mrbean355.bulldog.audio

import com.github.mrbean355.bulldog.data.SoundBite
import uk.co.caprica.vlcj.player.base.EventApi
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object SoundBitePlayer {
    private val components = mutableListOf<AudioPlayerComponent>()
    private val lock = ReentrantLock()

    fun play(soundBite: SoundBite) {
        val component = AudioPlayerComponent()
        lock.withLock {
            components += component
        }
        with(component.mediaPlayer()) {
            audio().setVolume(50) // TODO: add setting for volume.
            events().onPlayerFinished {
                it.submit {
                    component.release()
                    lock.withLock {
                        components -= component
                    }
                }
            }
            media().play(soundBite.filePath)
        }
    }

    private fun EventApi.onPlayerFinished(block: (MediaPlayer) -> Unit) {
        addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun finished(mediaPlayer: MediaPlayer) {
                block(mediaPlayer)
            }
        })
    }
}