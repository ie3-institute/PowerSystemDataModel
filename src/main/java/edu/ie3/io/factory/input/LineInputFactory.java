package edu.ie3.io.factory.input;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.connector.LineInput;

import java.util.UUID;

public class LineInputFactory extends ConnectorInputEntityFactory<LineInput, ConnectorInputEntityData> {

    @Override
    protected String[] getAdditionalFields() {
        return new String[0];
    }

    @Override
    protected LineInput buildModel(ConnectorInputEntityData data, UUID uuid, String id, NodeInput nodeA, NodeInput nodeB, int parallelDevices, OperatorInput operatorInput, OperationTime operationTime) {
        return null;
    }

    @Override
    protected LineInput buildModel(ConnectorInputEntityData data, UUID uuid, String id, NodeInput nodeA, NodeInput nodeB, int parallelDevices) {
        return null;
    }

}
