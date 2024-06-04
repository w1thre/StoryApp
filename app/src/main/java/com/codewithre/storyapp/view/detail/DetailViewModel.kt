package com.codewithre.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithre.storyapp.data.UserRepository
import com.codewithre.storyapp.data.remote.response.Story
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    private val _detailStory = MutableLiveData<Story?>()
    val detailStory: LiveData<Story?> = _detailStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDetailStories(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getDetailStory(id)
                _detailStory.value = response.story
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}