package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.InputEntity;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class SqlTypeSource {
    public SqlTypeSource(
            SqlConnector connector,
            String schemaName,
            DatabaseNamingStrategy databaseNamingStrategy
    ) {

    }
    public <T extends InputEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass) {
        return null;
    }
}
