package com.sosauce.chocola.core.data.local

import com.sosauce.chocola.core.domain.util.regex
import com.sosauce.chocola.core.domain.util.thenIf
import com.sosauce.chocola.core.domain.model.PlaylistSort
import java.util.regex.PatternSyntaxException

fun List<Playlist>.ordered(
    sort: PlaylistSort,
    regex: Boolean,
    matchCase: Boolean,
    ascending: Boolean,
    query: String
): List<Playlist> {
    val regexPattern = try {
        query.regex(matchCase)
    } catch (_: PatternSyntaxException) {
        Regex("")
    }

    val filtered = this.filter { track ->
        if (regex) {
            regexPattern.containsMatchIn(track.name)
        } else {
            try {
                track.name.contains(query, !matchCase)
            } catch (_: PatternSyntaxException) {
                false
            }
        }
    }

    return when (sort) {
        PlaylistSort.NAME -> filtered.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
        PlaylistSort.COLOR -> filtered.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.color.toString() })
        PlaylistSort.TAGS -> filtered.sortedBy { it.tags.size }
        PlaylistSort.NB_TRACKS -> filtered.sortedBy { it.musics.size }
    }.thenIf(!ascending) { asReversed() }
}
