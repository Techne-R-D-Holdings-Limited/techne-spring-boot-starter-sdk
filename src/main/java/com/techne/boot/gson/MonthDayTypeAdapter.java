package com.techne.boot.gson;

import com.google.gson.*;
import com.techne.boot.constants.TechneDatePattern;

import java.lang.reflect.Type;
import java.time.MonthDay;

/**
 * MonthDayTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class MonthDayTypeAdapter implements JsonSerializer<MonthDay>, JsonDeserializer<MonthDay> {

    @Override
    public JsonElement serialize(MonthDay monthDay, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(monthDay.format(TechneDatePattern.NORM_MONTH_DAY_FORMATTER));
    }

    @Override
    public MonthDay deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return MonthDay.parse(jsonElement.getAsString(), TechneDatePattern.NORM_MONTH_DAY_FORMATTER);
    }

}