package com.doodle.core.database.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Base64.DEFAULT
import androidx.room.TypeConverter
import java.nio.ByteBuffer

class FavoriteDatabaseConverters {
    @TypeConverter
    fun bitmapToBase64(originalBitmap: Bitmap): String {
        val byteBuffer = ByteBuffer.allocate(originalBitmap.height * originalBitmap.rowBytes)
        val newBitmap = originalBitmap.copy(Bitmap.Config.RGB_565, false)
        newBitmap.copyPixelsToBuffer(byteBuffer)
        val byteArray = byteBuffer.array()
        return Base64.encodeToString(byteArray, DEFAULT)
    }

    @TypeConverter
    fun base64ToBitmap(base64String: String): Bitmap {
        val byteArray = Base64.decode(base64String, DEFAULT)
        return BitmapFactory.decodeByteArray(
            byteArray,
            0,
            byteArray.size
        )
    }
}
