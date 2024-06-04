package com.codewithre.storyapp.data

import com.codewithre.storyapp.data.pref.UserModel
import com.codewithre.storyapp.data.pref.UserPreference
import com.codewithre.storyapp.data.remote.response.LoginResponse
import com.codewithre.storyapp.data.remote.retrofit.ApiService

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    companion object {
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ) = AuthRepository(apiService, userPreference)
    }
}