package com.example.yourjourney.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.yourjourney.R
import com.example.yourjourney.databinding.ActivityDetailBinding
import com.example.yourjourney.extention.Value
import com.example.yourjourney.extention.setImageUrl
import com.example.yourjourney.identity.Journey
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private var _activityDetailBinding: ActivityDetailBinding? = null
    private val binding get() = _activityDetailBinding!!

    private lateinit var journey: Journey

    companion object {
        private const val EXTRA_JOURNEY = "extra_journey"

        fun start(context: Context, journey: Journey) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_JOURNEY, journey)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(_activityDetailBinding?.root)

        initIntent()
        initUI()

    }

    private fun initIntent() {
        journey = intent.getParcelableExtra(Value.BUNDLE_KEY_STORY) ?: Journey("", "", "", "", "", 0.0 ,0.0 )
    }

    private fun initUI() {
        if (::journey.isInitialized) {
            binding.apply {
                imgThumbnail.setImageUrl(journey.photoUrl, true)
                tvTitle.text = journey.name
                tvDesc.text = journey.description
            }
            title = "Detail Journey"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            println("Journey is not initialized!")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onNavigateUp()
    }



}