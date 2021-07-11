package com.example.finalprojectacad.other.utilities

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

private const val TAG = "SaveImgToScopedStorage"

object SaveImgToScopedStorage {

//    fun save(appContext: Context, img: ImageCarRoom) {
//
//        try {
//            val bmp =  MediaStore.Images.Media.getBitmap(appContext.contentResolver, img.imgCar.toUri())
//
//            appContext.openFileOutput("${img.id}.jpg", AppCompatActivity.MODE_PRIVATE).use { stream ->
//                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//            }
//        } catch (e: IOException){
//            e.printStackTrace()
//        }
//    }


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

    fun saveFromBitmap(appContext: Context, id: Int, bmp: Bitmap): Boolean {

//        Log.d(TAG, "saveFromBitmap: HERE")
//        val fileOutput = appContext.openFileOutput("Route_$id.jpg", AppCompatActivity.MODE_PRIVATE)
//         fileOutput.use { stream ->
//             val a = bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//             Log.d(TAG, "saveFromBitmap: $a")
//             return true
//         }
//
//        return false

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
}