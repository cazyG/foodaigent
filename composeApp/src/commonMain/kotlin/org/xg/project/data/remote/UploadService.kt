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
    
    suspend fun getPresignedUrl(filename: String): org.xg.project.domain.Result<PresignedUrlResponse> {
        return try {
            val response = httpClient.get("$baseUrl/api/generatePresignedUrl?filename=$filename")
            try {
                org.xg.project.domain.Result.Success(response.body<PresignedUrlResponse>())
            } catch (e: Exception) {
                val responseBody = response.body<String>()
                org.xg.project.domain.Result.Error("服务器返回错误: $responseBody")
            }
        } catch (e: Exception) {
            org.xg.project.domain.Result.Error(e.message ?: "Unknown error")
        }
    }
    
    suspend fun uploadFile(putUrl: String, getUrl: String, headers: Map<String, String>, file: ByteArray): org.xg.project.domain.Result<UploadResult> {
        return try {
            val response: HttpResponse = httpClient.put(putUrl) {
                this.headers {
                    headers.forEach { (key, value) -> append(key, value) }
                }
                setBody(file)
            }
            
            if (response.status.value == 200) {
                org.xg.project.domain.Result.Success(UploadResult(success = true, url = getUrl))
            } else {
                org.xg.project.domain.Result.Error("Upload failed with status: ${response.status.value}")
            }
        } catch (e: Exception) {
            org.xg.project.domain.Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun uploadImage(fileName: String, imageBytes: ByteArray): org.xg.project.domain.Result<UploadResult> {
        return try {
            when (val presignedResult = getPresignedUrl(fileName)) {
                is org.xg.project.domain.Result.Success -> {
                    val uploadResult = uploadFile(
                        presignedResult.data.data.putUrl,
                        presignedResult.data.data.getUrl,
                        presignedResult.data.data.headers,
                        imageBytes
                    )
                    uploadResult
                }
                is org.xg.project.domain.Result.Error -> presignedResult
            }
        } catch (e: Exception) {
            e.printStackTrace()
            org.xg.project.domain.Result.Error(e.message ?: "Unknown error")
        }
    }
}