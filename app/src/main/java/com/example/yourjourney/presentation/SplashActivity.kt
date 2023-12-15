package com.example.yourjourney.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.paging.ExperimentalPagingApi
import com.example.yourjourney.R
import com.example.yourjourney.databinding.ActivitySplashBinding
import com.example.yourjourney.manager.SessionHandler

@ExperimentalPagingApi
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private lateinit var pref: SessionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SessionHandler(this)


        val backgroundImage: ImageView = binding.logoapp


        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_splash)
        backgroundImage.startAnimation(slideAnimation)


        Handler(Looper.getMainLooper()).postDelayed({
            if (pref.isLogin) {
                MainActivity.start(this)
            } else {
                WelcomeActivity.start(this)
            }
            finish()
        }, 2000)
    }
}
