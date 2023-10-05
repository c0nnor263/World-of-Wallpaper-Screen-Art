package com.notdoppler.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument
import com.notdoppler.core.navigation.type.ImageHitType


sealed class Screen(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {
    data object Home : Screen(route = "home")
    data object Store : Screen(route = "store")
    data object Settings : Screen(route = "settings")
    data class Details(val imageHit: String = "{imageHit}") :
        Screen(
            route = "details?imageHit=$imageHit", arguments = listOf(
                navArgument(
                    name = imageHit.arg()
                ) {
                    type = ImageHitType()
                }
            )
        )
}
