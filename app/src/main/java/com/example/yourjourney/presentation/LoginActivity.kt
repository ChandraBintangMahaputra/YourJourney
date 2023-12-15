package com.example.yourjourney.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.paging.ExperimentalPagingApi
import com.example.yourjourney.R
import com.example.yourjourney.data.dcLogin
import com.example.yourjourney.databinding.ActivityLoginBinding
import com.example.yourjourney.databinding.ActivityRegisterBinding
import com.example.yourjourney.editviewrespones.EditPassword
import com.example.yourjourney.extention.Value.KEY_EMAIL
import com.example.yourjourney.extention.Value.KEY_IS_LOGIN
import com.example.yourjourney.extention.Value.KEY_TOKEN
import com.example.yourjourney.extention.Value.KEY_USER_ID
import com.example.yourjourney.extention.Value.KEY_USER_NAME
import com.example.yourjourney.extention.gone
import com.example.yourjourney.extention.show
import com.example.yourjourney.extention.showOKDialog
import com.example.yourjourney.extention.showToast
import com.example.yourjourney.manager.SessionHandler
import com.example.yourjourney.model.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.yourjourney.presentation.RegisterActivity
import com.example.yourjourney.response.ResponseAPI

@ExperimentalPagingApi
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    private var _activityLoginBinding: ActivityLoginBinding? = null

    private val binding get() = _activityLoginBinding!!

    private lateinit var pref: SessionHandler

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_activityLoginBinding?.root)

        //animation
        val icLogin: ImageView = findViewById(R.id.ic_login)
        val translateRight = AnimationUtils.loadAnimation(this, R.anim.side_right)
        val translateLeft = AnimationUtils.loadAnimation(this, R.anim.side_left)

        icLogin.startAnimation(translateRight)
        translateRight.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation) {
                icLogin.startAnimation(translateLeft)
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation) {}
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pref = SessionHandler(this)

        if (pref.isLogin) {
            MainActivity.start(this)
            finish()
        }

        initAction()
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

        binding.btnLogin.setOnClickListener {
            val userEmail = binding.rvEmail.text.toString()
            val userPassword = binding.rvPassword.text.toString()

            when {
                userEmail.isBlank() -> {
                    binding.emailLogin.requestFocus()
                    binding.emailLogin.error = getString(R.string.empty_password)
                }
                userPassword.isBlank() -> {
                    binding.passwordLogin.requestFocus()
                    binding.passwordLogin.error = getString(R.string.empty_password)
                }

                else -> {
                    val request = dcLogin(
                        userEmail, userPassword
                    )

                    loginUser(request, userEmail)
                }
            }
        }
        binding.haventAccount.setOnClickListener {
            RegisterActivity.start(this)
        }
    }

    private fun loginUser(loginBody: dcLogin, email: String) {
        loginViewModel.loginUser(loginBody).observe(this) { response ->
            when (response) {
                is ResponseAPI.Loading -> {
                    showLoading(true)
                }
                is ResponseAPI.Success -> {
                    try {
                        showLoading(false)
                        val userData = response.data.loginResult
                        pref.apply {
                            setStringPreference(KEY_USER_ID, userData.userId)
                            setStringPreference(KEY_TOKEN, userData.token)
                            setStringPreference(KEY_USER_NAME, userData.name)
                            setStringPreference(KEY_EMAIL, email)
                            setBooleanPreference(KEY_IS_LOGIN, true)
                        }
                    } finally {
                        MainActivity.start(this)
                    }
                }
                is ResponseAPI.Error -> {
                    showLoading(false)
                    showOKDialog(getString(R.string.error), getString(R.string.login_failed))
                }
                else -> {
                    showToast(getString(R.string.unknown_condition))
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.show() else binding.progressBar.gone()
        if (isLoading) binding.backgrounLoading.show() else binding.backgrounLoading.gone()
        binding.emailLogin.isClickable = !isLoading
        binding.emailLogin.isEnabled = !isLoading
        binding.passwordLogin.isClickable = !isLoading
        binding.passwordLogin.isEnabled = !isLoading
        binding.btnLogin.isClickable = !isLoading
    }
}