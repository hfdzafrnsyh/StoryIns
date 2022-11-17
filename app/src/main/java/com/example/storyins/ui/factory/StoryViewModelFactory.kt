package com.example.storyins.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyins.di.Injection
import com.example.storyins.ui.main.auth.register.RegisterViewModel
import com.example.storyins.ui.main.auth.signin.LoginViewModel
import com.example.storyins.ui.main.MainViewModel
import com.example.storyins.ui.main.story.StoryViewModel

class StoryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                return LoginViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                return RegisterViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                return MainViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                return StoryViewModel(Injection.provideRepository(context)) as T
            }

            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

}