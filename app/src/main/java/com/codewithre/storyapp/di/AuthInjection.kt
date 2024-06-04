package com.codewithre.storyapp.di

import android.content.Context
import com.codewithre.storyapp.data.pref.UserPreference
import com.codewithre.storyapp.data.pref.dataStore
import com.codewithre.storyapp.data.AuthRepository
import com.codewithre.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object AuthInjection {
    fun provideRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return AuthRepository.getInstance(apiService, pref)
    }
}