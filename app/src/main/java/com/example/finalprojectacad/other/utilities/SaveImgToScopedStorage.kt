package com.example.finalprojectacad.other.utilities

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.finalprojectacad.data.localDB.entity.ImageCarRoom
import java.io.IOException

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
            val bmp =  MediaStore.Images.Media.getBitmap(appContext.contentResolver, uriImg)

            appContext.openFileOutput("$id.jpg", AppCompatActivity.MODE_PRIVATE).use { stream ->
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
            true
        } catch (e: IOException){
            e.printStackTrace()
            false
        }
    }
}