/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput;
import edu.ie3.datamodel.utils.GridAndGeoUtils;
import org.locationtech.jts.geom.LineString;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Length;
import java.util.Map;
import java.util.UUID;

public class LineInputFactory extends ConnectorInputEntityFactory<LineInput> {

  private final Map<UUID, LineTypeInput> types;

  public LineInputFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, LineTypeInput> types) {
    super(operators, nodes, LineInput.class);
    this.types = types;
  }

  @Override
  protected LineInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operator,
      OperationTime operationTime) {
    int parallelDevices = getInt(data, PARALLEL_DEVICES);
    LineTypeInput type = getType(data, types);
    ComparableQuantity<Length> length = getQuantity(data, LENGTH, StandardUnits.LINE_LENGTH);
    LineString geoPosition =
        getLineString(data, GEO_POSITION)
            .orElse(GridAndGeoUtils.buildSafeLineStringBetweenNodes(nodeA, nodeB));
    OlmCharacteristicInput olmCharacteristic;

    if (!isFieldEmpty(data, OLM_CHARACTERISTIC)) {
      String value = getField(data, OLM_CHARACTERISTIC);

      try {
        olmCharacteristic = new OlmCharacteristicInput(value);
      } catch (ParsingException e) {
        throw new FactoryException(
            "Cannot parse the following overhead line monitoring characteristic: '" + value + "'",
            e);
      }

    } else {
      olmCharacteristic = OlmCharacteristicInput.CONSTANT_CHARACTERISTIC;
    }

    return new LineInput(
        uuid,
        id,
        operator,
        operationTime,
        nodeA,
        nodeB,
        parallelDevices,
        type,
        length,
        geoPosition,
        olmCharacteristic,
        data);
  }
}
