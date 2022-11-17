package com.example.storyins.ui.main.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.example.storyins.R
import com.example.storyins.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var authActivityBinding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authActivityBinding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(authActivityBinding.root)

        val pageRequest = intent.getIntExtra("PAGE_REQUEST", 0)

        if(pageRequest == 2) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.fragment_login, true)
                .build()

            Navigation.findNavController(findViewById(R.id.navigation_auth_host))
                .navigate(R.id.fragment_register , null , navOptions)

        }


    }




}