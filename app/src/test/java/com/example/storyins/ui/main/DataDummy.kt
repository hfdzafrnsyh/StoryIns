package com.example.storyins.ui.main


import com.example.storyins.source.model.ErrorResponse
import com.example.storyins.source.model.local.entity.StoryEntity
import com.example.storyins.source.model.remote.reponse.addStory.AddStoriesResponse
import com.example.storyins.source.model.remote.reponse.login.LoginResponse
import com.example.storyins.source.model.remote.reponse.login.User
import com.example.storyins.source.model.remote.reponse.register.RegisterResponse
import com.example.storyins.source.model.remote.reponse.stories.StoriesResponse
import com.example.storyins.source.model.remote.request.StoriesRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


object DataDummy {

    fun generateDummyStoryEntity() : List<StoryEntity>{
        val listStory : MutableList<StoryEntity> = arrayListOf()
        for( i in 0..15){
            val story = StoryEntity(
                i.toString(),
                 "reviewer123",
            "tesssssssssss",
            "https://story-api.dicoding.dev/images/stories/photos-1667058715359_iRitxc-I.jpg",
            "2022-10-29T15:51:55.361Z",
            37.4220936f,
            -122.083922f
            )
            listStory.add(story)
        }
        return listStory
    }


    fun generateDummyStoriesResponse(): StoriesResponse {
        val listStory = ArrayList<StoryEntity>()
        for (i in 0..15) {
            val story = StoryEntity(
                i.toString(),
                "reviewer123",
                "tesssssssssss",
                "https://story-api.dicoding.dev/images/stories/photos-1667058715359_iRitxc-I.jpg",
                "2022-10-29T15:51:55.361Z",
                37.4220936f,
                -122.083922f
            )
            listStory.add(story)
        }
        return StoriesResponse(false,  "Success", listStory)
    }

    fun generateDummyToken(): String {
        return "token"
    }

    fun generateDummyUser() : User{
        return User("userId" , "kaido" ,"token")
    }

    fun generateDummyErrorResponse() : ErrorResponse{
        return ErrorResponse(true,"Error")
    }

    fun generateLoginResponse() : LoginResponse{
        return  LoginResponse(false,"success" , this.generateDummyUser())
    }

    fun generateRegisterResponse() : RegisterResponse{
        return RegisterResponse(false,"user created Success")
    }


    fun generateAddStoriesResponse() : AddStoriesResponse{
        return AddStoriesResponse(false, "Success")
    }

    fun generateDummyStoriesRequest() : StoriesRequest{
       val description = "arararra".toRequestBody("text/plain".toMediaType())
       val lat = "0f".toRequestBody("text/plain".toMediaType())
       val lon = "0f".toRequestBody("text/plain".toMediaType())
          return StoriesRequest(description,lat,lon)
    }


}