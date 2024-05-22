package com.dicoding.storysubmission.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storysubmission.data.UserRepository
import com.dicoding.storysubmission.data.pref.UserModel
import com.dicoding.storysubmission.data.response.ListStoryItem
import kotlinx.coroutines.launch
import com.dicoding.storysubmission.data.Result

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _storyList = MediatorLiveData<Result<List<ListStoryItem>>>()
    val storyList: LiveData<Result<List<ListStoryItem>>> = _storyList

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStories() {
        val liveData = repository.getStories()
        _storyList.addSource(liveData) { result ->
            _storyList.value = result
        }
    }

}