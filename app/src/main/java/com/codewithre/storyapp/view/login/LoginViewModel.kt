package com.codewithre.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithre.storyapp.data.pref.UserModel
import com.codewithre.storyapp.data.AuthRepository
import com.codewithre.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                saveSession(UserModel(email, response.loginResult.token))
                _loginResult.value = Result.success(response)
            } catch (e: HttpException) {
                _loginResult.value = Result.failure(e)
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }


}