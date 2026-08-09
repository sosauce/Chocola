package com.sosauce.chocola.data.widgets

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.util.fastForEach
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.sosauce.chocola.presentation.widgets.MusicWidget2x1
import com.sosauce.chocola.presentation.widgets.MusicWidget4x1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val WIDGET_TITLE = stringPreferencesKey("WIDGET_TITLE")
val WIDGET_ARTIST = stringPreferencesKey("WIDGET_ARTIST")
val WIDGET_ART = stringPreferencesKey("WIDGET_ART")
val WIDGET_IS_PLAYING = booleanPreferencesKey("WIDGET_IS_PLAYING")

class WidgetsHelper(
    private val context: Context,
    private val ioScope: CoroutineScope
) {
    private val widgets = listOf(MusicWidget4x1, MusicWidget2x1)
    private val manager = GlanceAppWidgetManager(context)


    fun <T> updateMusicWidgetData(
        key: Preferences.Key<T>,
        value: T
    ) {
        value
        ioScope.launch {
            widgets.fastForEach { widget ->
                val glanceIds = manager.getGlanceIds(widget.javaClass)
                glanceIds.fastForEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[key] = value
                    }
                    widget.update(context, glanceId)
                }
            }
        }
    }

    fun artUriToByteArrayString(
        uri: Uri?
    ): String {
        if (uri == null) return ""

        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { stream -> stream.readBytes() }
            bytes?.takeIf { it.isNotEmpty() }?.let { Base64.encodeToString(it, Base64.DEFAULT) } ?: ""
        } catch (e: Exception) { "" }


    }
}