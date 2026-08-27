package com.sosauce.chocola.presentation.screens.transformer

import android.annotation.SuppressLint
import android.app.Application
import android.os.Environment
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import java.io.File

@SuppressLint("UnsafeOptInUsageError")
class TransformerViewModel(
    private val application: Application,
    private val trackUri: String
) : AndroidViewModel(application) {

    private val inputMediaItem = MediaItem.Builder()
        .setUri(trackUri.toUri())
        .setClippingConfiguration(
            MediaItem.ClippingConfiguration.Builder()
                .setEndPositionMs(0)
                .setEndPositionMs(60000)
                .build()
        )
        .build()

    private val editedMediaItem = EditedMediaItem.Builder(inputMediaItem).build()

    private val musicDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    private val outputFile = File(musicDir, "MyOGGAudio.m4a")
    private val transformerListener = object : Transformer.Listener {
        override fun onCompleted(composition: Composition, exportResult: ExportResult) {
            super.onCompleted(composition, exportResult)
            Toast.makeText(application, "Success!", Toast.LENGTH_SHORT).show()
        }

        override fun onError(
            composition: Composition,
            exportResult: ExportResult,
            exportException: ExportException
        ) {
            super.onError(composition, exportResult, exportException)
            Toast.makeText(application, exportException.message, Toast.LENGTH_SHORT).show()
            outputFile.delete()

        }
    }
    val transformer = Transformer.Builder(application)
        .addListener(transformerListener)
        .setAudioMimeType(MimeTypes.AUDIO_AAC)
        .build()

    init {
        transformer.start(editedMediaItem, outputFile.absolutePath)
    }

    override fun onCleared() {
        super.onCleared()
        transformer.removeListener(transformerListener)
        transformer.cancel()
    }

}