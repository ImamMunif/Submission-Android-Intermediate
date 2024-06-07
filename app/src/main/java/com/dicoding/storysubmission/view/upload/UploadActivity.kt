package com.dicoding.storysubmission.view.upload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.storysubmission.R
import com.dicoding.storysubmission.data.Result
import com.dicoding.storysubmission.databinding.ActivityUploadBinding
import com.dicoding.storysubmission.view.ViewModelFactory
import com.dicoding.storysubmission.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class UploadActivity : AppCompatActivity() {

    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityUploadBinding

    private var currentImageUri: Uri? = null

    private var location: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    addLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    addLocation()
                }

                else -> {
                    binding.layoutLocation.switchLocation.isChecked = false
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_upload)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.layoutButton.galleryButton.setOnClickListener { startGallery() }
        binding.layoutButton.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadStory() }
        binding.layoutLocation.switchLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                if (!isGPSEnabled()) {
                    showEnableGPSDialog()
                }
                lifecycleScope.launch {
                    addLocation()
                }
            } else {
                location = null
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
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
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadStory() {
        val description = binding.descriptionEditText.text.toString().trim()

        if (description.isEmpty()) {
            binding.descriptionEditTextLayout.error = getString(R.string.empty_description_warning)
            return
        } else {
            binding.descriptionEditTextLayout.error = null
        }

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()

            viewModel.getSession().observe(this) { user ->
                val token = user.token
                viewModel.uploadStory(token, imageFile, description, location)
                    .observe(this) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                }

                                is Result.Success -> {
                                    showToast(result.data.message)
                                    showLoading(false)
                                    startActivity(Intent(this, MainActivity::class.java))
                                }

                                is Result.Error -> {
                                    showToast(result.error)
                                    showLoading(false)
                                }
                            }
                        }
                    }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun addLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    location = loc
                } else {
                    Toast.makeText(
                        this,
                        R.string.error_no_location,
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.layoutLocation.switchLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showEnableGPSDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.gps_dialog_tittle))
            setMessage(getString(R.string.gps_dialog_message))
            setPositiveButton(getString(R.string.next)) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            create()
            show()
        }
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
}