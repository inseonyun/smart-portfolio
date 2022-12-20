package com.douzone.smart.portfolio.controller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri

class RotateImageController(val context: Context) {
    fun rotateImage(uri: Uri): Bitmap? {
        try {
            // 이미지 샘플 사이즈 설정
            val option = BitmapFactory.Options()
            option.inSampleSize = 4

            //이미지 로딩
            var inputStream = context.contentResolver.openInputStream(uri)

            // 이미지 회전
            val exif = ExifInterface(inputStream!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val matrix = Matrix()
            inputStream.close()

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270F)
            }
            inputStream = context.contentResolver.openInputStream(uri!!)
            val bit = BitmapFactory.decodeStream(inputStream, null, option)
            inputStream!!.close()

            bit.let {
                return Bitmap.createBitmap(it!!, 0, 0, it!!.width, it!!.height, matrix, false)
            }
        } catch (e: Exception) {

        }
        return null
    }
}

