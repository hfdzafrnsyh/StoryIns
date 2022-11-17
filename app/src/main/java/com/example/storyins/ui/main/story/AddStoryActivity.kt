package com.example.storyins.ui.main.story

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyins.R
import com.example.storyins.databinding.ActivityAddStoryBinding
import com.example.storyins.preference.UserPreference
import com.example.storyins.source.model.Wrapper
import com.example.storyins.source.model.local.entity.UserLocation
import com.example.storyins.source.model.remote.request.StoriesRequest
import com.example.storyins.ui.main.MainActivity
import com.example.storyins.utils.Helpers
import com.example.storyins.utils.Helpers.createCustomTempFile
import com.example.storyins.ui.factory.StoryViewModelFactory
import com.example.storyins.ui.main.auth.AuthActivity
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity(){

    private val android.content.Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = Helpers.uriToFIle(selectedImg, this@AddStoryActivity)
            getFile=myFile
            addStoryBinding.ivStory.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if (result.resultCode == RESULT_OK){
            val myFile = File(currentPhotoPath)
            getFile=myFile
            val resultImg = BitmapFactory.decodeFile(getFile?.path)
            addStoryBinding.ivStory.setImageBitmap(resultImg)
        }
    }

    private lateinit var addStoryBinding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath : String
    private lateinit var  addStoryViewModel : StoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userPreference : UserPreference
    private lateinit var storiesRequest: StoriesRequest
    private var userLocation:UserLocation?=null
    private var getFile : File?=null
    private var progressBar : Dialog?=null



    companion object {
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(addStoryBinding.root)


        userPreference = UserPreference.getInstance(this.dataStore)
        addStoryViewModel = ViewModelProvider(this , StoryViewModelFactory(this))[StoryViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        userLocation = runBlocking { userPreference.getLocation().first() }

        initView()
        viewLoading()

    }




    @SuppressLint("MissingPermission")
    private fun initView(){

        addStoryBinding.btnGallery.setOnClickListener {
            startGallery()
        }

        addStoryBinding.btnCamera.setOnClickListener{
            startCamera()
        }

        if(!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }


        addStoryBinding.btnUpload.setOnClickListener {
            uploadFile()
        }



    }


    private fun backHome(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, getString(R.string.not_accept_permission), Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }




    private fun allPermissionGranted() : Boolean {
        if(ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED){
                return true
        }
        return false
    }





    private fun startCamera() {
      val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

      intent.resolveActivity(packageManager)
      createCustomTempFile(application).also {
          val photoURI: Uri = FileProvider.getUriForFile(
              this@AddStoryActivity,
              "com.example.storyins",
              it
          )
          currentPhotoPath = it.absolutePath
          intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
          launcherIntentCamera.launch(intent)

      }
  }

   private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }


    @SuppressLint("InflateParams")
    private fun viewLoading(){
        progressBar = Dialog(this)
        val progressbarLayout = layoutInflater.inflate(R.layout.dialog_progressbar , null)

        progressBar?.let {
            it.setContentView(progressbarLayout)
            it.setCancelable(false)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }


    private fun showLoading() {
        progressBar?.show()
    }

    private fun dismissLoading() {
        progressBar?.dismiss()
    }



    private fun uploadFile(){

        val token = runBlocking { "Bearer ${userPreference.getToken().first()}" }

        if(getFile != null){

                val file = getFile as File
                val desc = addStoryBinding.edDescription.text
                val description = "$desc".toRequestBody("text/plain".toMediaType())
                 val lat = "${userLocation!!.lat}".toRequestBody("text/plain".toMediaType())
                val lon = "${userLocation!!.lon}".toRequestBody("text/plain".toMediaType())

                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imgMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                if(desc.isNullOrEmpty()){
                    addStoryBinding.edDescription.error = getString(R.string.error_description)
                    addStoryBinding.edDescription.requestFocus()
                }else{
                    if(!userLocation?.lat.isNullOrEmpty() && !userLocation?.lon.isNullOrEmpty()){
                        storiesRequest = StoriesRequest(
                            description,
                            lat,
                            lon
                        )

                        addStoryViewModel.addStories(imgMultipart,storiesRequest,token).observe(this ) {
                            when(it){
                                is Wrapper.Loading -> {
                                    showLoading()
                                }
                                is Wrapper.Success -> {
                                    dismissLoading()
                                   successAddStories(it.data.message)

                                }
                                is Wrapper.Error -> {
                                    dismissLoading()
                                    showDialogError(it.msg)
                                }
                                else -> dismissLoading()
                            }
                        }
                    }else{
                        storiesRequest = StoriesRequest(
                            description,
                            null,
                            null
                        )

                        addStoryViewModel.addStories(imgMultipart,storiesRequest,token).observe(this ) {
                            when(it){
                                is Wrapper.Loading -> {
                                    showLoading()
                                }
                                is Wrapper.Success -> {
                                    dismissLoading()
                                    successAddStories(it.data.message)

                                }
                                is Wrapper.Error -> {
                                    dismissLoading()
                                    showDialogError(it.msg)
                                }
                                else -> dismissLoading()
                            }
                        }
                    }
                }


        }else{
            Toast.makeText(this, getString(R.string.error_add_photo) , Toast.LENGTH_SHORT).show()
        }

    }

    private fun successAddStories(message : String?){
        AlertDialog.Builder(this).apply {
            setMessage(message)
            setTitle(getString(R.string.success))
            setPositiveButton(getString(R.string.yes)
            ) { _, _ ->
                backHome()
            }
            create()
            show()
        }


    }


    private fun showDialogError(message : String?){
        AlertDialog.Builder(this).apply {
            setMessage(message)
            setTitle(R.string.error_title)
            setNegativeButton(getString(R.string.cancel)
            ) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            create()
            show()
        }
    }
}