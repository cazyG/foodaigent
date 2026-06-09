package org.xg.project.data.repository

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.xg.project.data.remote.ApiConfig
import org.xg.project.data.remote.decodeBaseResponse
import org.xg.project.data.remote.httpClient
import org.xg.project.data.remote.toUserFriendlyNetworkMessage
import org.xg.project.domain.Result
import org.xg.project.domain.model.RegisterRequest
import org.xg.project.domain.model.User
import org.xg.project.domain.model.UserResponse
import org.xg.project.domain.model.toUser

class UserRepository {

    private val baseUrl = ApiConfig.API_BASE_URL

    /**
     * 用户注册。优先 `POST /api/register`，404 时回退 `POST /api/users`。
     */
    suspend fun createUser(request: RegisterRequest): Result<User> {
        return when (val result = submitRegistration(request)) {
            is Result.Success -> Result.Success(result.data.toUser())
            is Result.Error -> result
        }
    }

    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = httpClient.get("$baseUrl/users")
            response.decodeBaseResponse()
        } catch (e: Exception) {
            Result.Error(toUserFriendlyNetworkMessage(e))
        }
    }

    suspend fun getUserById(id: Int): Result<User> {
        return try {
            val response = httpClient.get("$baseUrl/users/$id")
            response.decodeBaseResponse()
        } catch (e: Exception) {
            Result.Error(toUserFriendlyNetworkMessage(e))
        }
    }

    /**
     * 后端当前无独立登录接口，通过用户列表匹配用户名与密码完成鉴权。
     */
    suspend fun authenticate(username: String, password: String): Result<User> {
        return when (val result = getAllUsers()) {
            is Result.Success -> {
                val matched = result.data.find { user ->
                    user.username == username.trim() && user.password == password
                }
                if (matched != null) {
                    Result.Success(matched)
                } else {
                    Result.Error("用户名或密码错误", canRetry = false)
                }
            }
            is Result.Error -> result
        }
    }

    private suspend fun submitRegistration(request: RegisterRequest): Result<UserResponse> {
        return try {
            val registerResponse = httpClient.post("$baseUrl/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (registerResponse.status == HttpStatusCode.NotFound) {
                postRegistration("$baseUrl/users", request)
            } else {
                registerResponse.decodeBaseResponse()
            }
        } catch (e: Exception) {
            Result.Error(toUserFriendlyNetworkMessage(e))
        }
    }

    private suspend fun postRegistration(url: String, request: RegisterRequest): Result<UserResponse> {
        return try {
            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.decodeBaseResponse()
        } catch (e: Exception) {
            Result.Error(toUserFriendlyNetworkMessage(e))
        }
    }
}
