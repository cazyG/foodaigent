package org.xg.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import org.xg.project.core.ui.AppStrings
import org.xg.project.domain.Result

class UploadService(private val httpClient: HttpClient) {

    private val baseUrl = ApiConfig.UPLOAD_HOST
    
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
    
    suspend fun getPresignedUrl(filename: String): Result<PresignedUrlResponse> {
        return try {
            val response = httpClient.get("$baseUrl/api/generatePresignedUrl?filename=$filename")
            Result.Success(response.body<PresignedUrlResponse>())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
    
    suspend fun uploadFile(putUrl: String, getUrl: String, headers: Map<String, String>, file: ByteArray): Result<UploadResult> {
        return try {
            val response: HttpResponse = httpClient.put(putUrl) {
                this.headers {
                    headers.forEach { (key, value) -> append(key, value) }
                }
                setBody(file)
            }

            if (response.status == HttpStatusCode.OK) {
                Result.Success(UploadResult(success = true, url = getUrl))
            } else {
                Result.Error("Upload failed with status: ${response.status.value}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun uploadImage(fileName: String, imageBytes: ByteArray): Result<UploadResult> {
        return try {
            when (val presignedResult = getPresignedUrl(fileName)) {
                is Result.Success -> {
                    uploadFile(
                        presignedResult.data.data.putUrl,
                        presignedResult.data.data.getUrl,
                        presignedResult.data.data.headers,
                        imageBytes
                    )
                }
                is Result.Error -> presignedResult
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}