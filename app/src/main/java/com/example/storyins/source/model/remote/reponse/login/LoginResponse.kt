package com.example.storyins.source.model.remote.reponse.login


import com.google.gson.annotations.SerializedName

data class LoginResponse (
        @SerializedName("error")
        var error : Boolean,
        @SerializedName("message")
        var message : String,
        @SerializedName("loginResult")
        var loginResult : User
    )