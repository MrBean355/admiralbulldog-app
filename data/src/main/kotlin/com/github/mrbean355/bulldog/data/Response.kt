package com.github.mrbean355.bulldog.data

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo

sealed interface Response<T> {
    val statusCode: Int

    data class Success<T>(
        override val statusCode: Int,
        val data: T
    ) : Response<T>

    data class Error<T>(
        override val statusCode: Int,
        val cause: Throwable? = null
    ) : Response<T>
}

suspend inline fun <reified T> HttpResponse.toResponse(): Response<T> = toResponse(typeInfo<T>())

fun <T> Throwable.toResponse(): Response<T> = Response.Error(999, cause = this)

@PublishedApi
internal suspend fun <T> HttpResponse.toResponse(typeInfo: TypeInfo): Response<T> {
    return if (status.isSuccess()) {
        Response.Success(status.value, body(typeInfo))
    } else {
        Response.Error(status.value)
    }
}