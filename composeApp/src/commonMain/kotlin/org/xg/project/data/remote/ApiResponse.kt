package org.xg.project.data.remote

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import org.xg.project.data.model.BaseResponse
import org.xg.project.domain.Result

suspend inline fun <reified T> HttpResponse.decodeBaseResponse(): Result<T> {
    if (!status.isSuccess()) {
        return Result.Error(
            message = "请求失败 (${status.value})",
            canRetry = status.value >= 500,
        )
    }
    return try {
        val payload = body<BaseResponse<T>>()
        if (payload.success) {
            Result.Success(payload.data)
        } else {
            Result.Error(payload.message.ifBlank { "请求失败" })
        }
    } catch (e: Exception) {
        Result.Error(toUserFriendlyNetworkMessage(e))
    }
}

fun toUserFriendlyNetworkMessage(throwable: Throwable): String {
    val raw = throwable.message.orEmpty()
    return when {
        raw.contains("404") -> "服务暂未提供餐单数据"
        raw.contains("NoTransformationFoundException") -> "服务器响应格式异常，请稍后重试"
        raw.contains("Connection", ignoreCase = true) -> "网络连接失败，请检查网络"
        raw.contains("timeout", ignoreCase = true) -> "请求超时，请稍后重试"
        raw.length > 120 -> "加载失败，请稍后重试"
        raw.isBlank() -> "加载失败，请稍后重试"
        else -> raw
    }
}
