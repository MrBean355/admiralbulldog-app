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

package com.github.mrbean355.bulldog.data.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import java.io.File

private const val BaseUrl = "http://prod.upmccxmkjx.us-east-2.elasticbeanstalk.com:8090"
private const val ListSoundsPath = "$BaseUrl/soundBites/v3/list"
private const val DownloadSoundPath = "$BaseUrl/soundBites/%s"

internal object BulldogService {
    private val client by lazy {
        HttpClient(CIO) {
            install(Logging)
            install(ContentNegotiation) {
                json()
            }
        }
    }

    suspend fun listSoundBites(): Response<List<PlaySoundDto>> {
        return try {
            client.get(ListSoundsPath).toResponse()
        } catch (t: Throwable) {
            t.toResponse()
        }
    }

    suspend fun downloadSoundBite(name: String, destination: File): Response<Unit> {
        return try {
            require(!destination.exists() || destination.isFile) {
                "Destination must not exist, or be a file: ${destination.absolutePath}"
            }
            destination.delete()
            streamResponse(DownloadSoundPath.format(name), destination)
        } catch (t: Throwable) {
            t.toResponse()
        }
    }

    private suspend fun streamResponse(url: String, destination: File): Response<Unit> {
        return client.prepareGet(url).execute { response ->
            if (response.status.isSuccess()) {
                val channel = response.bodyAsChannel()
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                    while (!packet.isEmpty) {
                        destination.appendBytes(packet.readBytes())
                    }
                }
                Response.Success(response.status.value)
            } else {
                Response.Error(response.status.value)
            }
        }
    }
}