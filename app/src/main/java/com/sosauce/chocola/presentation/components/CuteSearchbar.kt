@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.presentation.components

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.lerp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.skydoves.cloudy.Sky
import com.skydoves.cloudy.cloudy
import com.skydoves.cloudy.rememberSky
import com.skydoves.cloudy.sky
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberAlbumGrids
import com.sosauce.chocola.data.datastore.rememberAlbumSort
import com.sosauce.chocola.data.datastore.rememberArtistSort
import com.sosauce.chocola.data.datastore.rememberGroupByFolders
import com.sosauce.chocola.data.datastore.rememberHasSeenTip
import com.sosauce.chocola.data.datastore.rememberMatchCaseFilter
import com.sosauce.chocola.data.datastore.rememberRegexFilter
import com.sosauce.chocola.data.datastore.rememberShowShuffleButton
import com.sosauce.chocola.data.datastore.rememberSortAlbumsAscending
import com.sosauce.chocola.data.datastore.rememberSortTracksAscending
import com.sosauce.chocola.data.datastore.rememberTrackSort
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.components.animations.AnimatedDrawable
import com.sosauce.chocola.presentation.components.animations.AnimatedFab
import com.sosauce.chocola.presentation.components.animations.AnimatedIconButton
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.playing.NowPlaying
import com.sosauce.chocola.presentation.screens.playing.components.PlayPauseButton
import com.sosauce.chocola.utils.LocalScreen
import com.sosauce.chocola.utils.SharedTransitionKeys
import com.sosauce.chocola.utils.bouncySpec
import com.sosauce.chocola.utils.rememberInteractionSource
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun SharedTransitionScope.CuteSearchbar(
    modifier: Modifier = Modifier,
    musicState: MusicState,
    textFieldState: TextFieldState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    fab: @Composable (() -> Unit)? = null,
    backButton: @Composable (() -> Unit)? = null,
    sortMenu: @Composable () -> Unit
) {

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
            CuteSearchbarContent(
                modifier = modifier,
                musicState = musicState,
                textFieldState = textFieldState,
                onHandlePlayerActions = onHandlePlayerActions,
                onOpenFullPlayer = { showFullPlayer = true },
                sortMenu = sortMenu,
                onNavigate = onNavigate,
                fab = fab,
                backButton = backButton
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.CuteSearchbarContent(
    modifier: Modifier = Modifier,
    musicState: MusicState,
    textFieldState: TextFieldState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onOpenFullPlayer: () -> Unit,
    fab: @Composable (() -> Unit)? = null,
    backButton: @Composable (() -> Unit)? = null,
    sortMenu: @Composable () -> Unit
) {

    val windowInfo = LocalWindowInfo.current
    val currentScreen = LocalScreen.current
    val scope = rememberCoroutineScope()
    val oneFourthOfHeight = windowInfo.containerSize.height / 4f
    val yTranslationSearchbar = remember { Animatable(0f) }
    var isInScreenSelectionMode by rememberSaveable { mutableStateOf(false) }
    val showFab by rememberShowShuffleButton()


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
        targetValue = lerp(0.85f, 0.95f, progress),
        animationSpec = bouncySpec()
    )
    // force 24.dp when player isn't ready because controls won't be here
    val animatedRadius by animateDpAsState(
        if (isInScreenSelectionMode || !musicState.isPlayerReady) 24.dp else 0.dp
    )


    Column(
        modifier = modifier
            .fillMaxWidth(searchbarWidth)
            .navigationBarsPadding()
            .imePadding()
            .draggable(
                state = searchbarDragState,
                orientation = Orientation.Vertical,
                enabled = musicState.isPlayerReady && !isInScreenSelectionMode,
                onDragStopped = {
                    val targetValue = yTranslationSearchbar.value

                    if (targetValue <= -oneFourthOfHeight / 2) {
                        onOpenFullPlayer()
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
        // FABs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            backButton?.invoke()
            Spacer(Modifier.weight(1f))
            if (showFab || currentScreen == Screen.Playlists) {
                fab?.invoke()
            }
        }
        // Controls
        AnimatedVisibility(
            visible = musicState.isPlayerReady,
            enter = fadeIn() + slideInVertically { it }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                            bottomStart = animatedRadius,
                            bottomEnd = animatedRadius
                        )
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer
                    )
                    .clickable(
                        enabled = musicState.isPlayerReady,
                        onClick = onOpenFullPlayer,
                        indication = null,
                        interactionSource = null
                    )
                    .drawMusicPosition(
                        position = { musicState.position.toFloat() },
                        trackDuration = { musicState.duration },
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    targetState = musicState.track.title,
                    transitionSpec = { slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut() },
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.basicMarquee()
                    )
                }
                Row {
                    AnimatedIconButton(
                        onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
                        icon = R.drawable.skip_previous,
                        contentDescription = null
                    )
                    PlayPauseButton(
                        isPlaying = musicState.isPlaying,
                        onHandlePlayerActions = onHandlePlayerActions
                    )
                    AnimatedIconButton(
                        onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
                        icon = R.drawable.skip_next,
                        contentDescription = null
                    )
                }
            }
        }
        // Bottom content

        val spacerHeight by animateDpAsState(
            targetValue = if (isInScreenSelectionMode) 10.dp else 0.dp,
            animationSpec = bouncySpec()
        )

        if (musicState.isPlayerReady) { // To avoid FAB bounce when no controls
            Spacer(spacerHeight)
        }
        Box(
            modifier = Modifier
                .sharedElement(
                    sharedContentState = rememberSharedContentState(SharedTransitionKeys.SEARCHBAR),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                )
                .fillMaxWidth()
                .clip(
                    shape = RoundedCornerShape(
                        topStart = animatedRadius,
                        topEnd = animatedRadius,
                        bottomStart = 24.dp,
                        bottomEnd = 24.dp
                    )
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
                .drawMusicPosition(
                    position = { musicState.position.toFloat() },
                    trackDuration = { musicState.duration },
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                )
        ) {
            SharedTransitionLayout {
                AnimatedContent(
                    targetState = isInScreenSelectionMode,
                    transitionSpec = { slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { it } + fadeOut() }
                ) {
                    CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                        if (it) {
                            ScreenSelection(
                                onNavigate = onNavigate,
                                dismiss = { isInScreenSelectionMode = false }
                            )
                        } else {
                            CuteSearchbarDefaults.CuteSearchbarTextField(
                                state = textFieldState,
                                onNavigate = onNavigate,
                                onSwitchToScreenSelection = { isInScreenSelectionMode = true },
                                sortingMenuPopupContent = sortMenu
                            )
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
    dismiss: () -> Unit
) {

    val currentScreen = LocalScreen.current
    val haptic = LocalHapticFeedback.current
    val screens = listOf(
        ScreenCategory(
            screen = Screen.Main,
            onClick = { onNavigate(Screen.Main) },
            unselectedIcon = R.drawable.music_note,
            selectedIcon = R.drawable.music_note,
            name = R.string.tracks
        ),
        ScreenCategory(
            screen = Screen.Albums,
            onClick = { onNavigate(Screen.Albums) },
            unselectedIcon = androidx.media3.session.R.drawable.media3_icon_album,
            selectedIcon = R.drawable.album_filled,
            name = R.string.albums
        ),
        ScreenCategory(
            screen = Screen.Artists,
            onClick = { onNavigate(Screen.Artists) },
            unselectedIcon = R.drawable.artist_rounded,
            selectedIcon = R.drawable.artists_filled,
            name = R.string.artists
        ),
        ScreenCategory(
            screen = Screen.Playlists,
            onClick = { onNavigate(Screen.Playlists) },
            unselectedIcon = R.drawable.queue_music_rounded,
            selectedIcon = R.drawable.queue_music_rounded,
            name = R.string.playlists
        )
    )

    ShortNavigationBar {
        screens.forEach { screen ->

            val selected = currentScreen == screen.screen

            ShortNavigationBarItem(
                selected = selected,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                    dismiss()
                    screen.onClick()
                },
                icon = {
                    val icon = if (selected) screen.selectedIcon else screen.unselectedIcon
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(icon),
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
                            )
                    )
                },
                label = {
                    Text(
                        text = stringResource(screen.name)
                    )
                }
            )
        }
    }

//    ButtonGroup(
//        modifier = Modifier.fillMaxWidth(),
//        overflowIndicator = {}
//    ) {
//
//
//
//        screens.fastForEachIndexed { index, item ->
//
//            val isActive = currentScreen == item.screen
//
//            customItem(
//                buttonGroupContent = {
//
//                    val containerColor = if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent
//
//                    Button(
//                        onClick = {
//                            item.onClick()
//                            dismiss()
//                        },
//                        interactionSource = interactionsSources[index],
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = containerColor,
//                            contentColor = contentColorFor(containerColor)
//                        ),
//                        shapes = ButtonDefaults.shapes(),
//                        modifier = Modifier
//                            .defaultMinSize(
//                                minWidth = TextFieldDefaults.MinWidth,
//                                minHeight = TextFieldDefaults.MinHeight,
//                            )
//                            .weight(1f)
//                            .animateWidth(interactionsSources[index])
//                    ) {
//                        val icon = if (isActive) item.selectedIcon else item.unselectedIcon
//
//                        Icon(
//                            painter = painterResource(icon),
//                            contentDescription = null
//                        )
//                    }
//                },
//                menuContent = {}
//            )
//        }
//    }
}

private data class ScreenCategory(
    val screen: Screen,
    val onClick: () -> Unit,
    val name: Int,
    val unselectedIcon: Int,
    val selectedIcon: Int
)


private fun Modifier.drawMusicPosition(
    position: () -> Float,
    trackDuration: () -> Long,
    color: Color
): Modifier = this.drawWithCache {
    onDrawBehind {
        val duration = trackDuration()
        val fraction = if (duration <= 0L) {
            0f
        } else {
            (position() / duration.toFloat()).coerceIn(0f, 1f)
        }
        val drawWidth = size.width * fraction
        drawRect(
            color = color,
            size = Size(drawWidth, size.height)
        )
    }
}


object CuteSearchbarDefaults {

    @Composable
    fun BackButton(
        onNavigateBack: () -> Unit
    ) {
        AnimatedFab(
            onClick = onNavigateBack,
            icon = R.drawable.back,
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    }

    @Composable
    context(sharedTransitionScope: SharedTransitionScope)
    fun CuteSearchbarTextField(
        state: TextFieldState,
        onNavigate: (Screen) -> Unit,
        sortingMenuPopupContent: @Composable () -> Unit,
        onSwitchToScreenSelection: () -> Unit
    ) {

        var hasSeenTip by rememberHasSeenTip()
        var sortMenuExpanded by remember { mutableStateOf(false) }

        val screenToLeadingIcon = mapOf(
            Screen.Main to R.drawable.music_note,
            Screen.Albums to R.drawable.album_filled,
            Screen.Artists to R.drawable.artists_filled,
            Screen.Playlists to R.drawable.queue_music_rounded,
        )
        val currentScreen = LocalScreen.current
        val isSearching = state.text.isNotEmpty()



        with(sharedTransitionScope) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .padding(6.dp)
            ) {
                TextField(
                    state = state,
                    modifier = Modifier
                        .fillMaxWidth(),
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
                            AnimatedContent(
                                targetState = isSearching
                            ) {
                                if (it) {
                                    Icon(
                                        painter = painterResource(R.drawable.search),
                                        contentDescription = null
                                    )
                                } else {

                                    val icon = screenToLeadingIcon[currentScreen]

                                    IconButton(
                                        onClick = {
                                            onSwitchToScreenSelection()
                                            hasSeenTip = true
                                        },
                                        shapes = IconButtonDefaults.shapes()
                                    ) {
                                        Icon(
                                            painter = painterResource(icon ?: R.drawable.search),
                                            modifier = Modifier
                                                .sharedElement(
                                                    sharedContentState = rememberSharedContentState(icon ?: R.drawable.search),
                                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                                ),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }

                        }
                    },
                    trailingIcon = {
                        AnimatedContent(
                            targetState = isSearching
                        ) {
                            if (it) {
                                IconButton(
                                    onClick = state::clearText,
                                    shapes = IconButtonDefaults.shapes()
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.cancel_filled),
                                        contentDescription = null
                                    )
                                }
                            } else {
                                Row {
                                    Box {
                                        IconButton(
                                            onClick = { sortMenuExpanded = !sortMenuExpanded },
                                            shapes = IconButtonDefaults.shapes()
                                        ) {
                                            AnimatedDrawable(
                                                drawable = R.drawable.animated_sort,
                                                atEnd = sortMenuExpanded
                                            )
                                        }
                                        DropdownMenuPopup(
                                            expanded = sortMenuExpanded,
                                            onDismissRequest = { sortMenuExpanded = false }
                                        ) {
                                            sortingMenuPopupContent()
                                        }
                                    }
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
                            }
                        }
                    },
                    lineLimits = TextFieldLineLimits.SingleLine,
                    shape = FloatingToolbarDefaults.ContainerShape
                )
            }
        }

    }


    @Composable
    fun AlbumSortPopupContent() {

        var numberOfAlbumGrids by rememberAlbumGrids()
        var isSortedByASC by rememberSortAlbumsAscending()
        var albumSort by rememberAlbumSort()

        DropdownMenuGroup(
            shapes = MenuDefaults.groupShape(0, 2)
        ) {
            DropdownMenuItem(
                onClick = {
                    numberOfAlbumGrids =
                        if (numberOfAlbumGrids == 4) 2 else numberOfAlbumGrids + 1
                },
                text = { Text(stringResource(R.string.no_of_grids)) },
                trailingContent = { Text("$numberOfAlbumGrids") },
                shape = MenuDefaults.leadingItemShape
            )
        }
        Spacer(Modifier.height(MenuDefaults.GroupSpacing))
        DropdownMenuGroup(
            shapes = MenuDefaults.groupShape(1, 2),
            content = {
                repeat(2) { i ->
                    val text = when (i) {
                        0 -> R.string.name
                        1 -> R.string.artist
                        else -> throw IndexOutOfBoundsException()
                    }
                    DropdownMenuItem(
                        selected = albumSort == i,
                        onClick = { albumSort = i },
                        shapes = MenuDefaults.itemShape(i, 2),
                        colors = MenuDefaults.selectableItemColors(),
                        text = { Text(stringResource(text)) }
                    )
                }
            }
        )
        Spacer(Modifier.height(MenuDefaults.GroupSpacing))
        SortingButtons(
            ascending = isSortedByASC,
            onChangeSort = { isSortedByASC = it }
        )
    }

    @Composable
    fun TrackSortPopupContent() {

        var trackSort by rememberTrackSort()
        var sortTracksAsc by rememberSortTracksAscending()
        var groupByFolders by rememberGroupByFolders()


        DropdownMenuGroup(
            shapes = MenuDefaults.groupShape(0, 2)
        ) {
            DropdownMenuItem(
                selected = groupByFolders,
                onClick = { groupByFolders = !groupByFolders },
                shapes = MenuDefaults.itemShape(0, 2),
                colors = MenuDefaults.selectableItemColors(),
                text = { Text(stringResource(R.string.group_tracks)) }
            )
        }
        Spacer(Modifier.height(MenuDefaults.GroupSpacing))
        DropdownMenuGroup(
            shapes = MenuDefaults.groupShape(1, 2),
            content = {
                repeat(5) { i ->
                    val text = when (i) {
                        0 -> R.string.title
                        1 -> R.string.artist
                        2 -> R.string.album
                        3 -> R.string.year
                        4 -> R.string.date_modified
                        else -> throw IndexOutOfBoundsException()
                    }

                    DropdownMenuItem(
                        selected = trackSort == i,
                        onClick = { trackSort = i },
                        shapes = MenuDefaults.itemShape(i, 5),
                        colors = MenuDefaults.selectableItemColors(),
                        text = { Text(stringResource(text)) }
                    )
                }
            }
        )
        Spacer(Modifier.height(MenuDefaults.GroupSpacing))
        SortingButtons(
            ascending = sortTracksAsc,
            onChangeSort = { sortTracksAsc = it }
        )
    }

    @Composable
    fun ArtistSortPopupContent() {

        var isSortedByASC by rememberSortAlbumsAscending()
        var artistSort by rememberArtistSort()

        DropdownMenuGroup(
            shapes = MenuDefaults.groupShapes(),
            content = {
                repeat(3) { i ->
                    val text = when (i) {
                        0 -> R.string.name
                        1 -> R.string.number_of_tracks
                        2 -> R.string.number_of_albums
                        else -> throw IndexOutOfBoundsException()
                    }

                    DropdownMenuItem(
                        selected = artistSort == i,
                        onClick = { artistSort = i },
                        shapes = MenuDefaults.itemShape(i, 3),
                        colors = MenuDefaults.selectableItemColors(),
                        text = { Text(stringResource(text)) }
                    )
                }
            }
        )
        Spacer(Modifier.height(MenuDefaults.GroupSpacing))
        SortingButtons(
            ascending = isSortedByASC,
            onChangeSort = { isSortedByASC = it }
        )
    }

    @Composable
    fun SortingButtons(
        ascending: Boolean,
        onChangeSort: (Boolean) -> Unit
    ) {

        val interactionSources = List(2) { rememberInteractionSource() }

        ButtonGroup(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
            overflowIndicator = {}
        ) {
            customItem(
                buttonGroupContent = {
                    ToggleButton(
                        checked = ascending,
                        onCheckedChange = { onChangeSort(true) },
                        interactionSource = interactionSources[0],
                        modifier = Modifier
                            .animateWidth(interactionSources[0])
                            .weight(1f)
                            .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.up),
                            contentDescription = null,
                            modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                        )
                    }
                },
                menuContent = {}
            )

            customItem(
                buttonGroupContent = {
                    ToggleButton(
                        checked = !ascending,
                        onCheckedChange = { onChangeSort(false) },
                        interactionSource = interactionSources[1],
                        modifier = Modifier
                            .animateWidth(interactionSources[1])
                            .weight(1f)
                            .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.down),
                            contentDescription = null,
                            modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                        )
                    }
                },
                menuContent = {}
            )
        }
        Spacer(Modifier.height(MenuDefaults.GroupSpacing))
        RegexButtons()
    }

    @Composable
    private fun RegexButtons() {

        var regexFilter by rememberRegexFilter()
        var matchCaseFilter by rememberMatchCaseFilter()
        val interactionSources = List(2) { rememberInteractionSource() }

        ButtonGroup(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
            overflowIndicator = {}
        ) {
            customItem(
                buttonGroupContent = {
                    ToggleButton(
                        checked = regexFilter,
                        onCheckedChange = { regexFilter = !regexFilter },
                        interactionSource = interactionSources[0],
                        modifier = Modifier
                            .animateWidth(interactionSources[0])
                            .weight(1f)
                            .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.regular_expression),
                            contentDescription = null,
                            modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                        )
                    }
                },
                menuContent = {}
            )

            customItem(
                buttonGroupContent = {
                    ToggleButton(
                        checked = matchCaseFilter,
                        onCheckedChange = { matchCaseFilter = !matchCaseFilter },
                        interactionSource = interactionSources[1],
                        modifier = Modifier
                            .animateWidth(interactionSources[1])
                            .weight(1f)
                            .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.match_case),
                            contentDescription = null,
                            modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                        )
                    }
                },
                menuContent = {}
            )
        }
    }
    

}












