package com.example.yourjourney.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yourjourney.R
import com.example.yourjourney.adapter.JourneyAdapter
import com.example.yourjourney.adapter.LoadingAdapter
import com.example.yourjourney.database.JourneyDb
import com.example.yourjourney.databinding.ActivityMainBinding
import com.example.yourjourney.extention.hide
import com.example.yourjourney.extention.isTrue
import com.example.yourjourney.extention.show
import com.example.yourjourney.extention.toBearerToken
import com.example.yourjourney.manager.SessionHandler
import com.example.yourjourney.model.JourneyViewModel
import com.example.yourjourney.response.ResponseAPI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlinx.coroutines.flow.collectLatest
import com.example.yourjourney.extention.gone
import com.example.yourjourney.identity.Journey

@ExperimentalPagingApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val storyViewModel: JourneyViewModel by viewModels()

    private var _activityMainBinding: ActivityMainBinding? = null
    private val binding get() = _activityMainBinding!!

    private lateinit var pref: SessionHandler
    private var token: String? = null

    private lateinit var adapter: JourneyAdapter

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_activityMainBinding?.root)
        pref = SessionHandler(this)
        token = pref.getToken
        Timber.d("Token: $token")
        initUI()
        initAction()
        initAdapter()
        getAllJourney("Bearer $token")
    }

    private fun initUI() {
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager?.onRestoreInstanceState(binding.rvStories.layoutManager?.onSaveInstanceState())
    }

    private fun initAction() {
        binding.addNewJourney.setOnClickListener {
            AddJourneyActivity.start(this)
        }
        binding.srStories.setOnRefreshListener {
            getAllJourney("Bearer $token")
        }
    }

    private fun initAdapter() {
        adapter = JourneyAdapter()
        binding.rvStories.layoutManager?.onRestoreInstanceState(binding.rvStories.layoutManager?.onSaveInstanceState())
        binding.rvStories.adapter = adapter

        lifecycleScope.launch {
            adapter.loadStateFlow.distinctUntilChanged { old, new ->
                old.mediator?.prepend?.endOfPaginationReached.isTrue() == new.mediator?.prepend?.endOfPaginationReached.isTrue()
            }
                .filter { it.refresh is LoadState.NotLoading && it.prepend.endOfPaginationReached && !it.append.endOfPaginationReached }
                .collect {
                    binding.rvStories.scrollToPosition(0)
                }
        }
        adapter.withLoadStateFooter(
            footer = LoadingAdapter {
                adapter.retry()
            }
        )
        adapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    isError(false)
                    isLoading(true)
                }
                is LoadState.NotLoading -> {
                    isError(false)
                    isLoading(false)
                }
                is LoadState.Error -> isError(true)
                else -> {
                    Timber.e("Unknown condition")
                }
            }
        }
    }

    private fun getAllJourney(token: String) {
        storyViewModel.getAllJourney(token).observe(this) { stories ->
            initRecyclerViewUpdate(stories)
        }
    }

    private fun initRecyclerViewUpdate(storiesData: PagingData<JourneyDb>) {
        val recyclerViewState = binding.rvStories.layoutManager?.onSaveInstanceState()

        adapter.submitData(lifecycle, storiesData)
        binding.srStories.isRefreshing = false

        binding.rvStories.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun isError(error: Boolean) {
        if (error) {
            binding.tvStoriesError.show()
            binding.rvStories.hide()
        } else {
            binding.tvStoriesError.hide()
            binding.rvStories.show()
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            Log.d("Loading", "Lagi loading")
            binding.rvStories.hide()
            binding.shimmerLoading.show()
            binding.shimmerLoading.startShimmer()
        } else {
            Log.d("Loading", "Loading selesai")
            binding.rvStories.show()
            binding.shimmerLoading.stopShimmer()
            binding.shimmerLoading.gone()
        }
    }

    private fun openLogoutDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Are you sure want to logout?")
            ?.setPositiveButton("Yes") { _, _ ->
                pref.clearPreferences()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            ?.setNegativeButton(getString(R.string.cancel), null)
        val alert = alertDialog.create()
        alert.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            R.id.logout -> {
                openLogoutDialog()
                pref.clearPreferences()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToLoginPage() {
        val homeIntent = Intent(this, LoginActivity::class.java)
        startActivity(homeIntent)
        finish()
    }


}