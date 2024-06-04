package com.codewithre.storyapp.data.remote.retrofit

import com.codewithre.storyapp.data.remote.response.DetailStoryResponse
import com.codewithre.storyapp.data.remote.response.LoginResponse
import com.codewithre.storyapp.data.remote.response.RegisterResponse
import com.codewithre.storyapp.data.remote.response.StoryResponse
import com.codewithre.storyapp.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(@Path("id") id: String
    ) : DetailStoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): UploadResponse

}