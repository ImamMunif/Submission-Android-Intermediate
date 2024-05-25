package com.dicoding.storysubmission.view.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.dicoding.storysubmission.R
import com.dicoding.storysubmission.data.Result
import com.dicoding.storysubmission.data.response.Story
import com.dicoding.storysubmission.databinding.ActivityDetailBinding
import com.dicoding.storysubmission.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val storyId = if (Build.VERSION.SDK_INT >= 33) {
            intent.getStringExtra("storyId")
        } else {
            intent.getStringExtra("storyId")
        }

        viewModel.getSession().observe(this) { user ->
            val token = user.token
            if (storyId != null) {
                viewModel.getStoryById(token, storyId).observe(this) {
                    when (it) {
                        is Result.Loading -> showLoading(true)
                        is Result.Success -> {
                            val storyItem = it.data
                            setDetailData(storyItem)
                            showLoading(false)
                        }
                        is Result.Error -> showLoading(false)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setDetailData(storyItem: Story) {
        binding.apply {
            Glide
                .with(this@DetailActivity)
                .load(storyItem.photoUrl)
                .fitCenter()
                .into(imgItemPhotoDetail)

            tvItemNameDetail.text = storyItem.name
            tvItemDescriptionDetail.text = storyItem.description
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.imgItemPhotoDetail.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}