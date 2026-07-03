package com.technehq.boot.gson;

import com.google.gson.*;
import com.technehq.boot.constants.TechneDatePattern;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * LocalDataTimeTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class LocalDataTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDateTime.format(TechneDatePattern.NORM_DATETIME_FORMATTER));
    }

    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return LocalDateTime.parse(jsonElement.getAsString(), TechneDatePattern.NORM_DATETIME_FORMATTER);
    }

}