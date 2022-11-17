package com.example.storyins.ui.main.auth.signin


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyins.source.model.remote.reponse.login.User
import com.example.storyins.source.model.remote.request.LoginRequest
import com.example.storyins.source.StoryAppRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: StoryAppRepository) : ViewModel() {


    fun login(loginRequest: LoginRequest) = loginRepository.login(loginRequest)


    fun setToken(token : String){
        viewModelScope.launch {
            loginRepository.setToken(token)
        }
    }

    fun getToken() = loginRepository.getToken()




    fun setUser(user: User) {
        viewModelScope.launch {
            loginRepository.setUser(user)
        }
    }

    fun getUser() = loginRepository.getUser()




}