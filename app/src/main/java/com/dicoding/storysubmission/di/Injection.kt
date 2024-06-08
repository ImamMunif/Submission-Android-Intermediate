package com.dicoding.storysubmission.di

import android.content.Context
import com.dicoding.storysubmission.data.api.ApiConfig
import com.dicoding.storysubmission.data.UserRepository
import com.dicoding.storysubmission.data.pref.UserPreference
import com.dicoding.storysubmission.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService, pref)
    }
}