package com.beta.finalprojectacad.other.utilities

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.beta.finalprojectacad.data.localDB.entity.ImageCarRoom
import java.io.IOException

private const val TAG = "SaveImgToScopedStorage"

object SaveImgToScopedStorage {

    fun copyToScopeStorageImg(
        choseImgUri: Uri?,
        carListSize: Int,
        appContext: Context
    ): ImageCarRoom? {
        var flagSuccessSave: Boolean
        choseImgUri?.let {
            val currentIdCars = carListSize + 1
            flagSuccessSave = save(appContext, currentIdCars, it)
            if (flagSuccessSave) {
                val listScopeStorageImg = openSavedImg(appContext)
                val lastSavedImg = listScopeStorageImg.last() // mb need find by name file

                return ImageCarRoom(
                    lastSavedImg.toString(),
                    System.currentTimeMillis(),
                    currentIdCars
                )
            }
        }
        return null
    }

    fun save(appContext: Context, id: Int, uriImg: Uri): Boolean {

        return try {
            val bmp = MediaStore.Images.Media.getBitmap(appContext.contentResolver, uriImg)

            appContext.openFileOutput("$id.jpg", AppCompatActivity.MODE_PRIVATE).use { stream ->
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun saveRoute(appContext: Context, id: Int, uriImg: Uri): Boolean {

        return try {
            val bmp = MediaStore.Images.Media.getBitmap(appContext.contentResolver, uriImg)
            Log.d(TAG, "saveRoute: img save")
            appContext.openFileOutput("Route_$id.jpg", AppCompatActivity.MODE_PRIVATE)
                .use { stream ->
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun saveFromBitmap(appContext: Context, id: Int, bmp: Bitmap): Boolean {
        return try {
            Log.d(TAG, "saveFromBitmap: HERE")
            appContext.openFileOutput("Route_$id.jpg", AppCompatActivity.MODE_PRIVATE)
                .use { stream ->
                    val a = bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    Log.d(TAG, "saveFromBitmap: $a")
                }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }


    fun openSavedImg(appContext: Context): List<Uri> {
        val listUri = mutableListOf<Uri>()
        val files = appContext.filesDir.listFiles()
        files?.filter { it.canRead() && it.isFile }?.map {
            val path = it.absolutePath
            val realAbsolutePath = "file:$path"
            listUri.add(realAbsolutePath.toUri())
        }
        return listUri
    }
}