@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sosauce.chocola.core.data.library

import android.content.Context
import android.provider.MediaStore
import com.sosauce.chocola.core.data.content.observe
import com.sosauce.chocola.core.domain.model.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest


class FoldersRepository(
    private val context: Context
) {


    fun fetchLatestMusicFolders() =
        context.contentResolver.observe(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).mapLatest {
            fetchMusicFolders()
        }.flowOn(Dispatchers.IO)


    // Only gets folder with musics in them
    private fun fetchMusicFolders(): List<Folder> {

        val folders = mutableListOf<Folder>()

        val projection = arrayOf(MediaStore.Audio.Media.DATA)

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use {
            val folderPaths = mutableSetOf<String>()
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val filePath = it.getString(dataColumn)
                val folderPath = filePath.substringBeforeLast('/')
                folderPaths.add(folderPath)
            }
            folderPaths.forEach { path ->
                val folderName = path.substringAfterLast('/')
                folders.add(
                    Folder(
                        name = folderName,
                        path = path,
                    )
                )
            }

        }
        return folders
    }
}