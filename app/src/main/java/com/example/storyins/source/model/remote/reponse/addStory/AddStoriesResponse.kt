package com.example.storyins.source.model.remote.reponse.addStory

import com.google.gson.annotations.SerializedName


class AddStoriesResponse(
    @SerializedName("error")
    var error : Boolean,
    @SerializedName("message")
    var message : String
)