package com.notdoppler.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.notdoppler.core.navigation.type.PictureDetailsNavArgsType


sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList(),
) {
    data object Home : Screen(route = "home")
    data class Search(val query: String = "{query}") :
        Screen(
            route = "search?query=$query",
            arguments = listOf(
                navArgument(
                    name = query.arg()
                ) {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
        )

    data class Details(
        val args: String = "{args}",
    ) : Screen(
        route = "details?args=$args",
        arguments = listOf(
            navArgument(
                name = args.arg()
            ) {
                type = PictureDetailsNavArgsType()
                nullable = true
            }
        )
    )
}
