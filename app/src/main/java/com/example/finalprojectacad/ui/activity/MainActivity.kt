package com.example.finalprojectacad.ui.activity

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
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.URI
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: CarViewModel by viewModels()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.apply {
           bottomNavigationBar.setupWithNavController(navController)
        }

    }

    suspend fun openSavedImg(): List<Img> {
        return withContext(Dispatchers.IO) {
            val files = filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                Img(it.absolutePath.toUri())
            } ?: listOf()
        }
    }


    fun saveImgCarToInternalStorage(filenameImgId: String, imgUri: Uri): Boolean{
        return try {
            val bmp =  MediaStore.Images.Media.getBitmap(this.contentResolver, imgUri) //deprecated

            openFileOutput("$filenameImgId.jpg", MODE_PRIVATE).use { stream ->
                bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)
            }

            true
        } catch (e: IOException){
            e.printStackTrace()
            false
        }
    }

    data class Img(
        val uri: Uri
    )

}
