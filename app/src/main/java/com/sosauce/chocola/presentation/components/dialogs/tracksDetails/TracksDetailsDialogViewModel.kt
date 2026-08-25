package com.sosauce.chocola.presentation.components.dialogs.tracksDetails

import android.app.Application
import android.net.Uri
import android.os.FileUtils
import android.provider.MediaStore
import android.text.format.DateUtils
import android.text.format.Formatter
import android.webkit.MimeTypeMap
import androidx.core.net.toFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kyant.taglib.TagLib
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.CuteTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class TracksDetailsDialogViewModel(
    private val track: CuteTrack,
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(TracksDetailsState())
    val state = _state.asStateFlow()


    init {
        loadFileDetails()
    }


    fun loadFileDetails() {
        viewModelScope.launch(Dispatchers.IO) {

            val lastModified = getLastModified()
            val type = application.contentResolver.getType(track.uri)
            val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(type)

            application.contentResolver.openFileDescriptor(track.uri, "r")?.use { fd ->

                val properties = TagLib.getAudioProperties(
                    fd = fd.dup().detachFd()
                )
                val metadata = TagLib.getMetadata(
                    fd = fd.dup().detachFd(),
                    readPictures = false
                )?.propertyMap

                val trackInfo = buildList {
                    add(
                        TrackDetails(
                            icon = androidx.media3.session.R.drawable.media3_icon_album,
                            text = R.string.album,
                            data = metadata?.get("ALBUM")?.getOrNull(0) ?: "-"
                        )
                    )
                    add(
                        TrackDetails(
                            icon = R.drawable.duration,
                            text = R.string.duration,
                            data = properties?.length?.let { DateUtils.formatElapsedTime(it / 1000L) } ?: "-"
                        )
                    )
                    add(
                        TrackDetails(
                            icon = R.drawable._123,
                            text = R.string.track_nb,
                            data = metadata?.get("TRACKNUMBER")?.getOrNull(0) ?: "-"
                        )
                    )
                    add(
                        TrackDetails(
                            icon = R.drawable._123,
                            text = R.string.disc_nb,
                            data = metadata?.get("DISCNUMBER")?.getOrNull(0) ?: "-"
                        )
                    )
                    add(
                        TrackDetails(
                            icon = R.drawable.calendar_filled,
                            text = R.string.date,
                            data = metadata?.get("DATE")?.getOrNull(0) ?: "-"
                        )
                    )
                    add(
                        TrackDetails(
                            icon = R.drawable.shapes,
                            text = R.string.genre,
                            data = metadata?.get("GENRE")?.getOrNull(0) ?: "-"
                        )
                    )
                }

                val fileInfo = buildList {
                    add(
                        TrackDetails(
                            icon = R.drawable.audio_file,
                            text = R.string.type,
                            data = fileExtension ?: "-"
                        )
                    )
                    add(
                        TrackDetails(
                            icon = R.drawable._123,
                            text = R.string.size,
                            data = Formatter.formatFileSize(
                                application,
                                fd.statSize
                            )
                        )
                    )
                    add(
                        TrackDetails(
                            icon = R.drawable.eq,
                            text = R.string.bitrate,
                            data = properties?.bitrate?.toString()?.plus(" kbps") ?: "-"
                        )
                    )
                    add(
                        TrackDetails(
                            icon = R.drawable.surround_sound,
                            text = R.string.channels,
                            data = properties?.channels?.toString() ?: "-"
                        )
                    )
                    add(
                        TrackDetails(
                            icon = R.drawable.saf,
                            text = R.string.saf,
                            data = if (track.isSaf) application.getString(R.string.yes) else application.getString(R.string.no)
                        )
                    )
                    add(
                        TrackDetails(
                            icon = R.drawable.edit_rounded,
                            text = R.string.date_modified,
                            data = DateUtils.formatDateTime(application, lastModified, DateUtils.FORMAT_ABBREV_MONTH)
                        )
                    )
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        trackInfo = trackInfo,
                        fileInfo = fileInfo
                    )
                }
            }
        }
    }


    private fun getLastModified(): Long {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.DATE_MODIFIED
        )
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(track.mediaId)

        application.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use {
            val lastModifiedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)

            if (it.moveToFirst()) {
                val lastModified = it.getLong(lastModifiedColumn) * 1000

                return lastModified
            }
        }

        return 0
    }
}

data class TracksDetailsState(
    val isLoading: Boolean = true,
    val trackInfo: List<TrackDetails> = emptyList(),
    val fileInfo: List<TrackDetails> = emptyList(),
)