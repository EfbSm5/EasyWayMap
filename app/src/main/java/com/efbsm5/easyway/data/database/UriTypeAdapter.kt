package com.efbsm5.easyway.data.database

import android.net.Uri
import androidx.core.net.toUri
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class UriTypeAdapter : TypeAdapter<Uri>() {
    override fun write(out: JsonWriter, value: Uri?) {
        out.value(value?.toString())
    }

    override fun read(`in`: JsonReader): Uri {
        val uriString = `in`.nextString()
        return uriString.toUri()
    }
}