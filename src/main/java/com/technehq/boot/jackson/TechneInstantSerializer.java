package com.technehq.boot.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializerBase;
import com.technehq.boot.constants.TechneConstants;
import org.springframework.boot.jackson.JsonComponent;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * TechneInstantSerializer
 *
 * @author 七濑武【Nanase Takeshi】
 */
@JsonComponent
public class TechneInstantSerializer extends InstantSerializerBase<Instant> {

    /**
     * 实例
     */
    public static final TechneInstantSerializer INSTANCE = new TechneInstantSerializer();

    /**
     * 构造方法
     */
    protected TechneInstantSerializer() {
        super(Instant.class, Instant::toEpochMilli, Instant::getEpochSecond, Instant::getNano, TechneConstants.INSTANT_FORMATTER);
    }

    /**
     * 构造方法
     *
     * @param base         base
     * @param useTimestamp useTimestamp
     * @param formatter    formatter
     * @param shape        shape
     */
    protected TechneInstantSerializer(TechneInstantSerializer base, Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        super(base, useTimestamp, base._useNanoseconds, formatter, shape);
    }

    @Override
    protected InstantSerializerBase<Instant> withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new TechneInstantSerializer(this, useTimestamp, formatter, shape);
    }

}
