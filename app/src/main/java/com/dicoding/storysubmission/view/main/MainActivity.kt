package com.dicoding.storysubmission.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storysubmission.R
import com.dicoding.storysubmission.databinding.ActivityMainBinding
import com.dicoding.storysubmission.view.ViewModelFactory
import com.dicoding.storysubmission.view.welcome.WelcomeActivity
import com.dicoding.storysubmission.data.Result

class MainActivity : AppCompatActivity() {

    // ViewModel dengan menerapkan Android KTX
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // List of stores RecyclerView layout manager
        val layoutManager = LinearLayoutManager(this)
        binding.rvStoriesList.layoutManager = layoutManager

        // Conditional if user had session
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.getStories()
            }
        }

        // Observe the storyList live data
        viewModel.storyList.observe(this) {
            when (it) {
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    showLoading(false)
                    Log.d("Debug: observe", "Failed to retrieve storyList")
                }

                is Result.Success -> {
                    showLoading(false)
                    adapter = StoryListAdapter(it.data)
                    binding.rvStoriesList.adapter = adapter
                    Log.d("Debug: observe", "storyList parsed to the adapter")
                }
            }
        }

        setupView()
        setMainMenu()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setMainMenu(){
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    viewModel.logout()
                    true
                }
                else -> false
            }
        }
    }

    //    private fun setupAction() {
    //        binding.logoutButton.setOnClickListener {
    //            viewModel.logout()
    //        }
    //    }

    private fun showLoading(isLoading: Boolean) {
        binding.rvStoriesList.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}