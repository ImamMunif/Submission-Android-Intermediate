package com.dicoding.storysubmission.view.upload

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storysubmission.data.UserRepository
import com.dicoding.storysubmission.data.pref.UserModel
import java.io.File

class UploadViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun uploadStory(token: String, file: File, description: String, location: Location?) =
        userRepository.uploadStory(token, file, description, location)

}