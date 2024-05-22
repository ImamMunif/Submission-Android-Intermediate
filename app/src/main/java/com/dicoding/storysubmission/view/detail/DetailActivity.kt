package com.dicoding.storysubmission.view.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
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

        val storyId = if (Build.VERSION.SDK_INT >= 33) {
            intent.getStringExtra("storyId")
        } else {
            @Suppress("DEPRECATION")
            intent.getStringExtra("storyId")
        }

        if (storyId != null) {
            viewModel.getStoryById(storyId).observe(this) {
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

    private fun setDetailData(storyItem: Story) {
        Log.d("log: DetailActivity", "setDetailData: storyItem: $storyItem")
        Glide
            .with(this)
            .load(storyItem.photoUrl)
            .fitCenter()
            .into(binding.imgItemPhotoDetail)

        binding.tvItemNameDetail.text = storyItem.name
        binding.tvItemDescriptionDetail.text = storyItem.description
    }

    private fun showLoading(isLoading: Boolean) {
        binding.imgItemPhotoDetail.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}