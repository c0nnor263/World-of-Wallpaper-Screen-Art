package com.doodle.feature.favorites.presentation

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.doodle.core.domain.enums.PagingKey
import com.doodle.core.domain.model.navigation.PictureDetailsNavArgs
import com.doodle.core.domain.model.remote.RemoteImage
import com.doodle.core.ui.ApplicationScaffold
import com.doodle.core.ui.NavigationIcon
import com.doodle.core.ui.card.CardButton
import com.doodle.core.ui.card.CardImage
import com.doodle.core.ui.list.CardImageList
import com.doodle.feature.favorites.R

@Composable
fun FavoritesScreen(
    viewModel: FavoritesScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit
) {
    val favoriteImages =
        viewModel.favoriteImages.collectAsLazyPagingItems()

    ApplicationScaffold(
        title = stringResource(id = R.string.favorites),
        navigationIcon = {
            NavigationIcon(
                imageVector = Icons.Default.ArrowLeft,
                onClick = onNavigateBack
            )
        }
    ) { innerPadding ->
        FavoritesScreenContent(
            modifier = Modifier.padding(innerPadding),
            favoriteImages = favoriteImages,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateToHome = onNavigateToHome
        )
    }
}

@Composable
fun FavoritesScreenContent(
    modifier: Modifier = Modifier,
    favoriteImages: LazyPagingItems<RemoteImage.Hit>,
    onNavigateToHome: () -> Unit,
    onNavigateToDetails: (PictureDetailsNavArgs) -> Unit
) {
    CardImageList(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(3),
        isItemsEmpty = favoriteImages.itemCount == 0,
        onEmptyContent = {
            FavoritesEmptyListContent(onNavigateToHome = onNavigateToHome)
        },
        content = {
            items(favoriteImages.itemCount) { index ->
                val image = favoriteImages[index]
                FavoriteImageItem(
                    fileURI = image?.largeImageURL ?: "",
                    onNavigateToDetails = {
                        onNavigateToDetails(
                            PictureDetailsNavArgs(
                                selectedImageIndex = index,
                                pagingKey = PagingKey.FAVORITES
                            )
                        )
                    }
                )
            }
        }
    )
}

@Composable
fun FavoriteImageItem(
    modifier: Modifier = Modifier,
    fileURI: String,
    onNavigateToDetails: () -> Unit
) {
    CardImage(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize(minHeight = 200.dp)
                .clickable(onClick = onNavigateToDetails),
            model = Uri.parse(fileURI),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun FavoritesEmptyListContent(modifier: Modifier = Modifier, onNavigateToHome: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(R.string.your_favorites_list_is_empty),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.check_more_wallpapers),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        CardButton(onClick = { onNavigateToHome() }) {
            Text(
                stringResource(R.string.go_to_home_screen),
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
        }
    }
}
