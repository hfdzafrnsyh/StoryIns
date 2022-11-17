package com.example.storyins.ui

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.storyins.ui.main.auth.AuthActivity
import com.example.storyins.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var splashScreenBinding: ActivitySplashScreenBinding
    companion object{
        const val TIME_SPLASH=2500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreenBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(splashScreenBinding.root)


        playAnimate()
        Handler(Looper.getMainLooper()).postDelayed({
            toAuthActivity()
        }, TIME_SPLASH.toLong())
    }


    private fun playAnimate(){
        ObjectAnimator.ofFloat(splashScreenBinding.ivSplash , View.TRANSLATION_Y , 15f, -15f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun toAuthActivity(){
        val intent = Intent(this , AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}