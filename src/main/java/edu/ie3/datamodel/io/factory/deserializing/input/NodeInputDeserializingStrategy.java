package edu.ie3.datamodel.io.factory.deserializing.input;

import edu.ie3.datamodel.io.factory.deserializing.SimpleEntityDeserializingStrategy;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import org.locationtech.jts.geom.Point;
import tec.uom.se.ComparableQuantity;

import javax.measure.quantity.Dimensionless;
import java.util.UUID;

public class NodeInputDeserializingStrategy extends SimpleEntityDeserializingStrategy<NodeInput> {

    private NodeInputDeserializingStrategy() {
        super(NodeInput.class);
    }


}
