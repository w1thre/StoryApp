package com.codewithre.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.codewithre.storyapp.data.database.StoryDatabase
import com.codewithre.storyapp.data.pref.UserModel
import com.codewithre.storyapp.data.pref.UserPreference
import com.codewithre.storyapp.data.remote.response.DetailStoryResponse
import com.codewithre.storyapp.data.remote.response.ListStoryItem
import com.codewithre.storyapp.data.remote.response.LoginResponse
import com.codewithre.storyapp.data.remote.response.RegisterResponse
import com.codewithre.storyapp.data.remote.response.StoryResponse
import com.codewithre.storyapp.data.remote.response.UploadResponse
import com.codewithre.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

class UserRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
//                StoryPagingSource(apiService)
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    suspend fun getStoriesWithLocation(): StoryResponse {
        return apiService.getStoriesWithLocation()
    }

    suspend fun getDetailStory(id: String) : DetailStoryResponse {
        return apiService.getDetailStory(id)
    }

    suspend fun uploadStory(photo: MultipartBody.Part, description: RequestBody) : UploadResponse {
        return apiService.uploadStory(photo, description)
    }

    companion object {
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService,
            userPreference: UserPreference
        ) = UserRepository(storyDatabase,apiService, userPreference)
    }
}