package com.dicoding.storysubmission.view.signup

import androidx.lifecycle.ViewModel
import com.dicoding.storysubmission.data.UserRepository

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    fun signup(name: String, email: String, password: String) = repository.signup(name, email, password)
}