package com.example.storyins.source.model.remote.reponse.login

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    @Expose
    @SerializedName("userId")
    var userId : String?,
    @Expose
    @SerializedName("name")
    var name : String?,
    @Expose
    @SerializedName("token")
    var token : String?
    ) : Parcelable