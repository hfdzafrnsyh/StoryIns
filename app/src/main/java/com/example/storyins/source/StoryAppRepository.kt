package com.example.storyins.source


import androidx.lifecycle.*
import androidx.paging.*
import com.example.storyins.network.ApiInterface
import com.example.storyins.source.model.Wrapper
import com.example.storyins.source.model.remote.reponse.addStory.AddStoriesResponse
import com.example.storyins.source.model.remote.reponse.login.LoginResponse
import com.example.storyins.source.model.remote.reponse.login.User
import com.example.storyins.source.model.remote.reponse.register.RegisterResponse
import com.example.storyins.source.model.remote.request.LoginRequest
import com.example.storyins.source.model.remote.request.RegisterRequest
import com.example.storyins.source.model.remote.request.StoriesRequest
import com.example.storyins.preference.UserPreference
import com.example.storyins.source.model.local.entity.StoryEntity
import com.example.storyins.source.model.local.entity.UserLocation
import com.example.storyins.source.model.local.room.StoryDatabase
import okhttp3.MultipartBody
import java.lang.Exception


class StoryAppRepository(private val apiInterface: ApiInterface, private val userPreference: UserPreference,
                              private val storyDatabase: StoryDatabase) {

    companion object {
        const val LOCATION_TRUE = 1
         const val SIZE_INDEX = 15
    }



    fun login(loginRequest: LoginRequest) : LiveData<Wrapper<LoginResponse>> = liveData {
        emit(Wrapper.Loading)
        try {

            val response = apiInterface.login(loginRequest)
            emit(Wrapper.Success(response))

        } catch (e : Exception){
            emit(Wrapper.Error("Email or Password Error"))
        }
    }


    fun register(registerRequest: RegisterRequest) : LiveData<Wrapper<RegisterResponse>> = liveData {
        emit(Wrapper.Loading)
        try {

            val response = apiInterface.register(registerRequest)
            emit(Wrapper.Success(response))

        } catch (e : Exception){
            emit(Wrapper.Error("Email is already taken!"))
        }
    }


    fun getStories(token: String) : LiveData<PagingData<StoryEntity>> {

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(token,apiInterface,storyDatabase),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories()
            }
        ).liveData

    }


    fun addStories(photo : MultipartBody.Part, storiesRequest: StoriesRequest, token: String) : LiveData<Wrapper<AddStoriesResponse>> = liveData {
        emit(Wrapper.Loading)
        try {
            val desc = storiesRequest.description
            val lat = storiesRequest.lat
            val lon = storiesRequest.lon

            val response = apiInterface.addStories(photo, desc,lat,lon,token)
            emit(Wrapper.Success(response))
        } catch (e : Exception){
            emit(Wrapper.Error("Failed to add Stories"))
        }
    }



    fun getStoriesWithLocation(token : String) : LiveData<Wrapper<List<StoryEntity>>> = liveData {
        emit(Wrapper.Loading)
        try {
            val response = apiInterface.storiesWithLocation(token, LOCATION_TRUE, SIZE_INDEX)
            val data = response.listStory
            val storyList = data.map { story ->
                StoryEntity(
                    story.id,
                    story.name,
                    story.description,
                    story.photoUrl,
                    story.createdAt,
                    story.lat,
                    story.lon
                )
            }
            emit(Wrapper.Success(storyList))
        } catch (e: Exception) {
            emit(Wrapper.Error("Failed to get Stories"))
        }

    }

    suspend fun setToken(token : String){
        userPreference.setToken(token)
    }

   suspend fun setLocation(userLocation : UserLocation){
        userPreference.setLocation(userLocation)
    }


    suspend fun setUser(user : User){
       userPreference.setUser(user)
    }

    fun getUser() : LiveData<User> {
       return userPreference.getUser().asLiveData()
    }

    fun getToken() : LiveData<String> {
      return userPreference.getToken().asLiveData()
    }


    fun getLocation() : LiveData<UserLocation> {
      return userPreference.getLocation().asLiveData()
    }

    suspend fun logout(){
        userPreference.logout()
    }

}