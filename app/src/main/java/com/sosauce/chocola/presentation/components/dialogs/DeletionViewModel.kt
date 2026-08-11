package com.sosauce.chocola.presentation.components.dialogs

import android.app.Application
import android.app.PendingIntent
import android.app.RecoverableSecurityException
import android.content.ContentProviderOperation
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DeletionViewModel(
    private val application: Application
) : AndroidViewModel(application) {


    private val _legacyAskPermission = Channel<PendingIntent?>()
    val legacyAskPermission = _legacyAskPermission.receiveAsFlow()

    /**
     * Unified function to delete tracks on any API
     */
    fun deleteTrack(
        tracks: List<Uri>,
        launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                deleteTrackAndroid11Plus(tracks, launcher)
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                deleteTrackAndroid10(tracks)
            } else {
                deleteTrackBelowAndroid10(tracks)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun deleteTrackAndroid11Plus(
        uris: List<Uri>,
        launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
    ) {
        val intentSender = MediaStore.createDeleteRequest(
            application.contentResolver,
            uris
        ).intentSender

        launcher.launch(IntentSenderRequest.Builder(intentSender).build())
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteTrackAndroid10(uris: List<Uri>) {
        try {
            deleteTrackBelowAndroid10(uris)
        } catch (exception: Exception) {
            if (exception is RecoverableSecurityException) {
                val pendingIntent = exception.userAction.actionIntent
                _legacyAskPermission.trySend(pendingIntent)
            }
        }
    }

    fun deleteTrackBelowAndroid10(uris: List<Uri>) {

        val ops = ArrayList<ContentProviderOperation>()
        uris.fastForEach {
            ops.add(
                ContentProviderOperation.newDelete(it).build()
            )
        }

        application.contentResolver.applyBatch(MediaStore.AUTHORITY, ops)
    }


}