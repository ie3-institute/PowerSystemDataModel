/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.type.LineTypeInput;
import java.util.Optional;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.LineString;

public class LineInputFactory extends ConnectorInputEntityFactory<LineInput, LineInputEntityData> {
  private static final String LENGTH = "length";
  private static final String GEO_POSITION = "geoposition";
  private static final String OLM_CHARACTERISTIC = "olmcharacteristic";

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {PARALLEL_DEVICES, LENGTH, GEO_POSITION, OLM_CHARACTERISTIC};
  }

  @Override
  protected LineInput buildModel(
      LineInputEntityData data,
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    final int parallelDevices = data.getInt(PARALLEL_DEVICES);
    final LineTypeInput type = data.getType();
    final Quantity<Length> length = data.getQuantity(LENGTH, StandardUnits.LINE_LENGTH);
    final LineString geoPosition = data.getLineString(GEO_POSITION).orElse(null);
    final Optional<String> olmCharacteristic =
        data.containsKey(OLM_CHARACTERISTIC)
            ? Optional.of(data.getField(OLM_CHARACTERISTIC))
            : Optional.empty();
    return new LineInput(
        uuid,
        operationTime,
        operatorInput,
        id,
        nodeA,
        nodeB,
        parallelDevices,
        type,
        length,
        geoPosition,
        olmCharacteristic);
  }

  @Override
  protected LineInput buildModel(
      LineInputEntityData data, UUID uuid, String id, NodeInput nodeA, NodeInput nodeB) {
    final int parallelDevices = data.getInt(PARALLEL_DEVICES);
    final LineTypeInput type = data.getType();
    final Quantity<Length> length = data.getQuantity(LENGTH, StandardUnits.LINE_LENGTH);
    final LineString geoPosition = data.getLineString(GEO_POSITION).orElse(null);
    final Optional<String> olmCharacteristic =
        data.containsKey(OLM_CHARACTERISTIC)
            ? Optional.of(data.getField(OLM_CHARACTERISTIC))
            : Optional.empty();
    return new LineInput(
        uuid, id, nodeA, nodeB, parallelDevices, type, length, geoPosition, olmCharacteristic);
  }
}
