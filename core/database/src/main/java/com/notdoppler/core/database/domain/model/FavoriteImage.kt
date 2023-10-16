package com.notdoppler.core.database.domain.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteImage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bitmap: Bitmap,
    val imageId: Int,
)
