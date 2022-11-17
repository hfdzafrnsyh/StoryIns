package com.example.storyins.source.model.remote.reponse.stories


import com.example.storyins.source.model.local.entity.StoryEntity
import com.google.gson.annotations.SerializedName

data class StoriesResponse(

    @SerializedName("error")
    var error: Boolean,
    @SerializedName("message")
    var message: String,
    @SerializedName("listStory")
    var listStory: List<StoryEntity>

)

