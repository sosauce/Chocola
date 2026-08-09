package com.sosauce.chocola.data

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import androidx.compose.ui.util.fastMap
import com.kyant.taglib.TagLib
import com.mocharealm.accompanist.lyrics.core.model.ISyncedLine
import com.mocharealm.accompanist.lyrics.core.model.karaoke.KaraokeLine
import com.mocharealm.accompanist.lyrics.core.model.synced.SyncedLine
import com.mocharealm.accompanist.lyrics.core.model.synced.mapper.toSyncedLine
import com.mocharealm.accompanist.lyrics.core.parser.AutoParser
import com.mocharealm.accompanist.lyrics.core.parser.EnhancedLrcParser
import com.sosauce.chocola.domain.model.Lyrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

class LyricsParser(private val context: Context) {

    suspend fun parseLyrics(
        path: String
    ): List<Lyrics> = withContext(Dispatchers.IO) {
        val parent = path.substringBeforeLast('/')
        val fileName = path.substringAfterLast('/').replaceAfterLast('.', "lrc")
        val lyricsFile = File(parent, fileName)


        return@withContext if (lyricsFile.exists()) {
            lyricsFile.bufferedReader().useLines { lines ->
                val lyrics = EnhancedLrcParser.parse(lines.toList())
                lyrics.lines.fastMap { line ->
                    if (line is SyncedLine) {
                        line.toLyricLine()
                    } else {
                        (line as KaraokeLine).toSyncedLine().toLyricLine()
                    }
                }
            }
        } else {

            val embeddedLyrics = loadEmbeddedLyrics(path) ?: return@withContext emptyList()



            // Tries to load synced embedded lyrics, if embedded lyrics are unsynced, just return raw embedded lyrics
            autoParser.parse(embeddedLyrics)
                .takeIf { it.lines.isNotEmpty() }
                ?.lines?.fastMap { line ->
                    if (line is SyncedLine) {
                        line.toLyricLine()
                    } else {
                        (line as KaraokeLine).toSyncedLine().toLyricLine()
                    }
                } ?: listOf(Lyrics(lineLyrics = embeddedLyrics))
        }

    }

    private fun loadEmbeddedLyrics(path: String): String? {
        val fd = getFileDescriptorFromPath(context, path) ?: return null
        return fd.dup().detachFd()
            .let { TagLib.getMetadata(it)?.propertyMap?.get("LYRICS")?.getOrNull(0) ?: "" }
            .takeIf { it.isNotEmpty() }
    }

    @SuppressLint("Range")
    private fun getFileDescriptorFromPath(
        context: Context,
        filePath: String,
        mode: String = "r"
    ): ParcelFileDescriptor? {
        val resolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.Files.FileColumns._ID)
        val selection = "${MediaStore.Files.FileColumns.DATA}=?"
        val selectionArgs = arrayOf(filePath)

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

    private fun SyncedLine.toLyricLine(): Lyrics {
        return Lyrics(
            timestamp = this.start,
            lineLyrics = this.content
        )
    }

    companion object {
        private val autoParser = AutoParser()
    }

}