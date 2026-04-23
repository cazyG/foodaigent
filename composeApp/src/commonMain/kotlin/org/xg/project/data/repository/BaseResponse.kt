package org.xg.project.data.repository

import kotlinx.serialization.Serializable

/**
 * 与后端约定的通用响应结构。
 *
 * 说明：
 * - success: 是否成功
 * - data: 业务数据（成功时）
 * - message: 错误信息/提示信息（失败时或提示用）
 */
@Serializable
data class BaseResponse<T>(
    val success: Boolean,
    val data: T,
    val message: String = ""
)

