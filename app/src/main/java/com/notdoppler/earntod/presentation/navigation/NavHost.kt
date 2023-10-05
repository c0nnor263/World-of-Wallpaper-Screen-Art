package com.notdoppler.earntod.presentation.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.notdoppler.core.domain.domain.model.FetchedImage
import com.notdoppler.core.navigation.Screen
import com.notdoppler.core.navigation.arg
import com.notdoppler.core.navigation.navigateTo
import com.notdoppler.core.navigation.popBack
import com.notdoppler.feature.home.presentation.HomeScreen
import com.notdoppler.feature.home.presentation.HomeScreenViewModel
import com.notdoppler.feature.picturedetails.PictureDetailsScreen
import com.notdoppler.feature.picturedetails.PictureDetailsViewModel
import com.notdoppler.feature.settings.SettingsScreen
import com.notdoppler.feature.settings.SettingsScreenViewModel
import com.notdoppler.feature.store.StoreScreen
import com.notdoppler.feature.store.StoreScreenViewModel

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
    composable(details.route, arguments = details.arguments) {
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


