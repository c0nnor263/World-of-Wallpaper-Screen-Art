package com.notdoppler.feature.picturedetails.state

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.notdoppler.feature.picturedetails.domain.model.PublisherInfoData

//private val PublisherInfoStateSaver = run {
//    val isVisibleKey = "isVisible"
//    val dataKey = "data"
//
//    mapSaver(
//        save = { mapOf(isVisibleKey to it.isVisible, dataKey to it.info) },
//        restore = {
//            PublisherInfoState().apply {
//                isVisible = it[isVisibleKey] as Boolean
//                info = it[dataKey] as PublisherInfoData?
//            }
//        }
//    )
//}


@Composable
fun rememberPublisherInfoState(): PublisherInfoState {
    return remember {
        PublisherInfoState()
    }
}

class PublisherInfoState {
    var isVisible: Boolean by mutableStateOf(false)
    var info: PublisherInfoData? by mutableStateOf(null)


    fun setPublisherData(data: PublisherInfoData) {
        info = data.also {
            Log.i("TAG", "setPublisherData: $it")
        }
    }

    fun show() {
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }
}