package com.dicoding.storysubmission.view.upload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.storysubmission.R

class UploadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        supportActionBar?.title = "Upload"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}