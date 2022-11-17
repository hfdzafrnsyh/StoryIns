package com.example.storyins.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyins.network.RetrofitClient
import com.example.storyins.preference.UserPreference
import com.example.storyins.source.StoryAppRepository
import com.example.storyins.source.model.local.room.StoryDatabase

object Injection {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    fun provideRepository(context: Context) : StoryAppRepository {

        val retrofitClient = RetrofitClient.getApiService()
        val database = StoryDatabase.getInstance(context)
        val userPreference = UserPreference.getInstance(context.dataStore)


        return StoryAppRepository(retrofitClient,userPreference,database)
    }
}