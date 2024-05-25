package com.dicoding.storysubmission.view.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.dicoding.storysubmission.R
import com.dicoding.storysubmission.data.Result
import com.dicoding.storysubmission.databinding.ActivityUploadBinding
import com.dicoding.storysubmission.view.ViewModelFactory
import com.dicoding.storysubmission.view.main.MainActivity

class UploadActivity : AppCompatActivity() {

    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityUploadBinding

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Upload"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.layoutButton.galleryButton.setOnClickListener { startGallery() }
        binding.layoutButton.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
        Log.d("Debug: onCreate", "onCreate started")
        Log.d("Debug: onCreate", "currentImageURI: $currentImageUri")
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        Log.d("Log: UploadActivity", "startGallery: function startGallery triggered")
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            Log.d("Log: UploadActivity", "launcherGallery: currentImageUri: $uri")
            showImage()
        } else {
            Log.d("Log: UploadActivity", "launcherGallery: No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            Log.d("Log: UploadActivity", "launcherCamera: currentImageURI: $currentImageUri")
            showImage()
        } else {
            Log.d("Log: UploadActivity", "launcherCamera: No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
            Log.d("Log: UploadActivity", "showImage: Uri selected: $it")
        }
    }

    private fun uploadImage() {
        val description = binding.descriptionEditText.text.toString().trim()

        if (description.isEmpty()) {
            binding.descriptionEditTextLayout.error = getString(R.string.empty_description_warning)
            return
        } else {
            binding.descriptionEditTextLayout.error = null
        }

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Debug: uploadImage", "imageFile: $imageFile")

            viewModel.getSession().observe(this) { user ->
                val token = user.token
                viewModel.uploadImage(token, imageFile, description).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                                Log.d("Debug: uploadImage", "uploadImage: uploading...")
                            }

                            is Result.Success -> {
                                showToast(result.data.message)
                                showLoading(false)
                                startActivity(Intent(this, MainActivity::class.java))
                                Log.d("Debug: uploadImage", "uploadImage: finish...")
                            }

                            is Result.Error -> {
                                showToast(result.error)
                                showLoading(false)
                                Log.d("Debug: uploadImage", "uploadImage: error...!!")
                            }
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}