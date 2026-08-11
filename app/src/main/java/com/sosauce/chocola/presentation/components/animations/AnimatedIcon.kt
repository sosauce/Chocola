package com.sosauce.chocola.presentation.components.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.presentation.components.animations.AnimatedIconStatus.Default
import com.sosauce.chocola.presentation.components.animations.AnimatedIconStatus.Error
import com.sosauce.chocola.presentation.components.animations.AnimatedIconStatus.Success
import com.sosauce.chocola.utils.barsContentTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Animated icon status
 * there are 3 possible states for this component:
 *  [Default], [Success] and [Error]
 */
enum class AnimatedIconStatus {
    Default, Success, Error
}


/**
 * Controller for animated icon status transitions.
 *
 * Manages state changes with automatic reset after delays.
 * Cancels previous jobs if new state is triggered before reset completes.
 *
 * @param scope CoroutineScope for launching state change animations
 */
class AnimatedIcon(
    private val scope: CoroutineScope
) {
    private val _status = MutableStateFlow(Default)
    val status = _status.asStateFlow()

    private var job: Job? = null


    /**
     * Sets icon to [AnimatedIconStatus.Error] state.
     *
     * Shows [AnimatedIconStatus.Error] icon for 500ms then returns to [AnimatedIconStatus.Default].
     * Cancels any previous pending state change.
     */
    fun setError() {
        job?.cancel()
        job = scope.launch {
            _status.value = Error
            delay(500)
            _status.value = Default
        }
    }

    /**
     * Sets icon to [AnimatedIconStatus.Success] state.
     *
     * Shows [AnimatedIconStatus.Success] icon for 500ms then returns to [AnimatedIconStatus.Default].
     * Cancels any previous pending state change.
     */
    fun setSuccess() {
        job = scope.launch {
            _status.value = Success
            delay(500)
            _status.value = Default
        }
    }
}


/**
 * Returns the appropriate painter for this status.
 *
 * @param defaultIcon Resource ID of the default icon
 * @return Painter for the current status ([AnimatedIconStatus.Default], [AnimatedIconStatus.Success], or [AnimatedIconStatus.Error] icon)
 */
@Composable
fun AnimatedIconStatus.icon(defaultIcon: Int): Painter {
    return painterResource(
        when (this) {
            Default -> defaultIcon
            Success -> R.drawable.check
            Error -> R.drawable.close
        }
    )
}


/**
 * Composable animated icon with state transitions.
 *
 * Displays icon that animates between [AnimatedIconStatus.Default], [AnimatedIconStatus.Success], and [AnimatedIconStatus.Error] states.
 * Icon is clickable and resets to default after animation completes.
 *
 * @param defaultIcon Resource ID of the default icon to display
 * @param onClick Callback when icon is clicked
 */
@Composable
fun AnimatedIcon.Icon(
    defaultIcon: Int,
    onClick: () -> Unit
) {
    val status by this.status.collectAsState()

    AnimatedContent(
        targetState = status,
        transitionSpec = { barsContentTransform }
    ) { status ->
        val painter = status.icon(defaultIcon)

        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .clickable(onClick = onClick)
                .padding(5.dp)
        )
    }
}

/**
 * Creates and remembers an [AnimatedIcon] controller.
 *
 * Automatically manages the lifecycle with the composition.
 *
 * @return [AnimatedIcon] instance tied to current composition scope
 */
@Composable
fun rememberClipboardIconController(): AnimatedIcon {
    val scope = rememberCoroutineScope()
    return remember { AnimatedIcon(scope) }
}
