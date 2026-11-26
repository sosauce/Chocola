package com.sosauce.chocola.core.designsystem

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sosauce.chocola.R

/**
 * Every button of the now playing screen, in on-screen order.
 *
 * Stored in DataStore by [name], so entries must never be renamed.
 */
enum class NowPlayingButton(
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    val isHideable: Boolean = true
) {
    CLOSE(R.drawable.arrow_down, R.string.close),
    LYRICS(R.drawable.lyrics_filled, R.string.lyrics),
    QUEUE(R.drawable.queue, R.string.queue),
    MORE_OPTIONS(R.drawable.more_vert, R.string.more_options),
    PREVIOUS(R.drawable.skip_previous, R.string.previous_song),
    REWIND(R.drawable.fast_rewind, R.string.rewind),
    PLAY_PAUSE(R.drawable.widget_play, R.string.now_playing, isHideable = false),
    FORWARD(R.drawable.fast_forward, R.string.fast_forward),
    NEXT(R.drawable.skip_next, R.string.next_song),
    SHUFFLE(R.drawable.shuffle, R.string.shuffle),
    REPEAT(R.drawable.repeat, R.string.repeat),
    SPEED(R.drawable.speed_filled, R.string.speed),
    FAVORITE(R.drawable.favorite_filled, R.string.favorite),
    SLEEP_TIMER(R.drawable.sleep_timer_filled, R.string.sleep_timer);

    companion object {
        val hideable = entries.filter { it.isHideable }
    }
}

/**
 * Maps stored names back to buttons. Unknown names (e.g. from an older or edited backup)
 * and buttons that can't be hidden are ignored.
 */
fun Set<String>.toHiddenNowPlayingButtons(): Set<NowPlayingButton> =
    NowPlayingButton.hideable.filterTo(mutableSetOf()) { it.name in this }
