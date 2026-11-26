@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.feature.bakery.presentation.story

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.chocola.R
import com.sosauce.chocola.core.designsystem.theme.ChocolaTheme
import com.sosauce.chocola.core.presentation.util.selfAlignHorizontally
import com.sosauce.chocola.feature.bakery.presentation.components.StoryProgressBar
import com.sosauce.chocola.feature.bakery.presentation.model.AlbumStatUi
import com.sosauce.chocola.feature.bakery.presentation.model.ArtistStatUi
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodId
import com.sosauce.chocola.feature.bakery.presentation.model.PeriodType
import com.sosauce.chocola.feature.bakery.presentation.model.StoryPageUi
import com.sosauce.chocola.feature.bakery.presentation.model.StreakUi
import com.sosauce.chocola.feature.bakery.presentation.model.TopDayUi
import com.sosauce.chocola.feature.bakery.presentation.model.TrackStatUi
import com.sosauce.chocola.feature.bakery.presentation.story.pages.EmptyPageText
import com.sosauce.chocola.feature.bakery.presentation.story.pages.FunFactsPage
import com.sosauce.chocola.feature.bakery.presentation.story.pages.StreakPage
import com.sosauce.chocola.feature.bakery.presentation.story.pages.SummaryPage
import com.sosauce.chocola.feature.bakery.presentation.story.pages.TopAlbumsPage
import com.sosauce.chocola.feature.bakery.presentation.story.pages.TopArtistsPage
import com.sosauce.chocola.feature.bakery.presentation.story.pages.TopDayPage
import com.sosauce.chocola.feature.bakery.presentation.story.pages.TopTracksPage
import com.sosauce.chocola.feature.bakery.presentation.story.pages.TotalsPage
import com.sosauce.nekobites.animations.AnimatedDrawable
import com.sosauce.nekobites.animations.AnimatedDrawableFile
import com.sosauce.nekobites.animations.AnimatedFab
import com.sosauce.nekobites.helpers.ObserveAsEvents
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BakeryStoryRoot(
    period: PeriodId,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val viewModel = koinViewModel<BakeryStoryViewModel>(
        parameters = { parametersOf(period) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            BakeryStoryEvent.Close -> onClose()
            BakeryStoryEvent.SummarySaved -> Toast.makeText(
                context,
                resources.getString(R.string.bakery_saved),
                Toast.LENGTH_SHORT
            ).show()

            BakeryStoryEvent.SaveFailed -> Toast.makeText(
                context,
                resources.getString(R.string.bakery_save_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    BakeryStoryScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun BakeryStoryScreen(
    state: BakeryStoryState,
    onAction: (BakeryStoryAction) -> Unit
) {
    BackHandler { onAction(BakeryStoryAction.Close) }

    val summaryLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            StoryProgressBar(
                pageCount = state.pages.size,
                currentPage = state.currentPage,
                pageProgress = state.pageProgress,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
            )
        },
        bottomBar = {
            StoryControls(
                modifier = Modifier.navigationBarsPadding(),
                state = state,
                onAction = onAction,
                onSaveSummary = {
                    scope.launch {
                        runCatching {
                            summaryLayer.toImageBitmap().asAndroidBitmap()
                        }.getOrNull()?.let { bitmap ->
                            onAction(BakeryStoryAction.SaveSummary(bitmap))
                        }
                    }
                }
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
        ) {

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularWavyProgressIndicator()
                    }
                }

                state.pages.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        EmptyPageText(stringResource(R.string.bakery_empty_desc))
                    }
                }

                else -> {
                    StoryPager(
                        state = state,
                        onAction = onAction,
                        summaryLayer = summaryLayer,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryPager(
    state: BakeryStoryState,
    onAction: (BakeryStoryAction) -> Unit,
    summaryLayer: GraphicsLayer,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { state.pages.size }
    )
    val currentPage by rememberUpdatedState(state.currentPage)

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { settledPage ->
                when {
                    settledPage > currentPage -> onAction(BakeryStoryAction.NextPage)
                    settledPage < currentPage -> onAction(BakeryStoryAction.PreviousPage)
                }
            }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize(),
        userScrollEnabled = false
    ) { page ->
        when (val content = state.pages.getOrNull(page)) {
            is StoryPageUi.Totals -> TotalsPage(content)
            is StoryPageUi.TopTracks -> TopTracksPage(content)
            is StoryPageUi.TopArtists -> TopArtistsPage(content)
            is StoryPageUi.TopAlbums -> TopAlbumsPage(content)
            is StoryPageUi.TopDay -> TopDayPage(content)
            is StoryPageUi.Streak -> StreakPage(content)
            is StoryPageUi.FunFacts -> FunFactsPage(content)
            is StoryPageUi.Summary -> SummaryPage(
                summary = content,
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        summaryLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(summaryLayer)
                    }
            )

            null -> Unit
        }
    }
}

@Composable
private fun StoryControls(
    modifier: Modifier = Modifier,
    state: BakeryStoryState,
    onAction: (BakeryStoryAction) -> Unit,
    onSaveSummary: () -> Unit
) {
    val isSummary = state.pages.getOrNull(state.currentPage) is StoryPageUi.Summary

    HorizontalFloatingToolbar(
        modifier = modifier.selfAlignHorizontally(),
        expanded = true,
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
        floatingActionButton = {
            AnimatedContent(
                targetState = isSummary,
                transitionSpec = { slideInVertically { it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut() }
            ) {
                if (it) {
                    AnimatedFab(
                        onClick = { onSaveSummary() },
                        icon = R.drawable.save_filled
                    )
                } else {
                    AnimatedFab(
                        onClick = { onAction(BakeryStoryAction.Close) },
                        icon = R.drawable.close
                    )
                }
            }
        }
    ) {
        IconButton(
            onClick = { onAction(BakeryStoryAction.PreviousPage) },
            shapes = IconButtonDefaults.shapes(),
            enabled = state.currentPage > 0
        ) {
            Icon(
                painter = painterResource(R.drawable.skip_previous),
                contentDescription = null
            )
        }

        IconButton(
            onClick = {
                onAction(
                    if (state.isPaused) {
                        BakeryStoryAction.Resume
                    } else {
                        BakeryStoryAction.Pause
                    }
                )
            },
            shapes = IconButtonDefaults.shapes()
        ) {
            AnimatedDrawable(
                drawable = AnimatedDrawableFile.PLAY,
                atEnd = !state.isPaused
            )
        }

        IconButton(
            onClick = { onAction(BakeryStoryAction.NextPage) },
            shapes = IconButtonDefaults.shapes(),
            enabled = state.currentPage < state.pages.lastIndex
        ) {
            Icon(
                painter = painterResource(R.drawable.skip_next),
                contentDescription = null
            )
        }
    }

//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 32.dp, vertical = 14.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        IconButton(
//            onClick = { onAction(BakeryStoryAction.PreviousPage) },
//            shapes = IconButtonDefaults.shapes(),
//            enabled = state.currentPage > 0
//        ) {
//            Icon(
//                painter = painterResource(R.drawable.skip_previous),
//                contentDescription = null
//            )
//        }
//
//        FilledIconButton(
//            onClick = {
//                onAction(
//                    if (state.isPaused) {
//                        BakeryStoryAction.Resume
//                    } else {
//                        BakeryStoryAction.Pause
//                    }
//                )
//            },
//            modifier = Modifier.size(58.dp)
//        ) {
//            Icon(
//                painter = painterResource(
//                    if (state.isPaused) R.drawable.widget_play else R.drawable.widget_pause
//                ),
//                contentDescription = null,
//                modifier = Modifier.size(28.dp)
//            )
//        }
//
//        if (isSummary) {
//            IconButton(
//                onClick = onSaveSummary,
//                enabled = !state.isSaving,
//                shapes = IconButtonDefaults.shapes()
//            ) {
//                if (state.isSaving) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(22.dp),
//                        strokeWidth = 2.dp
//                    )
//                } else {
//                    Icon(
//                        painter = painterResource(R.drawable.save_filled),
//                        contentDescription = stringResource(R.string.save)
//                    )
//                }
//            }
//        } else {
//            IconButton(
//                onClick = { onAction(BakeryStoryAction.NextPage) },
//                shapes = IconButtonDefaults.shapes(),
//                enabled = state.currentPage < state.pages.lastIndex
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.skip_next),
//                    contentDescription = null
//                )
//            }
//        }
//    }
}

@Preview(
    showBackground = true,
    widthDp = 380,
    heightDp = 820,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun BakeryStoryScreenPreview() {

    ChocolaTheme {
        BakeryStoryScreen(
            state = BakeryStoryState(
                isLoading = false,
                period = PeriodId(PeriodType.MONTH, "2026-10"),
                pages = previewPages,
                currentPage = 0,
                pageProgress = 0.35f
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 820)
@Composable
private fun BakeryStorySummaryPreview() {

    ChocolaTheme {
        BakeryStoryScreen(
            state = BakeryStoryState(
                isLoading = false,
                period = PeriodId(PeriodType.MONTH, "2026-10"),
                pages = previewPages,
                currentPage = 2,
                pageProgress = 0f
            ),
            onAction = {}
        )
    }
}

private val previewPages = listOf(
    StoryPageUi.Totals(
        periodTitle = "October 2026",
        totalListens = 1_284,
        totalListenedMillis = 21_834_000L
    ),
    StoryPageUi.TopTracks(
        items = listOf(
            TrackStatUi("1", "Cinnamon Girl", "Lana Del Rey", "NFR!", null, 412),
            TrackStatUi("2", "After Dark", "Mr.Kitty", "Time", null, 388),
            TrackStatUi("3", "Apocalypse", "Cigarettes After Sex", "Cry", null, 302)
        )
    ),
    StoryPageUi.Summary(
        period = PeriodId(PeriodType.MONTH, "2026-10"),
        title = "October 2026",
        totalListens = 1_284,
        totalListenedMillis = 21_834_000L,
        topTrack = TrackStatUi(
            mediaId = "1",
            title = "Cinnamon Girl",
            artist = "Lana Del Rey",
            album = "Norman Fucking Rockwell!",
            artworkUri = null,
            listenCount = 412
        ),
        topArtist = ArtistStatUi("Lana Del Rey", 1_024),
        topAlbum = AlbumStatUi("Norman Fucking Rockwell!", "Lana Del Rey", null, 892),
        streak = StreakUi(current = 12, best = 36),
        topDay = TopDayUi(
            epochDay = 20_694,
            label = "Saturday, October 18",
            listenCount = 186,
            listenedMillis = 3_060_000L
        )
    )
)
