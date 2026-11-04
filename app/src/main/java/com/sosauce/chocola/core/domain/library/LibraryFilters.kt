package com.sosauce.chocola.core.domain.library

import com.sosauce.chocola.core.domain.util.regex
import com.sosauce.chocola.core.domain.util.thenIf
import com.sosauce.chocola.core.domain.model.SearchSettings
import com.sosauce.chocola.core.domain.model.Album
import com.sosauce.chocola.core.domain.model.Artist
import com.sosauce.chocola.core.domain.model.CuteTrack
import com.sosauce.chocola.core.domain.model.AlbumSort
import com.sosauce.chocola.core.domain.model.ArtistSort
import java.util.regex.PatternSyntaxException

fun List<CuteTrack>.search(
    query: String,
    searchSettings: SearchSettings,
): List<CuteTrack> {
    val regexPattern = try {
        query.regex(searchSettings.matchCase)
    } catch (_: PatternSyntaxException) {
        Regex("")
    }
    return filter { track ->
        if (searchSettings.regex) {
            regexPattern.containsMatchIn(track.title) || regexPattern.containsMatchIn(track.artist)
        } else {
            try {
                track.title.contains(query, !searchSettings.matchCase)
                        || track.artist.contains(query, !searchSettings.matchCase)
            } catch (_: PatternSyntaxException) {
                false
            }
        }
    }
}


fun List<Album>.ordered(
    sort: AlbumSort,
    regex: Boolean,
    matchCase: Boolean,
    ascending: Boolean,
    query: String
): List<Album> {
    val regexPattern = try {
        query.regex(matchCase)
    } catch (_: PatternSyntaxException) {
        Regex("")
    }

    val filtered = this.filter { track ->
        if (regex) {
            regexPattern.containsMatchIn(track.name) || regexPattern.containsMatchIn(track.artist)
        } else {
            try {
                track.name.contains(query, !matchCase)
                        || track.artist.contains(query, !matchCase)
            } catch (_: PatternSyntaxException) {
                false
            }
        }
    }

    return filtered
        .sortedBy {
            when (sort) {
                AlbumSort.NAME -> it.name
                AlbumSort.ARTIST -> it.artist
            }
        }.thenIf(!ascending) { asReversed() }
}

fun List<Artist>.ordered(
    sort: ArtistSort,
    regex: Boolean,
    matchCase: Boolean,
    ascending: Boolean,
    query: String
): List<Artist> {
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
        ArtistSort.NAME -> filtered.sortedBy { it.name }
        ArtistSort.NB_ALBUMS -> filtered.sortedBy { it.numberAlbums }
        ArtistSort.NB_TRACKS -> filtered.sortedBy { it.tracks.size }
    }.thenIf(!ascending) { asReversed() }
}

fun List<CuteTrack>.orderAlbumTrackNumber(): List<CuteTrack> {
    return sortedBy { it.trackNumber }
}

fun List<CuteTrack>.sortByAlbum(): List<CuteTrack> {
    return sortedWith(compareBy({it.album}, {it.trackNumber}))
}
