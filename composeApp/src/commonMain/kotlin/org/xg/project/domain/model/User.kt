package org.xg.project.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val username: String,
    val email: String,
    val password: String = "",
)

/** 注册/创建用户请求体，对应文档 `RegisterRequest`。 */
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
)

@Serializable
data class UserResponse(
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val password: String = "",
)

fun UserResponse.toUser(): User = User(
    id = id.takeIf { it > 0 },
    username = username,
    email = email,
    password = password,
)

/** @deprecated 使用 [RegisterRequest] */
typealias CreateUserRequest = RegisterRequest
