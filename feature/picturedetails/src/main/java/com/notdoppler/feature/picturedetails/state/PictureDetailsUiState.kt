package com.notdoppler.feature.picturedetails.state

import androidx.compose.runtime.compositionLocalOf
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsViewModel

val LocalPictureDetailsUiState = compositionLocalOf<PictureDetailsViewModel.UiState?> {
    null
}
