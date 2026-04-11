package org.xg.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

class UploadService(private val httpClient: HttpClient) {
    
    private val baseUrl = "https://admin.api.tantuwuyou.com"
    
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
        println("UploadService: Getting presigned URL for filename: $filename")
        println("UploadService: Request URL: $baseUrl/admin/upload")
        try {
            // 使用GET方法并在URL中传递filename参数
            val response = httpClient.get("$baseUrl/api/generatePresignedUrl?filename=$filename")
            println("UploadService: Get presigned URL response status: ${response.status}")
            
            // 尝试读取原始响应内容
            val responseBody = response.body<String>()
            println("UploadService: Get presigned URL response body: $responseBody")
            
            // 尝试解析响应
            try {
                val presignedUrlResponse = response.body<PresignedUrlResponse>()
                println("UploadService: Get presigned URL success: ${presignedUrlResponse.data}")
                return presignedUrlResponse
            } catch (e: Exception) {
                // 如果解析失败，说明服务器返回了错误响应
                println("UploadService: Failed to parse response as PresignedUrlResponse: ${e.message}")
                throw Exception("服务器返回错误: $responseBody")
            }
        } catch (e: Exception) {
            println("UploadService: Get presigned URL error: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    suspend fun uploadFile(putUrl: String, getUrl: String, headers: Map<String, String>, file: ByteArray): UploadResult {
        println("UploadService: Uploading file to: $putUrl")
        println("UploadService: File size: ${file.size} bytes")
        println("UploadService: Headers: $headers")
        try {
            val response: HttpResponse = httpClient.put(putUrl) {
                this.headers {
                    headers.forEach { (key, value) ->
                        append(key, value)
                    }
                }
                setBody(file)
            }
            println("UploadService: Upload response status: ${response.status}")
            
            return if (response.status.value == 200) {
                println("UploadService: Upload success")
                UploadResult(success = true, url = getUrl)
            } else {
                println("UploadService: Upload failed with status: ${response.status}")
                UploadResult(success = false, url = "")
            }
        } catch (e: Exception) {
            println("UploadService: Upload error: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}