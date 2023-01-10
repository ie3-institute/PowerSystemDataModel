package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.InputEntity;

import java.util.Map;
import java.util.stream.Stream;

public class SqlTypeSource extends SqlDataSource implements TypeSource {

    public SqlTypeSource(
            String jdbcUrl,
            String userName,
            String password,
            String schemaName,
            DatabaseNamingStrategy databaseNamingStrategy
    ) {
        super(jdbcUrl, userName, password, schemaName);
    }
    public <T extends InputEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass) {
        return buildStreamByQuery(entityClass, connector);
    }





}
