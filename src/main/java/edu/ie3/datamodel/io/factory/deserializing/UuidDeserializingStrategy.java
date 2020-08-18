package edu.ie3.datamodel.io.factory.deserializing;

import java.util.Optional;
import java.util.UUID;

public class UuidDeserializingStrategy implements SingleValueDeserializingStrategy<UUID>{

    @Override
    public Optional<UUID> deserialize(String valueToDeserialize) {
        return Optional.of(UUID.fromString(valueToDeserialize));
    }

    @Override
    public Class<UUID> getType() {
        return UUID.class;
    }
}
