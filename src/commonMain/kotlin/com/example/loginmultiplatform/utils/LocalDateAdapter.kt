package com.example.loginmultiplatform.utils

import com.google.gson.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import java.lang.reflect.Type

class LocalDateAdapter : JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate {
        return json?.asString?.toLocalDate() ?: throw JsonParseException("Invalid date format")
    }

    // LocalDate'ten JSON'a serialization
    override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString())
    }
}