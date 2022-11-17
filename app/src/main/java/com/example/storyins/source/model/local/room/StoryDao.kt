package com.example.storyins.source.model.local.room


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyins.source.model.local.entity.StoryEntity


@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStories(story: List<StoryEntity>)

    @Query("SELECT * FROM story")
    fun getStories() : PagingSource<Int, StoryEntity>

    @Query("DELETE FROM story")
    suspend fun deleteStories()

}