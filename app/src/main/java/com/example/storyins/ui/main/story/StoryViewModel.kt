package com.example.storyins.ui.main.story


import androidx.lifecycle.*
import com.example.storyins.source.model.remote.request.StoriesRequest
import com.example.storyins.source.StoryAppRepository
import okhttp3.MultipartBody


class StoryViewModel(private val addStoryRepository: StoryAppRepository) : ViewModel() {

    fun addStories(photo : MultipartBody.Part, storiesRequest: StoriesRequest, token : String) =
        addStoryRepository.addStories(photo,storiesRequest,token)

}