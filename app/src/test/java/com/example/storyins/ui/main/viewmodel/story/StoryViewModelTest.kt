package com.example.storyins.ui.main.viewmodel.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyins.source.StoryAppRepository
import com.example.storyins.source.model.Wrapper
import com.example.storyins.source.model.remote.reponse.addStory.AddStoriesResponse
import com.example.storyins.ui.main.DataDummy
import com.example.storyins.ui.main.MainDispatcherRule
import com.example.storyins.ui.main.getOrAwaitValue
import com.example.storyins.ui.main.story.StoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class StoryViewModelTest{


    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()


    @Mock
    private lateinit var storyAppRepository: StoryAppRepository
    private lateinit var storyViewModel: StoryViewModel

    @Mock
    private lateinit var file: MultipartBody.Part


    @Before
    fun setUp(){
        storyViewModel = StoryViewModel(storyAppRepository)
    }


    @Test
    fun `when add Story Should Not Error and Return Success`() = runTest {

        val token = DataDummy.generateDummyToken()

        val photoImg = file
        val storiesRequest = DataDummy.generateDummyStoriesRequest()

        val dummyAddStoryResponse = DataDummy.generateAddStoriesResponse()

        val expectedStory = MutableLiveData<Wrapper<AddStoriesResponse>>()
        expectedStory.value = Wrapper.Success(dummyAddStoryResponse)

        Mockito.`when`(storyAppRepository.addStories(photoImg,storiesRequest,token)).thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyAppRepository)
        val actualStory = storyViewModel.addStories(photoImg,storiesRequest,token).getOrAwaitValue()
        Mockito.verify(storyAppRepository).addStories(photoImg,storiesRequest,token)

        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Wrapper.Success)
        Assert.assertEquals(dummyAddStoryResponse, (actualStory as Wrapper.Success).data)
    }

    @Test
    fun `when add Story Return Error`() = runTest {

        val token = DataDummy.generateDummyToken()

        val photoImg = file
        val storiesRequest = DataDummy.generateDummyStoriesRequest()

        val dummyAddStoryResponse = DataDummy.generateDummyErrorResponse()

        val expectedStory = MutableLiveData<Wrapper<AddStoriesResponse>>()
        expectedStory.value = Wrapper.Error(dummyAddStoryResponse.message)

        Mockito.`when`(storyAppRepository.addStories(photoImg,storiesRequest,token)).thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyAppRepository)
        val actualStory = storyViewModel.addStories(photoImg,storiesRequest,token).getOrAwaitValue()
        Mockito.verify(storyAppRepository).addStories(photoImg,storiesRequest,token)

        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Wrapper.Error)
        Assert.assertEquals(dummyAddStoryResponse.message, (actualStory as Wrapper.Error).msg)
    }


}