package com.codewithre.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithre.storyapp.data.UserRepository
import com.codewithre.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listMapStory = MutableLiveData<List<ListStoryItem>>()
    val listMapStory: LiveData<List<ListStoryItem>> = _listMapStory

    fun getLocationStories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getStoriesWithLocation()
                _listMapStory.value = response.listStory
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}