package com.notdoppler.earntod.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.notdoppler.core.domain.getSafeParcelable
import com.notdoppler.core.domain.model.PictureDetailsNavArgs
import com.notdoppler.core.domain.presentation.TabOrder
import com.notdoppler.core.navigation.Screen
import com.notdoppler.core.navigation.arg
import com.notdoppler.core.navigation.navigateTo
import com.notdoppler.core.navigation.popBack
import com.notdoppler.core.ui.HomeScreenViewModel
import com.notdoppler.feature.home.presentation.HomeScreen
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsScreen
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsViewModel

@Composable
fun NavHost(modifier: Modifier = Modifier, navController: NavHostController) {


    val homeViewModel = hiltViewModel<HomeScreenViewModel>()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        homeScreen(navController, homeViewModel)
        detailsScreen(navController, homeViewModel)
    }
}


fun NavGraphBuilder.homeScreen(navController: NavHostController, viewModel: HomeScreenViewModel) {
    val home = Screen.Home
    composable(home.route, arguments = home.arguments) {
        HomeScreen(
            viewModel = viewModel,
            onNavigateToDetails = { args ->
                navController.navigateTo(
                    Screen.Details(
                        args = args.toString()
                    )
                )
            }
        )
    }
}

fun NavGraphBuilder.detailsScreen(
    navController: NavHostController, viewModel: HomeScreenViewModel
) {
    val details = Screen.Details()
    composable(
        details.route,
        arguments = details.arguments,
        enterTransition = {
            fadeIn(
                animationSpec = tween(500)
            ) + scaleIn(
                animationSpec = tween(500),
            ) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up, tween(500)
            )
        },
        exitTransition = {
            scaleOut(
                animationSpec = tween(500),
            ) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Up, tween(500)
            ) + fadeOut(
                animationSpec = tween(500)
            )
        }
    ) {
        val pictureDetailsNavArgs =
            it.arguments?.getSafeParcelable(details.args.arg())
                ?: PictureDetailsNavArgs(
                    selectedImageIndex = 1,
                    tabOrder = TabOrder.LATEST
                )

        val pictureDetailsViewModel: PictureDetailsViewModel = hiltViewModel()
        PictureDetailsScreen(
            homeSharedViewModel = viewModel,
            pictureDetailsViewModel = pictureDetailsViewModel,
            navArgs = pictureDetailsNavArgs,
            onNavigateBack = {
                navController.popBack()
            }
        )
    }
}



