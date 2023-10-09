package com.notdoppler.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument
import com.notdoppler.core.navigation.type.PictureDetailsNavArgsType


sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object Home : Screen(route = "home")
    data class Details(
        val args: String = "{args}"
    ) : Screen(
        route = "details?args=$args",
        arguments = listOf(
            navArgument(
                name = args.arg()
            ) {
                type = PictureDetailsNavArgsType()
            }
        )
    )
}
