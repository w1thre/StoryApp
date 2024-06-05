package com.codewithre.storyapp.view.createstory

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codewithre.storyapp.R
import com.codewithre.storyapp.databinding.ActivityCreateStoryBinding
import com.codewithre.storyapp.view.ViewModelFactory
import com.codewithre.storyapp.view.utils.getImageUri
import com.codewithre.storyapp.view.utils.reduceFileImage
import com.codewithre.storyapp.view.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding
    private val viewModel by viewModels<CreateStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this,
                    getString(R.string.permission_request_granted), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,
                    getString(R.string.permission_request_denied), Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.switchGpsLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getLocation()
            }
        }

        binding.apply {
            btnGallery.setOnClickListener { startGallery() }
            btnCamera.setOnClickListener { startCamera() }
            binding.buttonAdd.setOnClickListener {
                if (binding.switchGpsLocation.isChecked) {
                    getLocation()
                } else {
                    uploadStory(0.0, 0.0)
                }
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }


        viewModel.uploadResult.observe(this) { response ->
            if (response.error == true) {
                showToast(getString(R.string.upload_failed, response.message))
            } else {
                showToast(getString(R.string.upload_successful))
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                uploadStory(location.latitude, location.longitude)
            } else {
                showToast(getString(R.string.location_unavailable))
            }
        }.addOnFailureListener {
            showToast(getString(R.string.location_unavailable))
        }
    }

    private fun uploadStory(latitude: Double, longitude: Double) {
        if (currentImageUri == null || binding.edAddDescription.text.isNullOrEmpty()) {
            showToast(getString(R.string.upload_validation))
            return
        }

        currentImageUri?.let { uri ->
            val description = binding.edAddDescription.text.toString()
            val imageFile = uriToFile(uri, this).reduceFileImage()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            viewModel.uploadStory(multipartBody, requestBody, latitude, longitude)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            binding.ivPreview.setImageURI(currentImageUri)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){ uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            binding.ivPreview.setImageURI(currentImageUri)
        } else {
            Log.e("Image URI", "No image selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA

    }

}