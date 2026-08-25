package com.sosauce.chocola.presentation.screens.settings.compenents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.utils.GITHUB_RELEASES
import com.sosauce.chocola.utils.SUPPORT_PAGE
import com.sosauce.chocola.utils.appVersion
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@Composable
fun AboutCard() {

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(15.dp)
                    .background(
                        shape = SquircleShape(smoothing = CornerSmoothing.Full),
                        color = Color(0xFFf4a7bd)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.music_note),
                    contentDescription = stringResource(id = R.string.app_icon),
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFFfdd9dc)
                )
            }
            Column {
                Text(
                    text = stringResource(id = R.string.app_name),

                    )
                Text(
                    text = "v${context.appVersion}",
                    style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Spacer(Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(end = 15.dp)
            ) {
                FilledIconButton(
                    onClick = { uriHandler.openUri(GITHUB_RELEASES) },
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.github),
                        contentDescription = null
                    )
                }
                FilledIconButton(
                    onClick = { uriHandler.openUri(SUPPORT_PAGE) },
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.favorite_filled),
                        contentDescription = null
                    )
                }
            }
        }
    }
}