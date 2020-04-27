/*
 * © 2020. TU Dortmund University,
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
import java.util.UUID;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.LineString;
import tec.uom.se.ComparableQuantity;

public class LineInputFactory
    extends ConnectorInputEntityFactory<LineInput, TypedConnectorInputEntityData<LineTypeInput>> {
  private static final String LENGTH = "length";
  private static final String GEO_POSITION = "geoposition";
  private static final String OLM_CHARACTERISTIC = "olmcharacteristic";

  public LineInputFactory() {
    super(LineInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {PARALLEL_DEVICES, LENGTH, GEO_POSITION, OLM_CHARACTERISTIC};
  }

  @Override
  protected LineInput buildModel(
      TypedConnectorInputEntityData<LineTypeInput> data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operator,
      OperationTime operationTime) {
    final int parallelDevices = data.getInt(PARALLEL_DEVICES);
    final LineTypeInput type = data.getType();
    final ComparableQuantity<Length> length = data.getQuantity(LENGTH, StandardUnits.LINE_LENGTH);
    final LineString geoPosition =
        data.getLineString(GEO_POSITION)
            .orElse(GridAndGeoUtils.buildLineStringBetweenNodes(nodeA, nodeB));
    final OlmCharacteristicInput olmCharacteristic;
    try {
      olmCharacteristic =
          data.containsKey(OLM_CHARACTERISTIC) && !data.getField(OLM_CHARACTERISTIC).isEmpty()
              ? new OlmCharacteristicInput(data.getField(OLM_CHARACTERISTIC))
              : OlmCharacteristicInput.CONSTANT_CHARACTERISTIC;
    } catch (ParsingException e) {
      throw new FactoryException(
          "Cannot parse the following overhead line monitoring characteristic: '"
              + data.getField(OLM_CHARACTERISTIC)
              + "'",
          e);
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
        olmCharacteristic);
  }
}
