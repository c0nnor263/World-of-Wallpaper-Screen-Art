package com.notdoppler.feature.picturedetails.presentation.common.dialog

import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.notdoppler.core.ui.R
import com.notdoppler.feature.picturedetails.domain.model.PublisherInfoData
import com.notdoppler.feature.picturedetails.state.PublisherInfoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublisherInfoDialog(
    modifier: Modifier = Modifier,
    state: PublisherInfoState,
    onTagSearch: (String) -> Unit,
) {
    AnimatedVisibility(
        visible = state.isVisible, modifier = modifier
    ) {
        AlertDialog(
            onDismissRequest = {
                state.hide()
            }
        ) {
            Card(shape = RoundedCornerShape(24.dp)) {
                Column(modifier = Modifier.padding(4.dp)) {
                    UserSection(state)
                    Spacer(modifier = Modifier.height(16.dp))
                    state.info?.tags?.split(",")?.map { it.trim() }?.let { tags ->
                        TagSection(tags, onTagSearch = onTagSearch)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoSection(
                        state, modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }

    }
}


@Composable
fun UserSection(state: PublisherInfoState, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Row(
        modifier = modifier.padding(16.dp)
    ) {

        Row(
            modifier = Modifier.weight(1f),
        ) {
            AsyncImage(
                model = state.info?.userImageURL,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.baseline_account_circle_24),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            val url =
                                context.getString(
                                    com.notdoppler.feature.picturedetails.R.string.pixabay_user_id_user_profile,
                                    state.info?.user?.lowercase(),
                                    state.info?.userId
                                )
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = state.info?.user.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "ID:" + state.info?.userId.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        val url = state.info?.pageURL
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                    .padding(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_web_24),
                    contentDescription = null
                )
                Text(
                    stringResource(id = com.notdoppler.feature.picturedetails.R.string.source),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }

        }
    }
}


@Composable
fun TagSection(tags: List<String>, modifier: Modifier = Modifier, onTagSearch: (String) -> Unit) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Adaptive(80.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(tags) { tag ->
            TagCard(
                tag,
                onTagSearch = onTagSearch
            )
        }
    }
}

@Composable
fun TagCard(text: String, modifier: Modifier = Modifier, onTagSearch: (String) -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable(
                    onClick = {
                        onTagSearch(text)
                    },
                )
                .wrapContentSize(Alignment.Center),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun InfoSection(state: PublisherInfoState, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoCard(
                drawable = R.drawable.baseline_remove_red_eye_24,
                textData = (state.info?.views ?: 0).toString()
            )
            InfoCard(
                drawable = R.drawable.baseline_favorite_24,
                textData = (state.info?.likes ?: 0).toString()
            )
            InfoCard(
                drawable = R.drawable.baseline_downloading_24,
                textData = (state.info?.downloads ?: 0).toString()
            )
            InfoCard(
                drawable = R.drawable.baseline_comment_24,
                textData = (state.info?.comments ?: 0).toString()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Type: " + state.info?.type.toString(),
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray.copy(0.7F))
        )
    }
}

@Composable
fun InfoCard(@DrawableRes drawable: Int, modifier: Modifier = Modifier, textData: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(id = drawable),
            contentDescription = null
        )
        Text(text = textData)
    }
}


@Preview
@Composable
fun PublisherInfoDialogPreview() {
    PublisherInfoDialog(
        state = PublisherInfoState().apply {
            isVisible = true
            info = PublisherInfoData(
                comments = 0,
                downloads = 1,
                imageHeight = 5824,
                imageWidth = 3264,
                largeImageURL = "https://pixabay.com/get/g9e8c845b083d19e29d52c8f3583651d8b8fc3a230ebde1d182959d344fdb02e7ea3fc83be0c3025533ef91f0f85705a2141c42f519d633006b26953d5e7b1c60_1280.jpg",
                likes = 0,
                pageURL = "https://pixabay.com/illustrations/ai-generated-house-architecture-8314875/",
                previewURL = "https://cdn.pixabay.com/photo/2023/10/14/13/12/ai-generated-8314875_150.jpg",
                tags = "ai generated, house, architecture, generated, awesome, cool, deep",
                type = "illustration",
                user = "GrumpyBeere",
                userImageURL = "https://cdn.pixabay.com/user/2023/04/10/18-40-23-284_250x250.jpg",
                userId = 22072131,
                views = 2
            )
        }
    ) {

    }
}