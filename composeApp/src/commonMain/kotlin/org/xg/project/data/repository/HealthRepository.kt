package org.xg.project.data.repository

import io.ktor.client.request.get
import org.xg.project.data.remote.ApiConfig
import org.xg.project.data.remote.decodeBaseResponse
import org.xg.project.data.remote.httpClient
import org.xg.project.data.remote.toUserFriendlyNetworkMessage
import org.xg.project.domain.Result

class HealthRepository {

    suspend fun checkHealth(): Result<Map<String, String>> {
        return try {
            val response = httpClient.get("${ApiConfig.HOST}/health")
            response.decodeBaseResponse()
        } catch (e: Exception) {
            Result.Error(toUserFriendlyNetworkMessage(e))
        }
    }
}
