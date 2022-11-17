package com.example.storyins.ui.main.auth.register


import androidx.lifecycle.ViewModel
import com.example.storyins.source.model.remote.request.RegisterRequest
import com.example.storyins.source.StoryAppRepository


class RegisterViewModel(private val registerRepository: StoryAppRepository) : ViewModel() {

    fun register(registerRequest: RegisterRequest) = registerRepository.register(registerRequest)


}