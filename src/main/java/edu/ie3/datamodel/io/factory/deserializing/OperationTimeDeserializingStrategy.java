package edu.ie3.datamodel.io.factory.deserializing;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.utils.FieldNameUtil;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class OperationTimeDeserializingStrategy implements DeserializingStrategy<OperationTime> {

    private static final Class<OperationTime> type = OperationTime.class;

    private static final String[] constructorFields = FieldNameUtil.getConstructorFields(type);
    private static final String OPERATES_FROM = constructorFields [0];
    private static final String OPERATES_UNTIL = constructorFields [1];


    @Override
    public Optional<OperationTime> deserialize(Map<String, String> fieldMap) {
        return deserialize(fieldMap, Collections.emptyMap());
    }

    @Override
    public Optional<OperationTime> deserialize(Map<String, String> fieldMap, Map<String, Object> deserializedFields) {
        ZonedDateTime start = null;
        ZonedDateTime end = null;
        if(deserializedFields.containsKey(OPERATES_FROM)) start = (ZonedDateTime) deserializedFields.get(OPERATES_FROM);
        else if (fieldMap.containsKey(OPERATES_FROM)) start = Deserializer.deserialize(ZonedDateTime.class, Collections.singletonMap(OPERATES_FROM, fieldMap.get(OPERATES_FROM))).orElse(null);
        if(deserializedFields.containsKey(OPERATES_UNTIL)) end = (ZonedDateTime) deserializedFields.get(OPERATES_UNTIL);
        else if (fieldMap.containsKey(OPERATES_UNTIL)) end = Deserializer.deserialize(ZonedDateTime.class, Collections.singletonMap(OPERATES_UNTIL, fieldMap.get(OPERATES_UNTIL))).orElse(null);
        return Optional.of(OperationTime.builder().withStart(start).withEnd(end).build());
    }

    @Override
    public Class<OperationTime> getType() {
        return type;
    }
}
