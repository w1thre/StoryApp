package com.codewithre.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codewithre.storyapp.data.AuthRepository
import com.codewithre.storyapp.di.AuthInjection
import com.codewithre.storyapp.view.login.LoginViewModel

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context)= AuthViewModelFactory(AuthInjection.provideRepository(context))
    }
}