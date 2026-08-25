package com.sosauce.chocola.presentation.screens.lyrics

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.R
import com.sosauce.chocola.domain.model.Lyrics
import com.sosauce.chocola.utils.copyMutate
import com.sosauce.chocola.utils.toLyricDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

class LyricsEditorViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(LyricsEditorState())
    val state = _state.asStateFlow()

    fun addLyricLine(lyric: Lyrics) {
        _state.update {
            it.copy(
                lyrics = it.lyrics.copyMutate { add(lyric) }
            )
        }
    }

    fun removeLyricLine(lyric: Lyrics) {
        _state.update {
            it.copy(
                lyrics = it.lyrics.copyMutate { remove(lyric) }
            )
        }
    }

    fun editLyricLine(
        index: Int,
        lyric: Lyrics
    ) {
        _state.update {
            it.copy(
                lyrics = it.lyrics.copyMutate { set(index, lyric) }
            )
        }
    }

    fun saveFile(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                application.contentResolver.openFileDescriptor(uri, "w")?.use { fileDescriptor ->
                    FileOutputStream(fileDescriptor.fileDescriptor).use { outputStream ->
                        state.value.lyrics.fastForEachIndexed { index, lyrics ->

                            if (lyrics.lineLyrics.isEmpty() || lyrics.lineLyrics.isBlank()) {
                                return@fastForEachIndexed
                            }

                            val timestamp = "[" + lyrics.timestamp.toLyricDuration() + "]"
                            val line = timestamp + " " + lyrics.lineLyrics
                            outputStream.write(line.toByteArray())
                            if (index != state.value.lyrics.lastIndex) {
                                outputStream.write("\n".toByteArray())
                            }
                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(application, application.resources.getString(R.string.error_saving),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




}


data class LyricsEditorState(
    val lyrics: List<Lyrics> = emptyList()
)


