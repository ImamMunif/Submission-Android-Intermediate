package com.dicoding.storysubmission.data.api

import com.dicoding.storysubmission.data.response.LoginResponse
import com.dicoding.storysubmission.data.response.SignupResponse
import com.dicoding.storysubmission.data.response.StoryDetailResponse
import com.dicoding.storysubmission.data.response.StoryResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun signup (
        @Field("name") name: String,
        @Field( "email") email: String,
        @Field( "password") password: String,
    ): SignupResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): StoryResponse

    @GET("stories/{id}")
    suspend fun getStoryById(@Path("id") id: String): StoryDetailResponse
}