package com.dicoding.storysubmission.data.api

import com.dicoding.storysubmission.data.response.LoginResponse
import com.dicoding.storysubmission.data.response.SignupResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
}