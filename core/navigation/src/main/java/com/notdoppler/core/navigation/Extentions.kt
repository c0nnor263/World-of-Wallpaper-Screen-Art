package com.notdoppler.core.navigation

import androidx.navigation.NavHostController


var lastTimeClicked: Long = 0L
fun NavHostController.navigateTo(screen: Screen) {
    val route = screen.route
    if (currentDestination?.route != route) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTimeClicked < 500) return
        lastTimeClicked = currentTime
        navigate(route)
    }
}

fun NavHostController.popBack() {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastTimeClicked < 500) return
    lastTimeClicked = currentTime
    popBackStack()
}

fun String.arg():String{
    return this.removeSurrounding("{","}")
}