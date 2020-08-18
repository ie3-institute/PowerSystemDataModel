package edu.ie3.datamodel.io.factory.deserializing;

import java.time.ZonedDateTime;
import java.util.Optional;

public class ZonedDateTimeDeserializingStrategy implements SingleValueDeserializingStrategy<ZonedDateTime> {

    @Override
    public Optional<ZonedDateTime> deserialize(String valueToDeserialize) {
        return Optional.ofNullable(ZonedDateTime.parse(valueToDeserialize));
    }

    @Override
    public Class<ZonedDateTime> getType() {
        return ZonedDateTime.class;
    }
}
