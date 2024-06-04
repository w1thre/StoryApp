package com.codewithre.storyapp.view.createstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithre.storyapp.data.UserRepository
import com.codewithre.storyapp.data.remote.response.UploadResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException

class CreateStoryViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uploadResult = MutableLiveData<UploadResponse>()
    val uploadResult: LiveData<UploadResponse> = _uploadResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun uploadStory(file: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.uploadStory(file, description)
                _uploadResult.value = response
            } catch (e: Exception) {
                val errorMessage = getErrorMessage(e)
                val errorResponse = UploadResponse(error = true, message = errorMessage)
                _uploadResult.value = errorResponse
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getErrorMessage(e: Exception): String? {
        return if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val jsonObject = errorBody?.let { JSONObject(it) }
                val message = jsonObject?.getString("message")
                message ?: e.message
            } catch (exception: Exception) {
                e.message()
            }
        } else {
            e.message
        }
    }
}