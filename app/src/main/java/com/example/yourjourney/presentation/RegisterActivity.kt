package com.example.yourjourney.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.ExperimentalPagingApi
import com.example.yourjourney.R
import com.example.yourjourney.data.dcRegister
import com.example.yourjourney.databinding.ActivityRegisterBinding
import com.example.yourjourney.editviewrespones.EditPassword
import com.example.yourjourney.extention.Constanta
import com.example.yourjourney.extention.isEmailValid
import com.example.yourjourney.extention.showOKDialog
import com.example.yourjourney.extention.showToast
import com.example.yourjourney.model.RegisterViewModel
import com.example.yourjourney.response.ResponseAPI
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagingApi
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val registerViewModel: RegisterViewModel by viewModels()

    private var _activityRegisterBinding: ActivityRegisterBinding? = null
    private val binding get() = _activityRegisterBinding!!

    companion object {
        fun start (context: Context) {
            val intent = Intent(context, RegisterActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(_activityRegisterBinding?.root)

        initAction()



        //animation
        val icRegister: ImageView = findViewById(R.id.ic_register)
        val translateRight = AnimationUtils.loadAnimation(this, R.anim.side_right)
        val translateLeft = AnimationUtils.loadAnimation(this, R.anim.side_left)
        icRegister.startAnimation(translateRight)

        translateRight.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation) {
                icRegister.startAnimation(translateLeft)
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation) {}
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initAction() {

        binding.btnRegister.setOnClickListener {
            val userName = binding.tvName.text.toString()
            val userEmail = binding.tvEmail.text.toString()
            val userPassword = binding.tvPassword.text.toString()

            Handler(Looper.getMainLooper()).postDelayed({
                when {
                    userName.isBlank() -> binding.name.error = getString(R.string.empty_name)
                    userEmail.isBlank() -> binding.email.error = getString(R.string.empty_email)
                    !userEmail.isEmailValid() -> binding.email.error = getString(R.string.email_invalid)
                    userPassword.isBlank() -> binding.password.error = getString(R.string.empty_password)
                    else -> {
                        val request = dcRegister(
                            userName, userEmail, userPassword
                        )
                        registerUser(request)
                    }
                }
            }, Constanta.ACTION_DELAYED_TIME)
        }
        binding.haveAccount.setOnClickListener {
            LoginActivity.start(this)
        }
    }


    private fun registerUser(newUser: dcRegister) {
        registerViewModel.registerUser(newUser).observe(this) { response ->
            when (response) {
                is ResponseAPI.Loading -> {
                    showLoading(true)
                }
                is ResponseAPI.Success -> {
                    try {
                        showLoading(false)
                    } finally {
                        LoginActivity.start(this)
                        finish()
                        showToast(getString(R.string.register_success))
                    }
                }
                is ResponseAPI.Error -> {
                    showLoading(false)
                    showOKDialog(getString(R.string.error), response.errorMessage)
                }
                else -> {
                    showToast(getString(R.string.unknown_condition))
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.backgrounLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.name.isClickable = !isLoading
        binding.name.isEnabled = !isLoading
        binding.email.isClickable = !isLoading
        binding.email.isEnabled = !isLoading
        binding.password.isClickable = !isLoading
        binding.password.isEnabled = !isLoading
        binding.btnRegister.isClickable = !isLoading
    }
}



