package com.sosauce.chocola.data

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class MediaItemConverter {


    @TypeConverter
    fun stringListToString(mediaItems: Set<String>): String {
        return Json.encodeToString(mediaItems)
    }

    @TypeConverter
    fun stringToListString(string: String): Set<String> {
        return Json.decodeFromString(string)
    }


}