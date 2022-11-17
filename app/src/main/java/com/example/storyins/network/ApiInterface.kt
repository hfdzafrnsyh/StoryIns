package com.example.storyins.network


import com.example.storyins.source.model.remote.reponse.addStory.AddStoriesResponse
import com.example.storyins.source.model.remote.reponse.login.LoginResponse
import com.example.storyins.source.model.remote.reponse.register.RegisterResponse
import com.example.storyins.source.model.remote.reponse.stories.StoriesResponse
import com.example.storyins.source.model.remote.request.LoginRequest
import com.example.storyins.source.model.remote.request.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiInterface {

    @POST("login")
   suspend fun login(
        @Body
        loginRequest: LoginRequest
    ) : LoginResponse

    @POST("register")
    suspend fun register(
        @Body
        registerRequest: RegisterRequest
    ) : RegisterResponse

    @GET("stories")
   suspend fun stories(
        @Header("Authorization") token : String,
        @Query("page") page : Int ,
        @Query("size") size : Int
    ) : StoriesResponse

    @GET("stories")
    suspend fun storiesWithLocation(
        @Header("Authorization") token : String,
        @Query("location") location : Int,
        @Query("size") size : Int
    ) : StoriesResponse

    @POST("stories")
    @Multipart
   suspend fun addStories(
        @Part
        photo: MultipartBody.Part,
        @Part("description")
        description: RequestBody,
        @Part("lat")
        lat: RequestBody?,
        @Part("lon")
        lon: RequestBody?,
        @Header("Authorization") token: String
    ) : AddStoriesResponse
}