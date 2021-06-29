package com.example.finalprojectacad.ui.activity

import android.Manifest
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.fondesa.kpermissions.request.PermissionRequest
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.finalprojectacad.R
import com.example.finalprojectacad.databinding.ActivityMainBinding
import com.example.finalprojectacad.db.entity.ImageCarRoom
import com.example.finalprojectacad.viewModel.CarViewModel
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.URI
import kotlin.random.Random

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PermissionRequest.Listener {

    private val viewModel: CarViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    private val request by lazy { // request for add permission from user
        permissionsBuilder(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



//        request.send()//right now don't need



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.apply {
           bottomNavigationBar.setupWithNavController(navController)
        }

    }


    override fun onPermissionsResult(result: List<PermissionStatus>) {
        when {
            result.anyPermanentlyDenied() -> Log.d(TAG, "onPermissionsResult: Denied")
            result.anyShouldShowRationale() -> Log.d(TAG, "onPermissionsResult: SHOW SWTH")
            result.allGranted() -> Log.d(TAG, "onPermissionsResult: all Allow")
        }
    }

    suspend fun openSavedImg(): List<Uri> {
        return withContext(Dispatchers.IO) {
            val listUri = mutableListOf<Uri>()
            val files = filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                listUri.add(it.absolutePath.toUri())
            }
            return@withContext listUri
        }
    }


    fun saveImgCarToInternalStorage(filenameImgId: String, imgUri: Uri): Boolean{
        return try {
            val bmp =  MediaStore.Images.Media.getBitmap(this.contentResolver, imgUri) //deprecated

            openFileOutput("$filenameImgId.jpg", MODE_PRIVATE).use { stream ->
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }

            true
        } catch (e: IOException){
            e.printStackTrace()
            false
        }
    }


}
