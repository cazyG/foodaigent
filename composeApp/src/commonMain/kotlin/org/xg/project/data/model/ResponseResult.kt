package org.xg.project.data.model

sealed class ResponseResult<out T> {
    data class Success<T>(val data: T) : ResponseResult<T>()
    data class Error(val message: String, val code: Int? = null) : ResponseResult<Nothing>()
}