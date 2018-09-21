package com.tretton37.twitter37.utils

import android.graphics.Point
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.io.IOException


/**
 * Created by suyashg
 *
 * Utility for time formatting
 *
 */
class InstantAdapter {

    companion object {
        val INSTANCE = InstantAdapter()
    }

    @ToJson
    @Throws(IOException::class)
    fun pointToJson(writer: JsonWriter, point: Point) {
        writer.beginArray()
        writer.value(point.x)
        writer.value(point.y)
        writer.endArray()
    }

    @FromJson
    @Throws(Exception::class)
    fun pointFromJson(reader: JsonReader): Point {
        reader.beginArray()
        val x = reader.nextInt()
        val y = reader.nextInt()
        reader.endArray()
        return Point(x, y)
    }
}