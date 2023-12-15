package com.example.yourjourney.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.paging.ExperimentalPagingApi
import com.example.yourjourney.R
import com.example.yourjourney.databinding.ActivityAddJourneyBinding
import com.example.yourjourney.extention.gone
import com.example.yourjourney.extention.reduceFileImage
import com.example.yourjourney.extention.show
import com.example.yourjourney.extention.showOKDialog
import com.example.yourjourney.extention.showToast
import com.example.yourjourney.extention.uriToFile
import com.example.yourjourney.manager.SessionHandler
import com.example.yourjourney.model.JourneyViewModel
import com.example.yourjourney.response.ResponseAPI
import com.example.yourjourney.setcamera.CameraActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


@ExperimentalPagingApi
@AndroidEntryPoint
class AddJourneyActivity : AppCompatActivity() {

    private val JourneyViewModel: JourneyViewModel by viewModels()

    private var _activityAddStoryBinding: ActivityAddJourneyBinding? = null
    private val binding get() = _activityAddStoryBinding!!

    private var uploadFile: File? = null
    private var token: String? = null

    private var currentLocation: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var pref: SessionHandler

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, AddJourneyActivity::class.java)
            context.startActivity(intent)
        }

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityAddStoryBinding = ActivityAddJourneyBinding.inflate(layoutInflater)
        setContentView(_activityAddStoryBinding?.root)

        pref = SessionHandler(this)
        token = pref.getToken
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                10
            )
        }

        initUI()
        initAction()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    Snackbar
                        .make(
                            binding.root,
                            "location not found, try again",
                            Snackbar.LENGTH_SHORT
                        )
                        .setActionTextColor(ContextCompat.getColor(this, R.color.white))
                        .setAction("Change setting") {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .show()

                    binding.cbShareLocation.isChecked = false
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    showToast("Location ${currentLocation!!.longitude} ${currentLocation!!.latitude}")
                } else {
                    showToast("location not found, try again")

                    binding.cbShareLocation.isChecked = false
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!allPermissionsGranted()) {
            showToast("Not Permitted")
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun initAction() {
        binding.btnOpenCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            launchIntentCamera.launch(intent)
        }
        binding.btnOpenGallery.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "choose a picture")
            launchIntentGallery.launch(chooser)
        }
        binding.btnUpload.setOnClickListener {
            uploadImage()
        }
        binding.cbShareLocation.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                getMyLastLocation()
            } else {
                currentLocation = null
            }
        }
    }

    private fun initUI() {
        title = "New Journey"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == 200) {
            val file = it?.data?.getSerializableExtra("Picture") as File

            uploadFile = file

            val result = BitmapFactory.decodeFile(file.path)

            binding.imgPreview.setImageBitmap(result)
        }
    }

    private val launchIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val file = uriToFile(selectedImg, this)

            uploadFile = file
            binding.imgPreview.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        if (uploadFile != null) {
            val file = reduceFileImage(uploadFile as File)
            val description = binding.edtStoryDesc.text
            if (description.isBlank()) {
                binding.edtStoryDesc.requestFocus()
                binding.edtStoryDesc.error = "description can't empty"
            } else {
                val descMediaTyped = description.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                var latitude: RequestBody? = null
                var longitude: RequestBody? = null

                if (currentLocation != null) {
                    longitude = currentLocation?.longitude.toString().toRequestBody("text/plain".toMediaType())
                    latitude = currentLocation?.latitude.toString().toRequestBody("text/plain".toMediaType())
                }

                JourneyViewModel.addNewJourney("Bearer $token", imageMultipart, descMediaTyped, latitude, longitude).observe(this) { response ->
                    when (response) {
                        is ResponseAPI.Loading -> {
                            showLoading(true)
                        }
                        is ResponseAPI.Success -> {
                            showLoading(false)
                            showToast("Upload Success")
                            finish()
                        }
                        is ResponseAPI.Error -> {
                            showLoading(false)
                            showOKDialog("Upload Info", response.errorMessage)
                        }
                        else -> {
                            showLoading(false)
                            showToast("Unknown Condition")
                        }
                    }
                }
            }
        } else {
            showOKDialog("Information", "Pick an Image")
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.show() else binding.progressBar.gone()
        if (isLoading) binding.backgroundLoading.show() else binding.backgroundLoading.gone()
        binding.apply {
            btnUpload.isClickable = !isLoading
            btnUpload.isEnabled = !isLoading
            btnOpenGallery.isClickable = !isLoading
            btnOpenGallery.isEnabled = !isLoading
            btnOpenCamera.isClickable = !isLoading
            btnOpenCamera.isEnabled = !isLoading
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}