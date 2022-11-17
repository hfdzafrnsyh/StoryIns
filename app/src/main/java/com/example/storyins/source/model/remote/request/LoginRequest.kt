package com.example.storyins.source.model.remote.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginRequest (
        @SerializedName("email")
        var email : String?=null,
        @SerializedName("password")
        var password : String?=null
        ) : Parcelable