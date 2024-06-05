package com.codewithre.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codewithre.storyapp.data.UserRepository
import com.codewithre.storyapp.di.Injection
import com.codewithre.storyapp.view.createstory.CreateStoryViewModel
import com.codewithre.storyapp.view.detail.DetailViewModel
import com.codewithre.storyapp.view.main.MainViewModel
import com.codewithre.storyapp.view.maps.MapsViewModel
import com.codewithre.storyapp.view.signup.SignupViewModel

class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CreateStoryViewModel::class.java) -> {
                CreateStoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context)= ViewModelFactory(Injection.provideRepository(context))
    }
}