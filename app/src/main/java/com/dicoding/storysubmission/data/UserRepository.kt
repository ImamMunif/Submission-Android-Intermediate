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
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

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
    fun getStories(): LiveData<Result<List<ListStoryItem>>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                Log.d("story list", "Mulai getStories")
                val successResponse = apiService.getStories()
                val storyList = successResponse.listStory
                Log.d("story list", "${successResponse.message}")
                emit(Result.Success(storyList))
            } catch (e: Exception) {
                Log.d("story list", "error getStories")
                emit(Result.Error(e.message.toString()))
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