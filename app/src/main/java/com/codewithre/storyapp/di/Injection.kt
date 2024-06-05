package com.codewithre.storyapp.di

import android.content.Context
import com.codewithre.storyapp.data.UserRepository
import com.codewithre.storyapp.data.database.StoryDatabase
import com.codewithre.storyapp.data.pref.UserPreference
import com.codewithre.storyapp.data.pref.dataStore
import com.codewithre.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return UserRepository.getInstance(storyDatabase, apiService ,pref)
    }
}