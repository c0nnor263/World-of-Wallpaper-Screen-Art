package com.notdoppler.earntod.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import com.notdoppler.feature.home.presentation.HomeScreen
import com.notdoppler.feature.home.presentation.HomeScreenViewModel
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsScreen
import com.notdoppler.feature.picturedetails.presentation.PictureDetailsViewModel
import com.notdoppler.feature.search.presentation.SearchScreen
import com.notdoppler.feature.search.presentation.SearchScreenViewModel

@Composable
fun NavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        detailsScreen(onNavigateToSearch = { query ->
            navController.navigateTo(
                Screen.Search(
                    query = query.toString()
                )
            )
        },
            onNavigateBack = { navController.popBackStack() })

        homeScreen(
            onNavigateToSearch = { query ->
                navController.navigateTo(
                    Screen.Search(
                        query = query.toString()
                    )
                )
            },
            onNavigateToDetails = { args ->
                navController.navigateTo(
                    Screen.Details(
                        args = args.toString()
                    )
                )
            })
        searchScreen(
            onNavigateToDetails = { args ->
                navController.navigateTo(
                    Screen.Details(
                        args = args.toString()
                    )
                )
            }, onNavigateBack = {
                navController.popBack()
            }
        )
    }
}

fun NavGraphBuilder.searchScreen(
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val search = Screen.Search()

    composable(search.route, arguments = search.arguments) {
        val queryArg = it.arguments?.getString(search.query.arg())

        val viewModel: SearchScreenViewModel = hiltViewModel()
        SearchScreen(
            viewModel = viewModel,
            query = queryArg,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavGraphBuilder.homeScreen(
    onNavigateToSearch: (String?) -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit,
) {
    val home = Screen.Home
    composable(home.route, arguments = home.arguments) {
        val viewModel = hiltViewModel<HomeScreenViewModel>()
        HomeScreen(
            viewModel = viewModel,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToDetails = onNavigateToDetails
        )
    }
}

fun NavGraphBuilder.detailsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSearch: (String?) -> Unit,
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
            viewModel = pictureDetailsViewModel,
            navArgs = pictureDetailsNavArgs,
            onNavigateToSearch = onNavigateToSearch,
            onNavigateBack = onNavigateBack
        )
    }
}



