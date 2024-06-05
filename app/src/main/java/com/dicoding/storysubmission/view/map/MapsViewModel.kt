package com.dicoding.storysubmission.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storysubmission.data.Result
import com.dicoding.storysubmission.data.UserRepository
import com.dicoding.storysubmission.data.pref.UserModel
import com.dicoding.storysubmission.data.response.ListStoryItem

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    private val _storyListWithLocation = MediatorLiveData<Result<List<ListStoryItem>>>()
    val storyListWithLocation: LiveData<Result<List<ListStoryItem>>> = _storyListWithLocation

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStoriesWithLocation(token: String) {
        val liveData = repository.getStoriesWithLocation(token)
        _storyListWithLocation.addSource(liveData) { result ->
            _storyListWithLocation.value = result
        }
    }
}