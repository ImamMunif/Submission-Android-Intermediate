package com.dicoding.storysubmission.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import com.dicoding.storysubmission.view.detail.DetailActivity
import com.dicoding.storysubmission.view.upload.UploadActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setMainMenu()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStoriesList.layoutManager = layoutManager

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.getStories(user.token)
            }
        }

        viewModel.storyList.observe(this) {
            when (it) {
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    showLoading(false)
                }
                is Result.Success -> {
                    showLoading(false)
                    adapter = StoryListAdapter(it.data) { story ->
                        val intent = Intent(this, DetailActivity::class.java).apply {
                            putExtra("storyId", story.id)
                        }
                        startActivity(intent)
                    }
                    binding.rvStoriesList.adapter = adapter
                }
            }
        }

        binding.fabUpload.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
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

    private fun showLoading(isLoading: Boolean) {
        binding.rvStoriesList.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}