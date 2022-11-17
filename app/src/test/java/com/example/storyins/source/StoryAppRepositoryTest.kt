package com.example.storyins.source


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storyins.network.ApiInterface
import com.example.storyins.preference.UserPreference
import com.example.storyins.source.model.Wrapper
import com.example.storyins.source.model.local.entity.UserLocation
import com.example.storyins.source.model.local.room.StoryDatabase
import com.example.storyins.source.model.remote.reponse.login.User
import com.example.storyins.source.model.remote.request.LoginRequest
import com.example.storyins.source.model.remote.request.RegisterRequest
import com.example.storyins.ui.main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner



@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StoryAppRepositoryTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var storyAppRepository: StoryAppRepository

    @Mock
    private lateinit var userPreference : UserPreference

    @Mock
     lateinit var apiInterface : ApiInterface

    @Mock
     lateinit var storyDatabase : StoryDatabase


    @Mock
    private lateinit var file: MultipartBody.Part


    @Before
    fun setUp() {
        apiInterface = FakeApiInterface()
        storyAppRepository = StoryAppRepository(apiInterface, userPreference,storyDatabase)
    }



    @Test
    fun `when Login Should  Return Success`() = runTest {

        val expectedLogin= DataDummy.generateLoginResponse()
        val loginRequest = LoginRequest("email" , "password")

        val actualLogin = storyAppRepository.login(loginRequest)
        actualLogin.observeForTesting {
            Assert.assertNotNull(actualLogin)
            Assert.assertEquals(
                expectedLogin,
                (actualLogin.value as Wrapper.Success).data
            )
        }

    }





    @Test
    fun `when Register Should  Return Success`() = runTest {

        val expectedRegister= DataDummy.generateRegisterResponse()
        val registerRequest = RegisterRequest("name","email" , "password")

        val actualRegister = storyAppRepository.register(registerRequest)
        actualRegister.observeForTesting {
            Assert.assertNotNull(actualRegister)
            Assert.assertEquals(
                expectedRegister,
                (actualRegister.value as Wrapper.Success).data
            )
        }

    }



    @Test
    fun `when Get Stories with Location Should not Null and Return Success`() = runTest{

        val token = DataDummy.generateDummyToken()
        val expectedStories = DataDummy.generateDummyStoriesResponse()

        val actualStories = storyAppRepository.getStoriesWithLocation(token)
        actualStories.observeForTesting {
            Assert.assertNotNull(actualStories)
            Assert.assertEquals(expectedStories.listStory.size ,
                (actualStories.value as Wrapper.Success).data.size
            )
        }

    }





    @Test
    fun `when Add Stories Return Success` () = runTest {
        val token = DataDummy.generateDummyToken()
        val photoImg = file

        val expectedStories= DataDummy.generateAddStoriesResponse()
        val storiesRequest = DataDummy.generateDummyStoriesRequest()

        val actualAddStories = storyAppRepository.addStories(photoImg,storiesRequest,token)
        actualAddStories.observeForTesting {
            Assert.assertNotNull(actualAddStories)
            Assert.assertEquals(
                expectedStories.message,
                (actualAddStories.value as Wrapper.Success).data.message
                )
        }
    }





    @Test
    fun `when Token Should not Null and Return Token` () = runTest {
        val dummyUserToken =  DataDummy.generateDummyToken()

        val expect:Flow<String> = flow {
            emit(dummyUserToken)
        }

        Mockito.`when`(userPreference.getToken()).thenReturn(expect)

        val actualToken = storyAppRepository.getToken().getOrAwaitValue()

        Assert.assertNotNull(actualToken)

    }


    @Test
    fun `when Token Should not Null but Return Empty` () = runTest {
        val dummyToken = ""

        val expect: Flow<String> = flow {
            emit(dummyToken)
        }

        Mockito.`when`(userPreference.getToken()).thenReturn(expect)

        val actualToken = storyAppRepository.getToken().getOrAwaitValue()

        Assert.assertNotNull(actualToken)
    }

    @Test
    fun `when User Should not Null and Return User data` () = runTest {
        val dummyUser = DataDummy.generateDummyUser()

        val expect : Flow<User> = flow {
            emit(dummyUser)
        }

        Mockito.`when`(userPreference.getUser()).thenReturn(expect)

        val actualUser =  storyAppRepository.getUser().getOrAwaitValue()

        Assert.assertNotNull(actualUser)

    }

    @Test
    fun `when User Should not Null but Return Empty` () = runTest {
        val dummyUser = User("","" , "")

        val expectedUser : Flow<User> = flow {
            emit(dummyUser)
        }

        Mockito.`when`(userPreference.getUser()).thenReturn(expectedUser)

        val actualUser = storyAppRepository.getUser().getOrAwaitValue()

        Assert.assertNotNull(actualUser)

    }

    @Test
    fun `when UserLocation Should not Null and Return UserLocation data` () = runTest {
        val dummyUserLocation = UserLocation("0","0")

        val expectLocation : Flow<UserLocation> = flow {
            emit(dummyUserLocation)
        }

        Mockito.`when`(userPreference.getLocation()).thenReturn(expectLocation)

       val actualUserLocation = storyAppRepository.getLocation().getOrAwaitValue()


        Assert.assertNotNull(actualUserLocation)
    }

    @Test
    fun `when UserLocation Should not Null but Return Empty` () = runTest {
        val dummyUserLocation = UserLocation("","")

        val expectLocation : Flow<UserLocation> = flow {
            emit(dummyUserLocation)
        }

        Mockito.`when`(userPreference.getLocation()).thenReturn(expectLocation)

        val actualUserLocation = storyAppRepository.getLocation().getOrAwaitValue()


        Assert.assertNotNull(actualUserLocation)

    }

    @Test
    fun `when User Logout and Return token String Empty` () = runTest {

        val dummyToken = ""

        val expect: Flow<String> = flow {
            emit(dummyToken)
        }

        storyAppRepository.logout()

        Mockito.`when`(userPreference.getToken()).thenReturn(expect)

        val actualToken = storyAppRepository.getToken().getOrAwaitValue()

        Mockito.verify(userPreference).logout()

        Assert.assertNotNull(actualToken)

    }

}

