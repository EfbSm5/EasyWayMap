package com.efbsm5.easyway.data.database

import android.net.Uri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromUri(uri: Uri): String {
        return uri.toString()
    }

    @TypeConverter
    fun toUri(uriString: String): Uri {
        return Uri.parse(uriString)
    }

    @TypeConverter
    fun fromUriList(uriList: List<String>?): String {
        return uriList?.joinToString(separator = ",") { it } ?: ""
    }

    @TypeConverter
    fun toUriList(data: String?): List<String> {
        return data?.split(",")?.map { it } ?: emptyList()
    }
}
