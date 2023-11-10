package com.notdoppler.feature.home.presentation.common

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notdoppler.core.ui.showShareDialog
import com.notdoppler.core.ui.theme.WallpapersTheme
import com.notdoppler.feature.home.R
import kotlinx.coroutines.launch

@Composable
fun HomeModalNavigationDrawer(
    modifier: Modifier = Modifier,
    onNavigateToFavorites: () -> Unit,
    onShowReview: () -> Unit,
    content: @Composable (DrawerState) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeModalNavigationDrawerContent(
                drawerState = drawerState,
                onShowReview = onShowReview,
                onNavigateToFavorites = onNavigateToFavorites
            )
        },
        gesturesEnabled = drawerState.isOpen,
        modifier = modifier
    ) {
        content(drawerState)
    }
}


@Composable
private fun HomeModalNavigationDrawerContent(
    drawerState: DrawerState,
    onShowReview: () -> Unit,
    onNavigateToFavorites: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ModalDrawerSheet {
        HeaderContent(modifier = Modifier)
        HomeNavigationDrawerItem(
            label = R.string.favorites_wallpapers,
            icon = com.notdoppler.core.ui.R.drawable.baseline_favorite_24,
            onClick = {
                scope.launch {
                    onNavigateToFavorites()
                    drawerState.close()
                }
            }
        )
        HomeNavigationDrawerItem(
            label = R.string.remove_ads,
            icon = com.notdoppler.core.ui.R.drawable.baseline_favorite_24,
            onClick = {
                // TODO remove_ads
            }
        )
        HomeNavigationDrawerItem(
            label = R.string.privacy_policy,
            icon = com.notdoppler.core.ui.R.drawable.baseline_favorite_24,
            onClick = {
                val policyUrl = context.getString(R.string.privacy_policy_link)
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(policyUrl)
                )
                context.startActivity(intent)
            }
        )
        HomeNavigationDrawerItem(
            label = R.string.rate_us,
            icon = com.notdoppler.core.ui.R.drawable.baseline_favorite_24,
            onClick = onShowReview
        )
        HomeNavigationDrawerItem(
            label = R.string.share_app,
            icon = com.notdoppler.core.ui.R.drawable.baseline_favorite_24,
            onClick = {
                showShareDialog(context)
            }
        )
        HomeNavigationDrawerItem(
            label = R.string.contact_us,
            icon = com.notdoppler.core.ui.R.drawable.baseline_favorite_24,
            onClick = {
                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:") // only email apps should handle this
                    putExtra(
                        Intent.EXTRA_EMAIL,
                        context.getString(R.string.developer_email)
                    )
                    val intent = Intent.createChooser(
                        this,
                        context.getString(R.string.contact_us)
                    )
                    context.startActivity(intent)
                }
            }
        )
    }
}

@Composable
fun HeaderContent(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            shape = RoundedCornerShape(0.dp),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(vertical = 16.dp)
                    .clickable(
                        onClick = {

                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = com.notdoppler.core.ui.R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(64.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = com.notdoppler.core.domain.R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}


@Composable
fun HomeNavigationDrawerItem(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    @DrawableRes icon: Int,

    onClick: () -> Unit = {},
) {
    NavigationDrawerItem(
        label = { Text(text = stringResource(id = label)) },
        selected = false,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null
            )
        },
        modifier = modifier.padding(8.dp)
    )
}


@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeModalNavigationDrawerContentPreview() {
    WallpapersTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
        HomeModalNavigationDrawerContent(
            drawerState = drawerState,
            onShowReview = {},
            onNavigateToFavorites = {})
    }
}