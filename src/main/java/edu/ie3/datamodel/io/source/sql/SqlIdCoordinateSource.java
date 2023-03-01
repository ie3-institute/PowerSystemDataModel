package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.source.IdCoordinateSource;

public class SqlIdCoordinateSource extends IdCoordinateSource {

    public SqlIdCoordinateSource(
            SqlDataSource sqlDataSource,
            IdCoordinateFactory factory
    ) {
        super(factory, sqlDataSource);
    }

    public SqlIdCoordinateSource(
            SqlConnector connector,
            String schemaName,
            DatabaseNamingStrategy namingStrategy,
            IdCoordinateFactory factory
    ) {
        super(factory, new SqlDataSource(connector, schemaName, namingStrategy));
    }
}
