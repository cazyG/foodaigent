package org.xg.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import org.xg.project.data.model.BaseResponse
import org.xg.project.domain.Result

suspend inline fun <reified T> HttpResponse.decodeBaseResponse(): Result<T> {
    return try {
        when {
            status == HttpStatusCode.NotFound ->
                Result.Error("Resource not found", canRetry = false)
            status.value !in 200..299 -> {
                val raw = runCatching { body<String>() }.getOrNull()
                Result.Error(raw?.takeIf { it.isNotBlank() } ?: "请求失败 (${status.value})")
            }
            else -> {
                val wrapped = body<BaseResponse<T>>()
                if (wrapped.success) {
                    Result.Success(wrapped.data)
                } else {
                    Result.Error(wrapped.message.ifBlank { "请求失败" })
                }
            }
        }
    } catch (e: Exception) {
        Result.Error(toUserFriendlyNetworkMessage(e))
    }
}

fun toUserFriendlyNetworkMessage(error: Throwable): String = when {
    error.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
        "网络不可用，请检查连接"
    error.message?.contains("timeout", ignoreCase = true) == true ->
        "请求超时，请稍后重试"
    else -> error.message?.takeIf { it.isNotBlank() } ?: "网络请求失败"
}
