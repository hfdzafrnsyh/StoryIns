package com.example.storyins.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.storyins.source.StoryAppRepository
import com.example.storyins.source.model.local.entity.StoryEntity
import com.example.storyins.source.model.local.entity.UserLocation
import kotlinx.coroutines.launch


class MainViewModel(private val mainRepository : StoryAppRepository) : ViewModel() {


    fun stories(token: String): LiveData<PagingData<StoryEntity>> =
        mainRepository.getStories(token)


    fun getStoriesWithLocation(token: String) =
        mainRepository.getStoriesWithLocation(token)



    fun setLocation(userLocation: UserLocation) {
      viewModelScope.launch {
          mainRepository.setLocation(userLocation)
      }
    }

    fun getLocation() = mainRepository.getLocation()

    fun getToken() = mainRepository.getToken()

    fun logout(){
        viewModelScope.launch {
            mainRepository.logout()
        }
    }


}