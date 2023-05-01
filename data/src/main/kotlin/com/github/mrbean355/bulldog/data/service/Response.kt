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

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo

sealed interface Response<T> {
    val statusCode: Int

    data class Success<T>(
        override val statusCode: Int = HttpStatusCode.OK.value,
        val data: T
    ) : Response<T>

    data class Error<T>(
        override val statusCode: Int,
        val cause: Throwable? = null
    ) : Response<T>

    companion object

}

@Suppress("FunctionName")
fun Response.Companion.Success(statusCode: Int = HttpStatusCode.OK.value): Response.Success<Unit> {
    return Response.Success(statusCode, Unit)
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