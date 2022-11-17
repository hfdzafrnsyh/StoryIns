package com.example.storyins.source.model.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "story")
data class StoryEntity(
    @PrimaryKey
    @field:SerializedName("id")
    var id: String,
    @field:SerializedName("name")
    var name: String?,
    @field:SerializedName("description")
    var description: String?,
    @field:SerializedName("photoUrl")
    var photoUrl: String?,
    @field:SerializedName("createdAt")
    var createdAt: String?,
    @field:SerializedName("lat")
    var lat: Float?=null,
    @field:SerializedName("lon")
    var lon: Float?=null
) : Parcelable
