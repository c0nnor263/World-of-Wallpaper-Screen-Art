package com.notdoppler.feature.picturedetails.domain.enums

enum class DragAnchors(val fraction: Float) {
    TopEnd(-0.2f),
    Start(0f),


    // TODO: Under the question mark, poor user experience
    BottomEnd(0.2f)
}