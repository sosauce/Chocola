@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.setup.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.components.WavySlider
import com.sosauce.chocola.core.presentation.preferences.rememberMinTrackDuration
import com.sosauce.chocola.feature.settings.presentation.components.foldersView
import com.sosauce.chocola.feature.setup.presentation.components.SetupHero
import com.sosauce.nekobites.components.settings.SettingsGroup
import com.sosauce.nekobites.components.settings.SettingsSlider
import com.sosauce.nekobites.helpers.ScopedViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SetupFolders(
    onNext: () -> Unit
) {
    ScopedViewModel {
        val viewModel = koinViewModel<SetupViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        var minTrackDuration by rememberMinTrackDuration()

        AnimatedContent(
            targetState = state.isLoadingFolders,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize()
        ) { isLoading ->
            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    SetupHero(
                        title = stringResource(R.string.library_setup),
                        description = stringResource(R.string.scanning_folders_notice),
                        shape = MaterialShapes.Cookie9Sided
                    ) {
                        LoadingIndicator(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(96.dp)
                        )
                    }
                }
            } else {
                Scaffold(
                    bottomBar = {
                        Button(
                            onClick = onNext,
                            shapes = ButtonDefaults.shapes(),
                            contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp)
                                .height(ButtonDefaults.MediumContainerHeight)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.check),
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.MediumIconSize)
                            )
                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                            Text(
                                text = "Let's a meow!",
                                style = MaterialTheme.typography.titleMediumEmphasized
                            )
                        }
                    }
                ) { pv ->
                    LazyColumn(
                        contentPadding = pv,
                    ) {
                        item(key = "header_setup") {
                            SetupHero(
                                icon = R.drawable.hide_filled,
                                title = stringResource(R.string.library_setup),
                                description = stringResource(R.string.library_setup_desc),
                                shape = MaterialShapes.Cookie9Sided,
                                size = 140.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 8.dp)
                            )
                        }

                        foldersView(
                            whitelisted = state.whitelistedFolders,
                            blacklisted = state.blacklistedFolders,
                            onBlacklist = { paths ->
                                viewModel.handleSetupAction(SetupActions.BlacklistFolders(paths))
                            },
                            onWhitelist = { paths ->
                                viewModel.handleSetupAction(SetupActions.WhitelistFolders(paths))
                            }
                        )

                        item(key = "trailing_setup") {
                            SettingsGroup {
                                SettingsSlider(
                                    title = stringResource(R.string.min_track_length_text),
                                    value = minTrackDuration,
                                    onValueChange = { minTrackDuration = it },
                                    unit = "s",
                                    slider = { state, onChange -> WavySlider(state, onChange) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
