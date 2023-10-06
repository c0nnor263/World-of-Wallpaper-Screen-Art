package com.notdoppler.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument


sealed class Screen(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {
    data object Home : Screen(route = "home")
    data class Details(
        val selectedImageIndex: String = "{selectedImageIndex}", val tabOrder: String = "{tabOrder}"
    ) : Screen(route = "details?selectedImageIndex=$selectedImageIndex&tabOrder=$tabOrder",
        arguments = listOf(navArgument(
            name = selectedImageIndex.arg()
        ) {
            type = NavType.IntType
//                    type = ImageHitType()
        }, navArgument(
            name = tabOrder.arg()
        ) {
            type = NavType.StringType
        }))
}
