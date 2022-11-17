package com.example.storyins.ui.main.viewmodel.auth.signin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyins.source.StoryAppRepository
import com.example.storyins.source.model.Wrapper
import com.example.storyins.source.model.remote.reponse.login.LoginResponse
import com.example.storyins.source.model.remote.reponse.login.User
import com.example.storyins.source.model.remote.request.LoginRequest
import com.example.storyins.ui.main.DataDummy
import com.example.storyins.ui.main.MainDispatcherRule
import com.example.storyins.ui.main.auth.signin.LoginViewModel
import com.example.storyins.ui.main.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
class LoginViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()


    @Mock
    private lateinit var storyAppRepository: StoryAppRepository
    private lateinit var loginViewModel: LoginViewModel




    @Before
    fun setUp(){
        loginViewModel = LoginViewModel(storyAppRepository)
    }


    @Test
    fun `when Login Should Not Error and Return Success`() = runTest{

        val loginRequest = LoginRequest("email","password")

        val dummyLoginResponse = DataDummy.generateLoginResponse()

        val expectedLogin = MutableLiveData<Wrapper<LoginResponse>>()
        expectedLogin.value = Wrapper.Success(dummyLoginResponse)

        Mockito.`when`(storyAppRepository.login(loginRequest)).thenReturn(expectedLogin)

        val loginViewModel = LoginViewModel(storyAppRepository)
        val actualLogin = loginViewModel.login(loginRequest).getOrAwaitValue()

        Mockito.verify(storyAppRepository).login(loginRequest)

        Assert.assertNotNull(actualLogin)
        Assert.assertTrue(actualLogin is Wrapper.Success)
        Assert.assertEquals(dummyLoginResponse, (actualLogin as Wrapper.Success).data)

    }



    @Test
    fun `when Login Should Error and Return Error`() = runTest{

        val loginRequest = LoginRequest("beast@beast.com","aaaaaaaa")

        val dummyLoginResponse = DataDummy.generateDummyErrorResponse()

        val expectedLogin = MutableLiveData<Wrapper<LoginResponse>>()
        expectedLogin.value = Wrapper.Error("Error")

        Mockito.`when`(storyAppRepository.login(loginRequest)).thenReturn(expectedLogin)

        val loginViewModel = LoginViewModel(storyAppRepository)
        val actualLogin = loginViewModel.login(loginRequest).getOrAwaitValue()

        Mockito.verify(storyAppRepository).login(loginRequest)

        Assert.assertNotNull(actualLogin)
        Assert.assertTrue(actualLogin is Wrapper.Error)
        Assert.assertEquals(dummyLoginResponse.message, (actualLogin as Wrapper.Error).msg)

    }


    @Test
    fun `when Token Should Return not Null` () = runTest {
        val dummyUserToken = DataDummy.generateDummyToken()

        val expectedToken = MutableLiveData<String>()
        expectedToken.value = dummyUserToken

        Mockito.`when`(storyAppRepository.getToken()).thenReturn(expectedToken)

        val loginViewModel = LoginViewModel(storyAppRepository)
        val actualToken = loginViewModel.getToken().getOrAwaitValue()

        loginViewModel.setToken(dummyUserToken)

        Mockito.verify(storyAppRepository).setToken(dummyUserToken)

        Assert.assertNotNull(actualToken)

    }



    @Test
    fun `when Token Should Return Empty or Null` () = runTest {
        val dummyUserToken = null

        val expectedToken = MutableLiveData<String>()
        expectedToken.value = dummyUserToken

        Mockito.`when`(storyAppRepository.getToken()).thenReturn(expectedToken)

        val loginViewModel = LoginViewModel(storyAppRepository)
        val actualToken = loginViewModel.getToken().getOrAwaitValue()

        Mockito.verify(storyAppRepository).getToken()

        Assert.assertNull(actualToken)

    }




    @Test
    fun `when User Should Return not Null` () = runTest {

        val dummyUser = DataDummy.generateDummyUser()

        val expectedUser = MutableLiveData<User>()
        expectedUser.value = dummyUser

        Mockito.`when`(storyAppRepository.getUser()).thenReturn(expectedUser)

        val loginViewModel = LoginViewModel(storyAppRepository)
        val actualUser =  loginViewModel.getUser().getOrAwaitValue()

        loginViewModel.setUser(dummyUser)

        Mockito.verify(storyAppRepository).setUser(dummyUser)

        Assert.assertNotNull(actualUser)

    }

    @Test
    fun `when User Should Return Empty or Null` () = runTest {

        val dummyUser = null

        val expectedUser = MutableLiveData<User>()
        expectedUser.value = dummyUser

        Mockito.`when`(storyAppRepository.getUser()).thenReturn(expectedUser)

        val loginViewModel = LoginViewModel(storyAppRepository)
        val actualUser =  loginViewModel.getUser().getOrAwaitValue()

        Mockito.verify(storyAppRepository).getUser()

        Assert.assertNull(actualUser)

    }

}