package com.example.storyins.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.storyins.source.model.local.entity.UserLocation
import com.example.storyins.source.model.remote.reponse.login.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {


    fun getUser() : Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[ID_KEY] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: ""
            )
        }
    }

    suspend fun setUser(user: User) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = user.userId.toString()
            preferences[NAME_KEY] = user.name.toString()
            preferences[TOKEN_KEY] = user.token.toString()
        }
    }


     fun getToken() : Flow<String>{
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    fun getLocation() : Flow<UserLocation> {
        return dataStore.data.map { preferences ->
            UserLocation(
                preferences[LAT_KEY] ?: "",
                preferences[LON_KEY] ?: "",
            )
        }
    }

    suspend fun setLocation( location : UserLocation){
        dataStore.edit { preferences ->
            preferences[LAT_KEY]=location.lat.toString()
            preferences[LON_KEY]=location.lon.toString()
        }
    }

     suspend fun setToken(token : String){
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }


    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[ID_KEY]=""
            preferences[NAME_KEY] = ""
            preferences[TOKEN_KEY] = ""
            preferences[LAT_KEY]=""
            preferences[LON_KEY]=""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val ID_KEY = stringPreferencesKey("userId")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val LAT_KEY = stringPreferencesKey("lat")
        private val LON_KEY = stringPreferencesKey("lon")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
