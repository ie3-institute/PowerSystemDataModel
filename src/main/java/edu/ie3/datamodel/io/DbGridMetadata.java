package edu.ie3.datamodel.io;

import java.util.UUID;
import java.util.stream.Stream;

import static edu.ie3.datamodel.io.IoUtil.quote;

/**
 * Class for identification of entities and results from grids in SQL databases.
 */
public record DbGridMetadata(String gridName, UUID uuid) {

    public static final String GRID_NAME = "grid_name";
    public static final String GRID_UUID = "grid_uuid";

    public String toString() {
        return GRID_NAME + "=" + gridName + ", " + GRID_UUID + "=" + uuid.toString();
    }

    public Stream<String> getStreamForQuery() {
        return Stream.of(quote(uuid.toString(), "'"));
    }

}
