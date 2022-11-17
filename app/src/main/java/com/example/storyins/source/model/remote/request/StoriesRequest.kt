package com.example.storyins.source.model.remote.request


import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody


data class StoriesRequest(
    @SerializedName("description")
    var description: RequestBody,
    @SerializedName("lat")
    var lat: RequestBody?,
    @SerializedName("lon")
    var lon: RequestBody?
)