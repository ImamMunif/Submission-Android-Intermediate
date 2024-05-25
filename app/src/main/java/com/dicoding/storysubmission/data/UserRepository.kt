package com.dicoding.storysubmission.data

import android.util.Log
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

    // !!-------------------- main logic --------------------!!
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    // !!-------------------- login logic --------------------!!
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

    // !!-------------------- signup logic --------------------!!
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

    // !!-------------------- story logic --------------------!!
    fun getStories(token: String): LiveData<Result<List<ListStoryItem>>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                Log.d("log: repository", "getStories: beginning getStories...")
                val successResponse = apiService.getStories("Bearer $token")
                val storyList = successResponse.listStory
                Log.d("log: repository", "getStories:  successResponse: ${successResponse.message}")
                emit(Result.Success(storyList))
            } catch (e: Exception) {
                Log.d("log: repository", "getStories: error getStories...!!")
                emit(Result.Error(e.message.toString()))
            }
        }

    // !!-------------------- story by ID logic --------------------!!
    fun getStoryById(id: String): LiveData<Result<Story>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                Log.d("log: repository", "getStoryByID: beginning getStoryByID...")
                val successResponse: StoryDetailResponse = apiService.getStoryById(id)
                Log.d("log: repository", "getStoryByID: successResponse: $successResponse")
                emit(Result.Success(successResponse.story))
            } catch (e: Exception) {
                Log.d("log: repository", "getStoryByID: error getStoryByID...!!")
                emit(Result.Error(e.message.toString()))
            }
        }

    // !!-------------------- upload logic --------------------!!
    fun uploadImage(imageFile: File, description: String) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadImage(multipartBody, requestBody)
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