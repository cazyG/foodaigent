package org.xg.project.data.repository

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.xg.project.data.remote.ApiConfig
import org.xg.project.data.remote.decodeBaseResponse
import org.xg.project.data.remote.httpClient
import org.xg.project.data.remote.toUserFriendlyNetworkMessage
import org.xg.project.domain.Result
import org.xg.project.domain.model.CreateProductRequest
import org.xg.project.domain.model.Product

class ProductRepository {

    private val baseUrl = ApiConfig.API_BASE_URL

    suspend fun createProduct(userId: Int, request: CreateProductRequest): Result<Product> {
        return try {
            val response = httpClient.post("$baseUrl/products/user/$userId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.decodeBaseResponse()
        } catch (e: Exception) {
            Result.Error(toUserFriendlyNetworkMessage(e))
        }
    }

    suspend fun getProductsByUserId(userId: Int): Result<List<Product>> {
        return try {
            val response = httpClient.get("$baseUrl/products/user/$userId")
            response.decodeBaseResponse()
        } catch (e: Exception) {
            Result.Error(toUserFriendlyNetworkMessage(e))
        }
    }
}
