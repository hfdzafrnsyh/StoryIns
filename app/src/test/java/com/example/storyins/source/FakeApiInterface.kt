package com.example.storyins.source

import com.example.storyins.network.ApiInterface
import com.example.storyins.source.model.remote.reponse.addStory.AddStoriesResponse
import com.example.storyins.source.model.remote.reponse.login.LoginResponse
import com.example.storyins.source.model.remote.reponse.register.RegisterResponse
import com.example.storyins.source.model.remote.reponse.stories.StoriesResponse
import com.example.storyins.source.model.remote.request.LoginRequest
import com.example.storyins.source.model.remote.request.RegisterRequest
import com.example.storyins.ui.main.DataDummy
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiInterface : ApiInterface {

    private var dummyLoginResponse = DataDummy.generateLoginResponse()
    override suspend fun login(loginRequest: LoginRequest): LoginResponse {
       return dummyLoginResponse
    }

    private var dummyRegisterResponse = DataDummy.generateRegisterResponse()
    override suspend fun register(registerRequest: RegisterRequest): RegisterResponse {
        return dummyRegisterResponse
    }

    private var dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()
    override suspend fun stories(token: String, page: Int, size: Int): StoriesResponse {
        return dummyStoriesResponse
    }

    private var dummyStoriesLocation = DataDummy.generateDummyStoriesResponse()
    override suspend fun storiesWithLocation(
        token: String,
        location: Int,
        size: Int
    ): StoriesResponse {
       return dummyStoriesLocation
    }

    private var dummyAddStories = DataDummy.generateAddStoriesResponse()
    override suspend fun addStories(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?,
        token: String
    ): AddStoriesResponse {
        return dummyAddStories
    }
}