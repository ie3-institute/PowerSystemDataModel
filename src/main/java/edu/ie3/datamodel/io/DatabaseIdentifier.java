package edu.ie3.datamodel.io;

import java.util.UUID;
import java.util.stream.Stream;

import static edu.ie3.datamodel.io.IoUtil.quote;

/**
 * Class for identification of grids and results in SQL databases.
 */
public class DatabaseIdentifier {

    private final String identifier;
    private final UUID uuid;

    public DatabaseIdentifier(
            String identifier,
            UUID uuid
    ) {
        this.identifier = identifier;
        this.uuid = uuid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String toString() {
        return "identifier=" + identifier + ", uuid=" + uuid.toString();
    }

    public String[] getQueryString() {
        return new String[]{quote(identifier, "'"), quote(uuid.toString(), "'")};
    }

    public Stream<String> getStreamForQuery() {
        return Stream.concat(Stream.of(quote(identifier, "'")), Stream.of(quote(uuid.toString(), "'")));
    }
}
