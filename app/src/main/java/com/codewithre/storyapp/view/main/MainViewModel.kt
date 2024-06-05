package com.codewithre.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.codewithre.storyapp.data.UserRepository
import com.codewithre.storyapp.data.pref.UserModel
import com.codewithre.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _listStory = MutableLiveData<List<ListStoryItem?>>()
    val listStory: LiveData<List<ListStoryItem?>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val stories : LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}