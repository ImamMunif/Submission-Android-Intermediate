package com.dicoding.storysubmission.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storysubmission.data.UserRepository
import com.dicoding.storysubmission.data.Result
import com.dicoding.storysubmission.data.pref.UserModel
import com.dicoding.storysubmission.data.response.Story

class DetailViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun getStoryById(token: String, id: String): LiveData<Result<Story>> {
        return userRepository.getStoryById(token, id)
    }
}