package com.dicoding.storysubmission.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storysubmission.data.UserRepository
import com.dicoding.storysubmission.data.Result
import com.dicoding.storysubmission.data.response.Story

class DetailViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getStoryById(id: String): LiveData<Result<Story>> {
        return userRepository.getStoryById(id)
    }
}