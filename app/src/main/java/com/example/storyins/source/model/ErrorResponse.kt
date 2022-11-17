package com.example.storyins.source.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error")
    var error : Boolean,
    @SerializedName("message")
    var message : String
)