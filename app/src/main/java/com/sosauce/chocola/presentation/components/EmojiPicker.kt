@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.components

import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import com.sosauce.chocola.R
import com.sosauce.nekobites.animations.AnimatedFab

@Composable
fun EmojiPicker(
    onDismiss: () -> Unit,
    onEmojiPicked: (String) -> Unit
) {

    val onBackground = MaterialTheme.colorScheme.onBackground.toArgb()
    fun findViewsById(view: View, targetId: Int): List<View> {
        val results = mutableListOf<View>()
        if (view.id == targetId) results.add(view)
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                results.addAll(findViewsById(view.getChildAt(i), targetId))
            }
        }
        return results
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 10.dp)
                .statusBarsPadding(),
            factory = { ctx ->
                val themedContext = ContextThemeWrapper(ctx, R.style.CustomEmojiPickerTheme)
                EmojiPickerView(themedContext).apply {
                    setOnEmojiPickedListener(onEmojiPickedListener = {
                        onEmojiPicked(it.emoji)
                        onDismiss()
                    })

                    postDelayed({
                        findViewsById(this, 2131361868).forEach {
                            (it as? TextView)?.setTextColor(onBackground)
                        }
                    }, 100)
                }
            }
        )
        AnimatedFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .statusBarsPadding()
                .padding(15.dp),
            onClick = onDismiss,
            icon = R.drawable.close
        )
    }
}