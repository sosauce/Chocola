@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.presentation.shared_components

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.lerp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberHasSeenTip
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.playing.NowPlaying
import com.sosauce.chocola.presentation.screens.playing.components.PlayPauseButton
import com.sosauce.chocola.presentation.shared_components.animations.AnimatedFab
import com.sosauce.chocola.presentation.shared_components.animations.AnimatedIconButton
import com.sosauce.chocola.utils.LocalScreen
import com.sosauce.chocola.utils.SharedTransitionKeys
import com.sosauce.chocola.utils.bouncySpec
import com.sosauce.chocola.utils.rememberInteractionSource
import com.sosauce.chocola.utils.rememberSearchbarRightPadding
import com.sosauce.chocola.utils.showBackButton
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@Composable
fun SharedTransitionScope.CuteSearchbar(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState = rememberTextFieldState(),
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onNavigateUp: (() -> Unit)? = null,
    showSearchField: Boolean = true,
    fab: @Composable (() -> Unit)? = null,
    sortingMenu: (@Composable (() -> Unit))? = null,
) {

    val currentScreen = LocalScreen.current
    val surfaceContainerHigh = MaterialTheme.colorScheme.surfaceContainerHigh
    var isInScreenSelectionMode by remember { mutableStateOf(false) }
    val screenToLeadingIcon = mapOf(
        Screen.Main to R.drawable.music_note,
        Screen.Albums to R.drawable.album_filled,
        Screen.Artists to R.drawable.artists_filled,
        Screen.Playlists to R.drawable.queue_music_rounded,
    )
    val scope = rememberCoroutineScope()
    var showFullPlayer by rememberSaveable { mutableStateOf(false) }
    val windowInfo = LocalWindowInfo.current




    AnimatedContent(
        targetState = showFullPlayer,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { fullPlayer ->
        if (fullPlayer) {

            val halfScreen = windowInfo.containerSize.height / 2f
            val yTranslationPlaying = remember { Animatable(halfScreen) }

            // enter in bounce
            LaunchedEffect(Unit) {
                yTranslationPlaying.animateTo(0f, bouncySpec())
            }

            val playingDragState = rememberDraggableState { dragAmount ->

                val value = (yTranslationPlaying.value + dragAmount).coerceAtLeast(0f) // always keep the value positive or else it's a shithole to manage

                scope.launch {
                    yTranslationPlaying.snapTo(value)
                }
            }

            BackHandler { showFullPlayer = false }
            NowPlaying(
                modifier = Modifier
                    .draggable(
                        state = playingDragState,
                        orientation = Orientation.Vertical,
                        onDragStopped = {
                            val targetValue = yTranslationPlaying.value
                            val minDistance = windowInfo.containerSize.height / 8

                            if (targetValue >= minDistance) {
                                showFullPlayer = false
                            } else {
                                scope.launch {
                                    yTranslationPlaying.animateTo(0f, bouncySpec())
                                }
                            }
                        }
                    )
                    .graphicsLayer {
                        translationY = yTranslationPlaying.value
                    },
                musicState = musicState,
                onHandlePlayerActions = onHandlePlayerActions,
                onNavigate = onNavigate,
                onShrinkToSearchbar = { showFullPlayer = false }
            )
        } else {


            val oneFourthOfHeight = windowInfo.containerSize.height / 4f
            val yTranslationSearchbar = remember { Animatable(0f) }


            val searchbarDragState = rememberDraggableState { dragAmount ->
                scope.launch {
                    // reminder: dragging up gives a negative value
                    val newValue = yTranslationSearchbar.value + dragAmount
                    val upLimit = -oneFourthOfHeight

                    val finalValue = if (newValue < 0) {
                        newValue.coerceAtLeast(upLimit)
                    } else {
                        newValue
                    }

                    yTranslationSearchbar.snapTo(finalValue)
                }
            }
            val progress by remember {
                derivedStateOf {
                    if (yTranslationSearchbar.value <= 0) {
                        (yTranslationSearchbar.value.absoluteValue / oneFourthOfHeight).coerceIn(0f, 1f)
                    } else 0f // means we're dragging down
                }
            }

            val searchbarWidth by animateFloatAsState(
                targetValue = lerp(0.85f, 1f, progress),
                animationSpec = bouncySpec()
            )


            Column(
                modifier = modifier
                    .navigationBarsPadding()
                    .fillMaxWidth(searchbarWidth)
                    .padding(end = rememberSearchbarRightPadding())
                    .imePadding()
                    .draggable(
                        state = searchbarDragState,
                        orientation = Orientation.Vertical,
                        enabled = musicState.isPlayerReady,
                        onDragStopped = {
                            val targetValue = yTranslationSearchbar.value

                            if (targetValue <= -oneFourthOfHeight / 2) {
                                showFullPlayer = true
                            } else if (targetValue > 0) {
                                onHandlePlayerActions(PlayerActions.StopPlayback)
                                scope.launch {
                                    yTranslationSearchbar.animateTo(0f, bouncySpec())
                                }
                            } else {
                                scope.launch {
                                    yTranslationSearchbar.animateTo(0f, bouncySpec())
                                }
                            }
                        }
                    )
                    .graphicsLayer {
                        translationY = yTranslationSearchbar.value
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    if (currentScreen.showBackButton()) {
                        AnimatedFab(
                            onClick = { onNavigateUp?.invoke() },
                            icon = R.drawable.back,
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    fab?.invoke()
//                    if (showShuffleButton || currentScreen == Screen.Playlists) {
//                    }
                }
                val animatedPosition by animateFloatAsState(musicState.position.toFloat(), tween(500))
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(
                            enabled = musicState.isPlayerReady
                        ) { showFullPlayer = true }
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .drawBehind {
                            val fraction = if (musicState.track.durationMs == 0L) {
                                0f
                            } else {
                                animatedPosition / musicState.track.durationMs.toFloat()
                            }

                            val drawWidth = size.width * fraction
                            drawRect(
                                color = surfaceContainerHigh,
                                size = Size(drawWidth, size.height)
                            )
                        }
                ) {
                    AnimatedVisibility(
                        visible = musicState.isPlayerReady,
                        enter = fadeIn() + slideInVertically { it }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnimatedContent(
                                    targetState = musicState.track.title,
                                    transitionSpec = { slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut() },
                                    modifier = modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = it,
                                        modifier = Modifier
                                            .padding(start = 10.dp)
                                            .sharedBounds(
                                                sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.CURRENTLY_PLAYING),
                                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                            )
                                            .basicMarquee()
                                    )
                                }
                            }
                            Row {
                                AnimatedIconButton(
                                    modifier = Modifier
                                        .sharedElement(
                                            sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_PREVIOUS_BUTTON),
                                            animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                        ),
                                    onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
                                    icon = R.drawable.skip_previous,
                                    contentDescription = stringResource(R.string.seek_prev_song)
                                )
                                PlayPauseButton(
                                    isPlaying = musicState.isPlaying,
                                    onHandlePlayerActions = onHandlePlayerActions,
                                    modifier = Modifier
                                        .sharedElement(
                                            sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.PLAY_PAUSE_BUTTON),
                                            animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                        )
                                )
                                AnimatedIconButton(
                                    modifier = Modifier
                                        .sharedElement(
                                            sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.SKIP_NEXT_BUTTON),
                                            animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                        ),
                                    onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
                                    icon = R.drawable.skip_next,
                                    contentDescription = stringResource(R.string.seek_next_song)
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = showSearchField
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .padding(6.dp)
                        ) {
                            SharedTransitionLayout {
                                AnimatedContent(
                                    targetState = isInScreenSelectionMode,
                                    //transitionSpec = { slideInVertically { it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut() }
                                ) {
                                    if (it) {
                                        ScreenSelection(
                                            onNavigate = onNavigate,
                                            dismiss = { isInScreenSelectionMode = false },
                                            animatedVisibilityScope = this
                                        )
                                    } else {
                                        TextField(
                                            state = textFieldState,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .sharedElement(
                                                    sharedContentState = rememberSharedContentState(SharedTransitionKeys.SEARCHBAR),
                                                    animatedVisibilityScope = this,
                                                ),
                                            colors = TextFieldDefaults.colors(
                                                focusedIndicatorColor = Color.Transparent,
                                                unfocusedIndicatorColor = Color.Transparent
                                            ),
                                            placeholder = {
                                                Text(
                                                    text = stringResource(R.string.search_here),
                                                    maxLines = 1
                                                )
                                            },
                                            leadingIcon = {
                                                var hasSeenTip by rememberHasSeenTip()
                                                TooltipBox(
                                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                                        TooltipAnchorPosition.Above
                                                    ),
                                                    tooltip = {
                                                        RichTooltip(
                                                            caretShape = TooltipDefaults.caretShape(),
                                                            colors = TooltipDefaults.richTooltipColors(
                                                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                                contentColor = contentColorFor(
                                                                    MaterialTheme.colorScheme.primaryContainer
                                                                )
                                                            ),
                                                        ) { Text(stringResource(R.string.click_hint)) }
                                                    },
                                                    state = rememberTooltipState(
                                                        initialIsVisible = !hasSeenTip,
                                                        isPersistent = !hasSeenTip
                                                    )
                                                ) {
                                                    IconButton(
                                                        onClick = {
                                                            isInScreenSelectionMode = true
                                                            hasSeenTip = true
                                                        },
                                                        shapes = IconButtonDefaults.shapes()
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(
                                                                screenToLeadingIcon[currentScreen]
                                                                    ?: R.drawable.search
                                                            ),
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                            },
                                            trailingIcon = {
                                                Row {
                                                    sortingMenu?.invoke()
                                                    IconButton(
                                                        onClick = { onNavigate(Screen.Settings) },
                                                        shapes = IconButtonDefaults.shapes()
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.settings_filled),
                                                            contentDescription = stringResource(R.string.settings)
                                                        )
                                                    }
                                                }
                                            },
                                            lineLimits = TextFieldLineLimits.SingleLine,
                                            shape = FloatingToolbarDefaults.ContainerShape
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun SharedTransitionScope.ScreenSelection(
    onNavigate: (Screen) -> Unit,
    dismiss: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val interactionsSources = List(4) { rememberInteractionSource() }
    val key = rememberSharedContentState(key = SharedTransitionKeys.SEARCHBAR)
    val currentScreen = LocalScreen.current
    val screens = listOf(
        ScreenCategory(
            screen = Screen.Main,
            onClick = { onNavigate(Screen.Main) },
            unselectedIcon = R.drawable.music_note,
            selectedIcon = R.drawable.music_note
        ),
        ScreenCategory(
            screen = Screen.Albums,
            onClick = { onNavigate(Screen.Albums) },
            unselectedIcon = androidx.media3.session.R.drawable.media3_icon_album,
            selectedIcon = R.drawable.album_filled
        ),
        ScreenCategory(
            screen = Screen.Artists,
            onClick = { onNavigate(Screen.Artists) },
            unselectedIcon = R.drawable.artist_rounded,
            selectedIcon = R.drawable.artists_filled
        ),
        ScreenCategory(
            screen = Screen.Playlists,
            onClick = { onNavigate(Screen.Playlists) },
            unselectedIcon = R.drawable.queue_music_rounded,
            selectedIcon = R.drawable.queue_music_rounded
        )
    )
    ButtonGroup(
        modifier = Modifier.fillMaxWidth(),
        overflowIndicator = {}
    ) {
        screens.fastForEachIndexed { index, item ->

            val isActive = currentScreen == item.screen

            customItem(
                buttonGroupContent = {

                    val containerColor = if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent

                    Button(
                        onClick = {
                            item.onClick()
                            dismiss()
                        },
                        interactionSource = interactionsSources[index],
                        colors = ButtonDefaults.buttonColors(
                            containerColor = containerColor,
                            contentColor = contentColorFor(containerColor)
                        ),
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier
                            .defaultMinSize(
                                minWidth = TextFieldDefaults.MinWidth,
                                minHeight = TextFieldDefaults.MinHeight,
                            )
                            .weight(1f)
                            .animateWidth(interactionsSources[index])
                            .then(
                                if (isActive) {
                                    Modifier.sharedElement(
                                        sharedContentState = key,
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    )
                                } else Modifier
                            )
                    ) {
                        val icon = if (isActive) item.selectedIcon else item.unselectedIcon

                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null
                        )
                    }
                },
                menuContent = {}
            )
        }
    }
}

private data class ScreenCategory(
    val screen: Screen,
    val onClick: () -> Unit,
    @param:DrawableRes val unselectedIcon: Int,
    @param:DrawableRes val selectedIcon: Int
)












