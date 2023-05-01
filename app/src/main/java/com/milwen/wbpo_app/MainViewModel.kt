package com.milwen.wbpo_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milwen.wbpo_app.api.ApiCallError
import com.milwen.wbpo_app.api.ApiCallResponse
import com.milwen.wbpo_app.application.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

open class MainViewModel: ViewModel() {

    fun <T> apiCall(
        apiCall: suspend () -> Response<T>,
        onError: (ApiCallResponse.Error)->Unit,
        onSuccess: suspend (ApiCallResponse.Success<T>)->Unit): Job {

        return viewModelScope.launch(Dispatchers.Main){
            try {
                val response: Response<T> =
                withContext(Dispatchers.IO){
                    apiCall()
                }

                if (response.isSuccessful) {
                    App.log("MainViewModel: response: successful: responseBody: ${response.body()}")
                    App.log("MainViewModel: response: successful: response: ${response.message()}")
                    onSuccess(ApiCallResponse.Success(data = response.body()!!))
                } else {
                    App.log("MainViewModel: response: error: response: ${response.raw()}")
                    onError(ApiCallResponse.Error(errorBody = response.errorBody()))
                }
            } catch (e: HttpException) {
                onError(ApiCallResponse.Error(errorMessage = e.message ?: "Something went wrong"))
            } catch (e: IOException) {
                onError(ApiCallResponse.Error(errorMessage = e.message ?: "Something went wrong"))
            } catch (e: Exception) {
                onError(ApiCallResponse.Error(errorMessage = e.message ?: "Something went wrong"))
            }
        }
    }
}