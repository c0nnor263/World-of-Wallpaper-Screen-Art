package com.notdoppler.earntod.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.notdoppler.earntod.navigation.Screen
import com.notdoppler.earntod.navigation.navigateTo
import com.notdoppler.earntod.navigation.popBack

@Composable
fun AppHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        splashScreen(
            onNavigateToHome = {
                navController.navigateTo(Screen.Home) {
                    popUpTo(Screen.Home.route) {
                        inclusive = true
                    }
                }
            }
        )

        detailsScreen(
            onNavigateToSearch = { searchNavArgs ->
                val screen = Screen.Search(args = searchNavArgs.toString())
                navController.navigateTo(screen)
            },
            onNavigateBack = {
                navController.popBack()
            }
        )

        homeScreen(
            onNavigateToFavorites = {
                navController.navigateTo(Screen.Favorites)
            },
            onNavigateToSearch = { searchNavArgs ->
                val screen = Screen.Search(args = searchNavArgs.toString())
                navController.navigateTo(screen)
            },
            onNavigateToDetails = { pictureDetailsNavArgs ->
                val screen = Screen.Details(args = pictureDetailsNavArgs.toString())
                navController.navigateTo(screen)
            }
        )

        searchScreen(
            onNavigateToDetails = { pictureDetailsNavArgs ->
                val screen = Screen.Details(args = pictureDetailsNavArgs.toString())
                navController.navigateTo(screen)
            },
            onNavigateBack = {
                navController.popBack()
            }
        )

        favoritesScreen(
            onNavigateBack = {
                navController.popBack()
            },
            onNavigateToHome = {
                navController.navigateTo(Screen.Home) {
                    popUpTo(Screen.Home.route) {
                        inclusive = true
                    }
                }
            },
            onNavigateToDetails = { pictureDetailsNavArgs ->
                val screen = Screen.Details(args = pictureDetailsNavArgs.toString())
                navController.navigateTo(screen)
            }
        )
    }
}






