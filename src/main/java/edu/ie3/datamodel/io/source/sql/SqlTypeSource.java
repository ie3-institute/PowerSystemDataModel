package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.source.TypeSource;

public class SqlTypeSource extends TypeSource {
    public SqlTypeSource(
            SqlConnector connector,
            String schemaName,
            DatabaseNamingStrategy databaseNamingStrategy
    ) {
        super(new SqlDataSource(connector, schemaName, databaseNamingStrategy));
    }
    /*
    public <T extends InputEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass) {
        String query = createBaseQueryString(getSchemaName(), entityClass.getSimpleName());
        try (PreparedStatement ps = connector.getConnection().prepareStatement(query)) {
            return buildStreamByQuery(TimeBasedValue.class, a -> {}, ps);
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    protected Optional createEntity(Map<String, String> fieldToValues) {
        return Optional.empty();
    }
     */
}
