package com.notdoppler.earntod.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.notdoppler.core.domain.enums.PagingKey
import com.notdoppler.core.domain.getSafeParcelable
import com.notdoppler.core.domain.model.navigation.PictureDetailsNavArgs
import com.notdoppler.core.domain.model.navigation.SearchNavArgs
import com.notdoppler.core.ui.tweenMedium
import com.notdoppler.earntod.navigation.Screen
import com.notdoppler.earntod.navigation.arg
import com.notdoppler.feature.favorites.presentation.FavoritesScreen
import com.notdoppler.feature.favorites.presentation.FavoritesScreenViewModel
import com.notdoppler.feature.home.presentation.HomeScreen
import com.notdoppler.feature.home.presentation.HomeScreenViewModel
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsScreen
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsViewModel
import com.notdoppler.feature.search.presentation.SearchScreen
import com.notdoppler.feature.search.presentation.SearchScreenViewModel
import com.notdoppler.feature.splash.presentation.SplashScreen
import com.notdoppler.feature.splash.presentation.SplashScreenViewModel

fun NavGraphBuilder.splashScreen(
    onNavigateToHome: () -> Unit
) {
    val splash = Screen.Splash

    composable(splash.route, arguments = splash.arguments) {
        val viewModel: SplashScreenViewModel = hiltViewModel()
        SplashScreen(viewModel, onNavigateToHome)
    }
}

fun NavGraphBuilder.searchScreen(
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onNavigateBack: () -> Unit
) {
    val search = Screen.Search()

    composable(search.route, arguments = search.arguments) {
        val defaultValue = SearchNavArgs(query = "", pagingKey = PagingKey.POPULAR)
        val args = it.arguments?.getSafeParcelable(search.args.arg())
            ?: defaultValue

        val viewModel: SearchScreenViewModel = hiltViewModel()
        SearchScreen(
            viewModel = viewModel,
            navArgs = args,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavGraphBuilder.homeScreen(
    onNavigateToFavorites: () -> Unit,
    onNavigateToSearch: (SearchNavArgs?) -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit
) {
    val home = Screen.Home
    composable(home.route, arguments = home.arguments) {
        val viewModel: HomeScreenViewModel = hiltViewModel()
        HomeScreen(
            viewModel = viewModel,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateToFavorites = onNavigateToFavorites
        )
    }
}

fun NavGraphBuilder.detailsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSearch: (SearchNavArgs?) -> Unit
) {
    val details = Screen.Details()
    composable(
        details.route,
        arguments = details.arguments,
        enterTransition = {
            fadeIn(animationSpec = tweenMedium()) +
                scaleIn(animationSpec = tweenMedium()) +
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tweenMedium()
                )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                tweenMedium()
            ) +
                scaleOut(animationSpec = tweenMedium()) +
                fadeOut(animationSpec = tweenMedium())
        }
    ) {
        val defaultValue = PictureDetailsNavArgs(
            selectedImageIndex = 1,
            pagingKey = PagingKey.LATEST
        )
        val pictureDetailsNavArgs =
            it.arguments?.getSafeParcelable(details.args.arg())
                ?: defaultValue

        val pictureDetailsViewModel: PictureDetailsViewModel = hiltViewModel()
        PictureDetailsScreen(
            viewModel = pictureDetailsViewModel,
            navArgs = pictureDetailsNavArgs,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavGraphBuilder.favoritesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit
) {
    val favorites = Screen.Favorites
    composable(
        favorites.route,
        arguments = favorites.arguments
    ) {
        val viewModel: FavoritesScreenViewModel = hiltViewModel()
        FavoritesScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateToHome = onNavigateToHome
        )
    }
}
