package com.example.storyins.ui.main.viewmodel.auth.register

import com.example.storyins.ui.main.auth.register.RegisterViewModel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyins.source.StoryAppRepository
import com.example.storyins.source.model.Wrapper
import com.example.storyins.source.model.remote.reponse.register.RegisterResponse
import com.example.storyins.source.model.remote.request.RegisterRequest
import com.example.storyins.ui.main.DataDummy
import com.example.storyins.ui.main.MainDispatcherRule
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
class RegisterViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()


    @Mock
    private lateinit var storyAppRepository: StoryAppRepository
    private lateinit var registerViewModel: RegisterViewModel



    @Before
    fun setUp(){
        registerViewModel = RegisterViewModel(storyAppRepository)
    }


    @Test
    fun `when Register Should Not Error and Return Success`() = runTest{

        val registerRequest = RegisterRequest("newuser","newuser@test.com","aaaaaaaa")

        val dummyRegisterResponse = DataDummy.generateRegisterResponse()


        val expectedRegister = MutableLiveData<Wrapper<RegisterResponse>>()
        expectedRegister.value = Wrapper.Success(dummyRegisterResponse)

        Mockito.`when`(storyAppRepository.register(registerRequest)).thenReturn(expectedRegister)

        val registerViewModel = RegisterViewModel(storyAppRepository)
        val actualRegister = registerViewModel.register(registerRequest).getOrAwaitValue()

        Mockito.verify(storyAppRepository).register(registerRequest)

        Assert.assertNotNull(actualRegister)
        Assert.assertTrue(actualRegister is Wrapper.Success)
        Assert.assertEquals(dummyRegisterResponse, (actualRegister as Wrapper.Success).data)

    }


    @Test
    fun `when Register Should Error and Return Error Email is ready Taken`() = runTest{

        val registerRequest = RegisterRequest("newuser","newuser@test.com","aaaaaaaa")

        val dummyRegisterResponse = DataDummy.generateDummyErrorResponse()

        val expectedRegister = MutableLiveData<Wrapper<RegisterResponse>>()
        expectedRegister.value = Wrapper.Error("Error")

        Mockito.`when`(storyAppRepository.register(registerRequest)).thenReturn(expectedRegister)

        val registerViewModel = RegisterViewModel(storyAppRepository)
        val actualRegister = registerViewModel.register(registerRequest).getOrAwaitValue()

        Mockito.verify(storyAppRepository).register(registerRequest)

        Assert.assertNotNull(actualRegister)
        Assert.assertTrue(actualRegister is Wrapper.Error)
        Assert.assertEquals(dummyRegisterResponse.message, (actualRegister as Wrapper.Error).msg)


    }



}