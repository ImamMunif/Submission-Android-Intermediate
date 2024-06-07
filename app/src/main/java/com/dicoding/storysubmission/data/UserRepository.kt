package com.dicoding.storysubmission.data

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.storysubmission.data.api.ApiService
import com.dicoding.storysubmission.data.response.SignupResponse
import com.dicoding.storysubmission.data.pref.UserModel
import com.dicoding.storysubmission.data.pref.UserPreference
import com.dicoding.storysubmission.data.response.ListStoryItem
import com.dicoding.storysubmission.data.response.LoginResponse
import com.dicoding.storysubmission.data.response.Story
import com.dicoding.storysubmission.data.response.StoryDetailResponse
import com.dicoding.storysubmission.data.response.StoryResponse
import com.dicoding.storysubmission.data.response.StoryUploadResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.login(email, password)
            val userModel = UserModel(
                email = email,
                token = successResponse.loginResult.token,
                isLogin = true
            )
            saveSession(userModel)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }

    fun signup(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.signup(name, email, password)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, SignupResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }

    fun getStories(token: String): LiveData<Result<List<ListStoryItem>>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val successResponse: StoryResponse = apiService.getStories("Bearer $token")
                val storyList = successResponse.listStory
                emit(Result.Success(storyList))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getStoriesWithLocation(token: String): LiveData<Result<List<ListStoryItem>>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val successResponse = apiService.getStoriesWithLocation("Bearer $token")
                val storyList = successResponse.listStory
                emit(Result.Success(storyList))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getStoryById(token: String, id: String): LiveData<Result<Story>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val successResponse: StoryDetailResponse =
                    apiService.getStoryById("Bearer $token", id)
                emit(Result.Success(successResponse.story))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    fun uploadStory(token: String, imageFile: File, description: String, location: Location?) =
        liveData {
            emit(Result.Loading)
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            try {
                val successResponse =
                    if (location != null) {
                        apiService.uploadStory(
                            "Bearer $token",
                            multipartBody,
                            requestBody,
                            location.latitude.toString().toRequestBody("text/plain".toMediaType()),
                            location.longitude.toString().toRequestBody("text/plain".toMediaType())
                        )
                    } else {
                        apiService.uploadStory("Bearer $token", multipartBody, requestBody)
                    }
                emit(Result.Success(successResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, StoryUploadResponse::class.java)
                emit(Result.Error(errorResponse.message))
            }
        }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}