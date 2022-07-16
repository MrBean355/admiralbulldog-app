package com.github.mrbean355.bulldog.data

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

internal object BulldogServiceImpl : BulldogService {
    private val client by lazy {
        HttpClient(CIO) {
            install(Logging)
            install(ContentNegotiation) {
                json()
            }
        }
    }

    override suspend fun listSoundBites(): Response<Map<String, String>> {
        return try {
            client.get("$BaseUrl/soundBites/listV2").toResponse()
        } catch (t: Throwable) {
            t.toResponse()
        }
    }

    override suspend fun downloadSoundBite(name: String, destination: File): Response<Unit> {
        return try {
            require(!destination.exists() || destination.isFile) {
                "Destination must not exist, or be a file: ${destination.absolutePath}"
            }
            destination.delete()
            streamResponse("$BaseUrl/soundBites/$name", destination)
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
                Response.Success(response.status.value, Unit)
            } else {
                Response.Error(response.status.value)
            }
        }
    }
}