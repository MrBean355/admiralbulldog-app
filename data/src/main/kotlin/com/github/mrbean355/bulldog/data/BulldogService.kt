package com.github.mrbean355.bulldog.data

import java.io.File

sealed interface BulldogService {

    suspend fun listSoundBites(): Response<Map<String, String>>

    suspend fun downloadSoundBite(name: String, destination: File): Response<Unit>

}

fun BulldogService(): BulldogService = BulldogServiceImpl
