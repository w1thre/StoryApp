package com.codewithre.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithre.storyapp.data.UserRepository
import com.codewithre.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = userRepository.register(name, email, password)
                _registerResult.value = response
            } catch (e: Exception) {
                val errorMessage = getErrorMessage(e)
                val errorResponse = RegisterResponse(error = true, message = errorMessage)
                _registerResult.value = errorResponse
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getErrorMessage(e: Exception): String {
        return if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                 val jsonObject = errorBody?.let { JSONObject(it) }
                 val message = jsonObject?.getString("message")
                message ?: e.message()
            } catch (exception: Exception) {
                e.message()
            }
        } else ({
            e.message
        }).toString()
    }

}