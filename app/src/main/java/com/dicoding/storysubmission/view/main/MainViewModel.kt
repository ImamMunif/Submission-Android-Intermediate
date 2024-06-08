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

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private var token: String = "token"

    val storyList: LiveData<PagingData<ListStoryItem>> = repository.getStories(token).cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    init {
        viewModelScope.launch {
            repository.getSession().collect(){ user ->
                token = user.token
                Log.d("Debug", "MainViewModel: token: $token")
            }
        }
    }

}