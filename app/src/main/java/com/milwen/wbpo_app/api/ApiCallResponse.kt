package com.milwen.wbpo_app.api

open class ApiCallResponse<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : ApiCallResponse<T>(data = data)
    class Error<T>(errorMessage: String) : ApiCallResponse<T>(message = errorMessage)
}