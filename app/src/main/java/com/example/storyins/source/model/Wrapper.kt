package com.example.storyins.source.model

sealed class Wrapper<out R>private constructor() {

    data class Success<out T>(val data: T) : Wrapper<T>()
    object  Loading : Wrapper<Nothing>()
    data class Error(val msg: String?) : Wrapper<Nothing>()

}


