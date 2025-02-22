package com.alibaba.fastjson2.writer;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.codec.DateTimeCodec;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

final class ObjectWriterImplZonedDateTime
        extends DateTimeCodec
        implements ObjectWriter {
    static final ObjectWriterImplZonedDateTime INSTANCE = new ObjectWriterImplZonedDateTime(null);
    static final ObjectWriterImplZonedDateTime INSTANCE_UNIXTIME = new ObjectWriterImplZonedDateTime("unixtime");

    public ObjectWriterImplZonedDateTime(String format) {
        super(format);
    }

    @Override
    public void writeJSONB(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        jsonWriter.writeZonedDateTime((ZonedDateTime) object);
    }

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }

        ZonedDateTime zdt = (ZonedDateTime) object;

        JSONWriter.Context ctx = jsonWriter.getContext();

        if (formatUnixTime || ctx.isDateFormatUnixTime()) {
            long millis = zdt.toInstant().toEpochMilli();
            jsonWriter.writeInt64(millis / 1000);
            return;
        }

        if (formatMillis || ctx.isDateFormatMillis()) {
            jsonWriter.writeInt64(zdt
                    .toInstant()
                    .toEpochMilli());
            return;
        }

        if (formatISO8601 || ctx.isDateFormatISO8601()) {
            jsonWriter.writeDateTimeISO8601(
                    zdt.getYear(),
                    zdt.getMonthValue(),
                    zdt.getDayOfMonth(),
                    zdt.getHour(),
                    zdt.getMinute(),
                    zdt.getSecond(),
                    zdt.getNano() / 1000_000,
                    zdt.getOffset().getTotalSeconds()
            );
            return;
        }

        DateTimeFormatter formatter = this.getDateFormatter();
        if (formatter == null) {
            formatter = ctx.getDateFormatter();
        }

        if (formatter == null) {
            jsonWriter.writeZonedDateTime(zdt);
        } else {
            String str = formatter.format(zdt);
            jsonWriter.writeString(str);
        }
    }
}
