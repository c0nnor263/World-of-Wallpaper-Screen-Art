package com.notdoppler.earntod.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.notdoppler.core.domain.model.FetchedImage
import com.notdoppler.core.navigation.Screen
import com.notdoppler.core.navigation.arg
import com.notdoppler.core.navigation.navigateTo
import com.notdoppler.core.navigation.popBack
import com.notdoppler.feature.home.presentation.HomeScreen
import com.notdoppler.feature.home.presentation.HomeScreenViewModel
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsScreen
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NavHost(modifier: Modifier = Modifier, navController: NavHostController) {

    NavHost(
        modifier = modifier.fillMaxWidth(),
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        homeScreen(navController)

        detailsScreen(navController)
    }
}


fun NavGraphBuilder.detailsScreen(navController: NavHostController) {
    val details = Screen.Details()
    composable(details.route, arguments = details.arguments, enterTransition = {
        fadeIn(
            animationSpec = tween(500)
        ) + scaleIn(
            animationSpec = tween(500),
        ) + slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Up, tween(500)
        )
    }, exitTransition = {
        scaleOut(
            animationSpec = tween(500),
        ) + slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Up, tween(500)
        ) + fadeOut(
            animationSpec = tween(500)
        )
    }) {
        val imageHit = it.arguments?.getParcelable<FetchedImage.Hit>(details.imageHit.arg())
        val viewModel: PictureDetailsViewModel = hiltViewModel()
        PictureDetailsScreen(viewModel = viewModel, imageHit = imageHit, onNavigateBack = {
            navController.popBack()
        })
    }
}

fun NavGraphBuilder.homeScreen(navController: NavHostController) {
    val home = Screen.Home
    composable(home.route, arguments = home.arguments) {
        val viewModel: HomeScreenViewModel = hiltViewModel()
        HomeScreen(viewModel = viewModel, onNavigateToDetails = { imageHit ->
            navController.navigateTo(Screen.Details(imageHit.toString()))
        })
    }
}


