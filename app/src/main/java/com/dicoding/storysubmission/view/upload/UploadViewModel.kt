package com.dicoding.storysubmission.view.upload

import androidx.lifecycle.ViewModel
import com.dicoding.storysubmission.data.UserRepository
import java.io.File

class UploadViewModel(private val repository: UserRepository) : ViewModel() {
    fun uploadImage(file: File, description: String) = repository.uploadImage(file, description)
}