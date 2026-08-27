package com.sosauce.chocola.presentation.screens.metadata

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kyant.taglib.AudioProperties
import com.kyant.taglib.AudioPropertiesReadStyle
import com.kyant.taglib.Metadata
import com.kyant.taglib.Picture
import com.kyant.taglib.TagLib
import com.sosauce.chocola.utils.toAudioFileMetadata
import com.sosauce.chocola.utils.toModifiableMap
import com.sosauce.chocola.utils.toPropertyMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


class MetadataViewModel(
    private val trackPath: String,
    private val application: Application
) : AndroidViewModel(application) {


    private val _events = Channel<MetadataEvents>()
    val events = _events.receiveAsFlow()


    private val _metadata = MutableStateFlow(MetadataState())
    val metadataState = _metadata.asStateFlow()


    private val _legacyAskPermission = Channel<PendingIntent?>()
    val legacyAskPermission = _legacyAskPermission.receiveAsFlow()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadMetadata()
        }
    }

    private suspend fun loadMetadata() {
        runCatching {
            getFileDescriptorFromPath("r")?.use { fd ->
                val metadata = loadAudioMetadata(fd)
                val audioProperties = loadAudioProperties(fd)
                val audioArt = loadAudioArt(fd)

                _metadata.update {
                    it.copy(
                        metadata = metadata,
                        audioProperties = audioProperties,
                        art = audioArt
                    )
                }

            }
        }.onSuccess {
            metadataState.value.metadata?.propertyMap?.toModifiableMap()?.forEach {
                metadataState.value.mutablePropertiesMap[it.key] = it.value ?: ""
            }
        }
    }


    private suspend fun loadAudioMetadata(songFd: ParcelFileDescriptor): Metadata? {
        val fd = songFd.dup()?.detachFd() ?: throw NullPointerException()

        return withContext(Dispatchers.IO) {
            TagLib.getMetadata(fd)
        }
    }

    private suspend fun loadAudioProperties(
        songFd: ParcelFileDescriptor,
        readStyle: AudioPropertiesReadStyle = AudioPropertiesReadStyle.Fast
    ): AudioProperties? {
        val fd = songFd.dup()?.detachFd() ?: throw NullPointerException()

        return withContext(Dispatchers.IO) {
            TagLib.getAudioProperties(fd, readStyle)
        }
    }

    private suspend fun loadAudioArt(songFd: ParcelFileDescriptor): Picture? {
        val fd = songFd.dup()?.detachFd() ?: throw NullPointerException()

        return withContext(Dispatchers.IO) {
            TagLib.getFrontCover(fd)
        }
    }


    private fun saveChangesApi30Plus() {
        try {
            (getFileDescriptorFromPath("w")
                ?: throw Exception("No file descriptor found!")).use { fd ->
                fd.dup().detachFd().let {
                    TagLib.savePropertyMap(it,
                        metadataState.value.mutablePropertiesMap.toAudioFileMetadata()
                            .toPropertyMap()
                    )
                }

                fd.dup().detachFd().let {
                    val newPic =
                        metadataState.value.art?.let { art -> arrayOf(art) } ?: emptyArray()
                    TagLib.savePictures(it, newPic)
                }

                scanTrack()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveChangesLegacy() {
        try {
            val sourceFileUri = getFileUri() ?: throw FileNotFoundException()

            application.contentResolver.openFileDescriptor(sourceFileUri, "rw", null)?.use { fd ->
                fd.dup().detachFd().let {
                    TagLib.savePropertyMap(it,
                        metadataState.value.mutablePropertiesMap.toAudioFileMetadata()
                            .toPropertyMap()
                    )
                }
                fd.dup().detachFd().let {
                    val newPic =
                        metadataState.value.art?.let { art -> arrayOf(art) } ?: emptyArray()
                    TagLib.savePictures(it, newPic)
                }
            }

            scanTrack()

        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val recoverableSecurityException =
                    e as? RecoverableSecurityException ?: throw RuntimeException(
                        e.message, e
                    )

                _legacyAskPermission.trySend(recoverableSecurityException.userAction.actionIntent)
            } else {
                throw RuntimeException(e.message, e)
            }
        }
    }

    private fun scanTrack() {
        MediaScannerConnection.scanFile(
            application.applicationContext,
            arrayOf(trackPath),
            null,
            null
        )
    }

    private fun saveNewAudioArt(uri: Uri) {

        // App will crash if it tries to open an input stream on an empty uri !
        if (uri == Uri.EMPTY) return

        val byteArray = application.contentResolver.openInputStream(uri)?.use { inputStream ->

            val baos = ByteArrayOutputStream()
            BitmapFactory.decodeStream(inputStream).apply {
                compress(Bitmap.CompressFormat.JPEG, 100, baos)
            }

            baos.toByteArray()
        }


        val picture = Picture(
            data = byteArray ?: byteArrayOf(),
            description = "",
            pictureType = "Front Cover",
            mimeType = "image/jpeg"
        )

        _metadata.update {
            it.copy(
                art = picture
            )
        }

    }

    @SuppressLint("Range")
    private fun getFileDescriptorFromPath(mode: String = "r"): ParcelFileDescriptor? {
        val resolver = application.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.Files.FileColumns._ID)
        val selection = "${MediaStore.Files.FileColumns.DATA}=?"
        val selectionArgs = arrayOf(trackPath)

        resolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val fileId = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                if (fileId == -1) {
                    return null
                } else {
                    val fileUri = Uri.withAppendedPath(uri, fileId.toString())
                    try {
                        return resolver.openFileDescriptor(fileUri, mode)
                    } catch (e: FileNotFoundException) {
                        Log.e("MediaStoreReceiver", "File not found: ${e.message}")
                    }
                }
            }
        }

        return null
    }

    private fun getFileUri(): Uri? {

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        val selection = "${MediaStore.Audio.Media.DATA} = ?"
        val selectionArgs = arrayOf(trackPath)

        return application.contentResolver.query(uri, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    ContentUris.withAppendedId(uri, id)
                } else null
            }
    }


    fun onHandleMetadataActions(action: MetadataActions) {
        when (action) {
            is MetadataActions.SaveChanges -> {
                viewModelScope.launch(Dispatchers.IO) {

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            saveChangesApi30Plus()
                        } else {
                            saveChangesLegacy()
                        }
                        _events.trySend(MetadataEvents.SaveSuccessful)
                    } catch (e: Exception) {
                        _events.trySend(MetadataEvents.SaveUnsuccessful(e.message))
                    }

                }
            }


            is MetadataActions.UpdateAudioArt -> {
                saveNewAudioArt(action.newArtUri)
            }

            is MetadataActions.RemoveArtwork -> {
                _metadata.update {
                    it.copy(art = null)
                }
            }
        }
    }
}

sealed interface MetadataEvents {
    data object SaveSuccessful : MetadataEvents
    data class SaveUnsuccessful(
        val error: String?
    ) : MetadataEvents
}



