package org.xg.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable

class UploadService(private val httpClient: HttpClient) {
    
    private val baseUrl = "https://admin.api.tantuwuyou.com"
    
    @Serializable
    data class PresignedUrlResponse(val data: PresignedUrlData)
    
    @Serializable
    data class PresignedUrlData(
        val putUrl: String,
        val getUrl: String,
        val headers: Map<String, String>
    )
    
    @Serializable
    data class UploadResult(val success: Boolean, val url: String)
    
    suspend fun getPresignedUrl(filename: String): PresignedUrlResponse {
        try {
            val response = httpClient.get("$baseUrl/api/generatePresignedUrl?filename=$filename")
            val responseBody = response.body<String>()
            
            try {
                return response.body<PresignedUrlResponse>()
            } catch (e: Exception) {
                throw Exception("服务器返回错误: $responseBody")
            }
        } catch (e: Exception) {
            throw e
        }
    }
    
    suspend fun uploadFile(putUrl: String, getUrl: String, headers: Map<String, String>, file: ByteArray): UploadResult {
        try {
            val response: HttpResponse = httpClient.put(putUrl) {
                this.headers {
                    headers.forEach { (key, value) -> append(key, value) }
                }
                setBody(file)
            }
            
            return if (response.status.value == 200) {
                UploadResult(success = true, url = getUrl)
            } else {
                UploadResult(success = false, url = "")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}