package com.dicoding.storysubmission.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storysubmission.data.UserRepository
import com.dicoding.storysubmission.data.pref.UserModel
import com.dicoding.storysubmission.data.response.ListStoryItem
import kotlinx.coroutines.launch
import com.dicoding.storysubmission.data.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class MainViewModel(private val repository: UserRepository) : ViewModel() {


    val storyList: LiveData<PagingData<ListStoryItem>> = repository.getStories(getToken()).cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    private fun getToken(): String {
        var token: String
        runBlocking {
            token = repository.getSession().firstOrNull()?.token ?: ""
        }
        return token
    }

}