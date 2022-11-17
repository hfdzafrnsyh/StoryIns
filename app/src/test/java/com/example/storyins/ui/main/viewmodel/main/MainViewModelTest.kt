package com.example.storyins.ui.main.viewmodel.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyins.source.StoryAppRepository
import com.example.storyins.source.model.Wrapper
import com.example.storyins.source.model.local.entity.StoryEntity
import com.example.storyins.source.model.local.entity.UserLocation
import com.example.storyins.ui.adapter.StoryViewAdapter
import com.example.storyins.ui.main.DataDummy
import com.example.storyins.ui.main.MainDispatcherRule
import com.example.storyins.ui.main.MainViewModel
import com.example.storyins.ui.main.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()


    @Mock
    private lateinit var storyAppRepository: StoryAppRepository

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp(){
        mainViewModel = MainViewModel(storyAppRepository)
    }


    @Test
    fun `when Get Story Should Not Null and Return Success`() = runTest {
        val token = DataDummy.generateDummyToken()
        val dummyStory = DataDummy.generateDummyStoryEntity()
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data

        Mockito.`when`(storyAppRepository.getStories(token)).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(storyAppRepository)
        val actualStory: PagingData<StoryEntity> = mainViewModel.stories(token).getOrAwaitValue()


        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryViewAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory, differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)

    }



    @Test
    fun `when Get Story With Location Should Not Null and Return Success`() = runTest {
        val token = DataDummy.generateDummyToken()
        val dummyStory = DataDummy.generateDummyStoryEntity()
        val data: List<StoryEntity> = dummyStory

        val expectedStory = MutableLiveData<Wrapper<List<StoryEntity>>>()
        expectedStory.value = Wrapper.Success(data)

        Mockito.`when`(storyAppRepository.getStoriesWithLocation(token)).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(storyAppRepository)
        val actualStory = mainViewModel.getStoriesWithLocation(token).getOrAwaitValue()
        Mockito.verify(storyAppRepository).getStoriesWithLocation(token)

        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Wrapper.Success)
        Assert.assertEquals(dummyStory.size, (actualStory as Wrapper.Success).data.size)

    }


    @Test
    fun `when Get Story With Location  Return Error`() = runTest{
        val token = DataDummy.generateDummyToken()
        val expectedStory = MutableLiveData<Wrapper<List<StoryEntity>>>()
        expectedStory.value = Wrapper.Error("Error")

        Mockito.`when`(storyAppRepository.getStoriesWithLocation(token)).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(storyAppRepository)
        val actualStory = mainViewModel.getStoriesWithLocation(token).getOrAwaitValue()

        Mockito.verify(storyAppRepository).getStoriesWithLocation(token)

        Assert.assertNotNull(actualStory)
        Assert.assertTrue(actualStory is Wrapper.Error)
    }

    @Test
    fun `when UserLocation Should Return not Null` () = runTest {
        val dummyUserLocation = UserLocation("0","0")

        val expectedLocation = MutableLiveData<UserLocation>()
        expectedLocation.value = dummyUserLocation

        Mockito.`when`(storyAppRepository.getLocation()).thenReturn(expectedLocation)

        val mainViewModel = MainViewModel(storyAppRepository)
        val actualUserLocation =  mainViewModel.getLocation().getOrAwaitValue()
        mainViewModel.setLocation(dummyUserLocation)

        Mockito.verify(storyAppRepository).setLocation(dummyUserLocation)

        Assert.assertNotNull(actualUserLocation)

    }


    @Test
    fun `when UserLocation Should Return Empty or Null` () = runTest {
        val dummyUserLocation = null

        val expectedLocation = MutableLiveData<UserLocation>()
        expectedLocation.value = dummyUserLocation

        Mockito.`when`(storyAppRepository.getLocation()).thenReturn(expectedLocation)

        val mainViewModel = MainViewModel(storyAppRepository)
        val actualUserLocation = mainViewModel.getLocation().getOrAwaitValue()

        Assert.assertNull(actualUserLocation)
    }


    @Test
    fun `when User Logout Should Return token Null` () = runTest {

        val dummyToken = null
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = dummyToken

        val mainViewModel = MainViewModel(storyAppRepository)
        mainViewModel.logout()

        Mockito.`when`(storyAppRepository.getToken()).thenReturn(expectedToken)

        val actualToken = mainViewModel.getToken().getOrAwaitValue()

        Mockito.verify(storyAppRepository).logout()

        Assert.assertNull(actualToken)


    }


}


class StoryPagingSource : PagingSource<Int, LiveData<List<StoryEntity>>>() {
    companion object {
        fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

