package com.dicoding.storysubmission.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storysubmission.R
import com.dicoding.storysubmission.databinding.ActivityMainBinding
import com.dicoding.storysubmission.view.ViewModelFactory
import com.dicoding.storysubmission.view.adapter.LoadingStateAdapter
import com.dicoding.storysubmission.view.welcome.WelcomeActivity
import com.dicoding.storysubmission.view.adapter.StoryListAdapter
import com.dicoding.storysubmission.view.detail.DetailActivity
import com.dicoding.storysubmission.view.map.MapsActivity
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

        showLoading(true)

        setupView()
        setMainMenu()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStoriesList.layoutManager = layoutManager

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        binding.fabUpload.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        getData()
    }

    private fun getData() {
        adapter = StoryListAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("storyId", story.id)
            }
            startActivity(intent)
        }

        binding.rvStoriesList.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.storyList.observe(this) {
            adapter.submitData(lifecycle, it)
        }
        showLoading(false)
    }

    private fun setupView() {
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        supportActionBar?.hide()
    }

    private fun setMainMenu() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.logout))
                        setMessage(getString(R.string.logout_message))
                        setPositiveButton(R.string.yes) { _, _ ->
                            viewModel.logout()
                        }
                        setNegativeButton(R.string.no) { _, _ ->

                        }
                        create()
                        show()
                    }
                    true
                }

                R.id.menu_maps -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
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