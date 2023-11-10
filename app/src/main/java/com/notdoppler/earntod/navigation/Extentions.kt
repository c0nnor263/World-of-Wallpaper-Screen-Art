package com.notdoppler.earntod.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder


var lastTimeClicked: Long = 0L
fun NavHostController.navigateTo(screen: Screen, builder: NavOptionsBuilder.() -> Unit = {}) {
    val route = screen.route
    if (currentDestination?.route != route) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTimeClicked < 500) return
        lastTimeClicked = currentTime
        navigate(route, builder)
    }
}

fun NavHostController.popBack() {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastTimeClicked < 500) return
    lastTimeClicked = currentTime
    popBackStack()
}

fun String.arg(): String {
    return this.removeSurrounding("{", "}")
}