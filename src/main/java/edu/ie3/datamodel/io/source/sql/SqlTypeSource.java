package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class SqlTypeSource extends SqlDataSource implements TypeSource {

    private final DatabaseNamingStrategy databaseNamingStrategy;

    public SqlTypeSource(
            SqlConnector connector,
            String schemaName,
            DatabaseNamingStrategy databaseNamingStrategy
    ) {
        super(connector, schemaName);
        this.databaseNamingStrategy = databaseNamingStrategy;
    }
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
}
