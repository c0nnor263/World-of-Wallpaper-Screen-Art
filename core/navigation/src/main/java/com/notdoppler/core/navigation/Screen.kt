package com.notdoppler.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument
import com.notdoppler.core.navigation.type.PictureDetailsNavArgsType
import com.notdoppler.core.navigation.type.SearchNavArgsType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


sealed class Screen(
    val route: String,
    val arguments: ImmutableList<NamedNavArgument> = persistentListOf(),
) {
    data object Home : Screen(route = "home")
    data class Search(val args: String = "{args}") :
        Screen(
            route = "search?args=$args",
            arguments = persistentListOf(
                navArgument(
                    name = args.arg()
                ) {
                    type = SearchNavArgsType()
                    nullable = true
                    defaultValue = null
                }
            )
        )

    data class Details(
        val args: String = "{args}",
    ) : Screen(
        route = "details?args=$args",
        arguments = persistentListOf(
            navArgument(
                name = args.arg()
            ) {
                type = PictureDetailsNavArgsType()
            }
        )
    )
}
