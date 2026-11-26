package com.sosauce.chocola.core.data.library

import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.sosauce.chocola.core.data.datastore.UserPreferences
import com.sosauce.chocola.core.designsystem.NewFolderPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class FoldersClassifier(
    private val foldersRepository: FoldersRepository,
    private val userPreferences: UserPreferences
) {

    private val folderSettings = combine(
        userPreferences.getWhitelistedFolders(),
        userPreferences.getBlacklistedFolders(),
        userPreferences.getAutoAddSubfolders(),
        userPreferences.getNewFolderPolicy()
    ) { whitelisted, blacklisted, addSub, policy ->
        FolderSettings(whitelisted, blacklisted, addSub, policy)
    }


    suspend fun start() = withContext(Dispatchers.IO) {

        foldersRepository.fetchLatestMusicFolders().collectLatest { folders ->
            val setting = folderSettings.first()

            val paths = folders.fastMap { it.path }.sortedBy { it.length } // parents first
            val existing = paths.toHashSet()

            // delete folders from wl/bl that no longer exist
            val newWl = setting.whitelisted.filterTo(mutableSetOf()) { w ->
                w in existing || paths.any {
                    it.startsWith("$w/")
                }
            }
            val newBl = setting.blacklisted.filterTo(mutableSetOf()) { it in existing }


            paths.fastForEach { path ->
                // already decided by the user -> don't touch
                if (path in setting.whitelisted || path in setting.blacklisted) return@fastForEach

                when {
                    // subfolder of a whitelisted folder (explicit blacklist excluded above)
                    setting.addSub && hasWhitelistedAncestor(path, newWl) -> newWl += path
                    // folder policy for new folders
                    setting.newFolderPolicy == NewFolderPolicy.WHITELIST -> newWl += path
                    else -> newBl += path
                }
            }

            if (setting.whitelisted != newWl || setting.blacklisted != newBl) userPreferences.saveFoldersList(
                newWl,
                newBl
            )
        }
    }

    /**
     * True when any folder in [whitelisted] is a strict ancestor of [path].
     */
    private fun hasWhitelistedAncestor(path: String, whitelisted: Set<String>): Boolean {
        var parent = path.substringBeforeLast('/')
        while (parent.isNotEmpty() && parent != path) {
            if (parent in whitelisted) return true
            parent = parent.substringBeforeLast('/')
        }
        return false
    }

}

private data class FolderSettings(
    val whitelisted: Set<String>,
    val blacklisted: Set<String>,
    val addSub: Boolean,
    val newFolderPolicy: String
)