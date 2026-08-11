@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberInitialScreen
import com.sosauce.chocola.presentation.components.LazyRowWithScrollButton
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.settings.compenents.SelectorSurface
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsSelector
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsWithTitle

@Composable
fun SettingsNavigation() {

    var initialScreen by rememberInitialScreen()

    val screenItems = listOf(
        ScreenItem(
            onClick = { initialScreen = Screen.Main.toString() },
            icon = R.drawable.music_note,
            text = R.string.main,
            isSelected = initialScreen == Screen.Main.toString()
        ),
        ScreenItem(
            onClick = { initialScreen = Screen.Albums.toString() },
            icon = R.drawable.album_filled,
            text = R.string.albums,
            isSelected = initialScreen == Screen.Albums.toString()

        ),
        ScreenItem(
            onClick = { initialScreen = Screen.Artists.toString() },
            icon = R.drawable.artists_filled,
            text = R.string.artists,
            isSelected = initialScreen == Screen.Artists.toString()
        )
    )

    Column {
        SettingsWithTitle(
            title = R.string.default_launch_screen
        ) {
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column {
                    LazyRowWithScrollButton(
                        items = screenItems
                    ) { screen ->
                        SettingsSelector(
                            onClick = screen.onClick,
                            icon = screen.icon,
                            text = screen.text,
                            isSelected = screen.isSelected
                        )
                    }
                }
            }
        }
    }

}

data class ScreenItem(
    val onClick: () -> Unit,
    val icon: Int,
    val text: Int,
    val isSelected: Boolean
)