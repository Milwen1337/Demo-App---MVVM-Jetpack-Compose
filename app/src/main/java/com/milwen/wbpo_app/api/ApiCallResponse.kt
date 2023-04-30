package com.milwen.wbpo_app.api

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import java.io.IOException

data class ApiCallError(
    @SerializedName("error") val error: String?
)

open class ApiCallResponse<T>(
    val data: T? = null
) {
    class Success<T>(data: T) : ApiCallResponse<T>(data = data)
    class Error(){
        var apiCallError: ApiCallError? = null
        constructor(errorMessage: String): this(){
            apiCallError = ApiCallError(errorMessage)
        }

        constructor(errorBody: ResponseBody?) : this() {
            try {
                errorBody?.let {
                    val json = it.string()
                    apiCallError = Gson().fromJson(json, object : TypeToken<ApiCallError>() {}.type)
                }
            } catch (e: IOException) {
                apiCallError = ApiCallError(e.message ?: "Error parsing error response")
            } catch (e: Exception) {
                apiCallError = ApiCallError(e.message ?: "Something went wrong")
            }
        }
    }
}