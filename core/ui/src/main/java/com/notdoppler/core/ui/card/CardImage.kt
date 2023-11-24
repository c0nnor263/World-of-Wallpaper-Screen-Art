package com.notdoppler.core.ui.card

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notdoppler.core.ui.R
import com.notdoppler.core.ui.theme.WallpapersTheme

@Composable
fun CardImage(
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.inversePrimary)
    ) {
        image()
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Composable
fun CardImagePreview() {
    WallpapersTheme {
        CardImage(modifier = Modifier.size(300.dp, 500.dp)) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                painter = painterResource(id = R.drawable.yes_icon),
                contentDescription = null
            )
        }
    }
}
