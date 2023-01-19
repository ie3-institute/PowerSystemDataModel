package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.source.ExtraDataSource;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class SqlRawGridSource extends RawGridSource {

    private final SqlDataSource sqlDataSource;



    // general fields
    private final TypeSource typeSource;

    // factories
    private final NodeInputFactory nodeInputFactory;
    private final LineInputFactory lineInputFactory;
    private final Transformer2WInputFactory transformer2WInputFactory;
    private final Transformer3WInputFactory transformer3WInputFactory;
    private final SwitchInputFactory switchInputFactory;
    private final MeasurementUnitInputFactory measurementUnitInputFactory;

    private final DatabaseNamingStrategy databaseNamingStrategy;

    public SqlRawGridSource(
            SqlConnector connector,
            String schemaName,
            DatabaseNamingStrategy databaseNamingStrategy
    ) {
        super(connector, schemaName);
        this.databaseNamingStrategy = databaseNamingStrategy;
    }

    Optional<RawGridElements> getGridData() {
        return null;
    }

    Set<NodeInput> getNodes() {
        return null;
    }

    Set<NodeInput> getNodes(Set<OperatorInput> operators) {
        return null;
    }

    Set<LineInput> getLines() {
        return null;
    }

    Set<LineInput> getLines(Set<NodeInput> nodes, Set<LineTypeInput> lineTypeInputs, Set<OperatorInput> operators) {
        return null;
    }

    Set<Transformer2WInput> get2WTransformers() {
        return null;
    }

    Set<Transformer2WInput> get2WTransformers(
            Set<NodeInput> nodes,
            Set<Transformer2WTypeInput> transformer2WTypes,
            Set<OperatorInput> operators) {
        return null;
    }

    Set<Transformer3WInput> get3WTransformers() {
        return null;
    }

    Set<Transformer3WInput> get3WTransformers(
            Set<NodeInput> nodes,
            Set<Transformer3WTypeInput> transformer3WTypeInputs,
            Set<OperatorInput> operators) {
        return null;
    }

    Set<SwitchInput> getSwitches() { return null; }

    Set<SwitchInput> getSwitches(Set<NodeInput> nodes, Set<OperatorInput> operators) {
        return null;
    }

    Set<MeasurementUnitInput> getMeasurementUnits() { return null; }

    Set<MeasurementUnitInput> getMeasurementUnits(Set<NodeInput> nodes, Set<OperatorInput> operators) { return null; }

    public <T extends InputEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass) {
        String query = createBaseQueryString(getSchemaName(), entityClass.getSimpleName());
        try (PreparedStatement ps = connector.getConnection().prepareStatement(query)) {
            return buildStreamByQuery(TimeBasedValue.class, a -> {}, ps);
        } catch (SQLException e) {
            return null;
        }
    }






}
