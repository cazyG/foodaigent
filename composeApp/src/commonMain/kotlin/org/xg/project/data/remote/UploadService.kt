package org.xg.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable

class UploadService(private val httpClient: HttpClient) {
    
    @Serializable
    data class PresignedUrlResponse(
        val data: PresignedUrlData
    )
    
    @Serializable
    data class PresignedUrlData(
        val putUrl: String,
        val getUrl: String,
        val headers: Map<String, String>
    )
    
    @Serializable
    data class UploadResult(
        val success: Boolean,
        val url: String
    )
    
    suspend fun getPresignedUrl(filename: String): PresignedUrlResponse {
        // 这里应该调用后端API获取预签名URL
        // 暂时返回模拟数据
        return PresignedUrlResponse(
            data = PresignedUrlData(
                putUrl = "https://example.com/upload",
                getUrl = "https://example.com/images/$filename",
                headers = mapOf(
                    "Content-Type" to "image/jpeg",
                    "Date" to "2026-04-10"
                )
            )
        )
    }
    
    suspend fun uploadFile(url: String, headers: Map<String, String>, file: ByteArray): UploadResult {
        try {
            val response: HttpResponse = httpClient.put(url) {
                this.headers {
                    headers.forEach { (key, value) ->
                        append(key, value)
                    }
                }
                setBody(file)
            }
            
            return if (response.status.value == 200) {
                UploadResult(success = true, url = url)
            } else {
                UploadResult(success = false, url = "")
            }
        } catch (e: Exception) {
            println("上传错误: ${e.message}")
            return UploadResult(success = false, url = "")
        }
    }
}