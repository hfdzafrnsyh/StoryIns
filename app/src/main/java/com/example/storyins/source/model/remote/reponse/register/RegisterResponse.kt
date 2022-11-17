package com.example.storyins.source.model.remote.reponse.register


import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("error")
    var error : Boolean,
    @SerializedName("message")
    var message : String
)