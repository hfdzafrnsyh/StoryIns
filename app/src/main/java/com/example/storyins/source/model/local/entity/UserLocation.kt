package com.example.storyins.source.model.local.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserLocation(
    @SerializedName("lat")
    var lat: String? =null,
    @SerializedName("lon")
    var lon: String? =null
) : Parcelable
