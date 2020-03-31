/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.characteristic.LineCharacteristicInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.LineString;

/**
 * Describes an electrical grid line that connects two {@link
 * edu.ie3.datamodel.models.input.NodeInput}s
 */
public class LineInput extends ConnectorInput {

  /** Type of this line, containing default values for lines of this kind */
  private final LineTypeInput type;
  /** Length of this line */
  private final Quantity<Length> length;
  /** Coordinates of this line */
  private final LineString geoPosition;
  /** Description of an optional weather dependent operation curve */
  private final LineCharacteristicInput characteristic;

  /**
   * Constructor for an operated line
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA Grid node at one side of the line
   * @param nodeB Grid node at the other side of the line
   * @param parallelDevices Amount of parallel lines
   * @param type of line
   * @param length of this line
   * @param geoPosition Coordinates of this line
   * @param characteristic Description of a weather dependent operation curve
   */
  public LineInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      LineTypeInput type,
      Quantity<Length> length,
      LineString geoPosition,
      LineCharacteristicInput characteristic) {
    super(uuid, operationTime, operator, id, nodeA, nodeB, parallelDevices);
    this.type = type;
    this.length = length.to(StandardUnits.LINE_LENGTH);
    this.geoPosition = geoPosition;
    this.characteristic = characteristic;
  }

  /**
   * Constructor for an operated line without a weather dependent operation curve
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA Grid node at one side of the line
   * @param nodeB Grid node at the other side of the line
   * @param parallelDevices Amount of parallel lines
   * @param type of line
   * @param length of this line
   * @param geoPosition Coordinates of this line
   */
  public LineInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      LineTypeInput type,
      Quantity<Length> length,
      LineString geoPosition) {
    super(uuid, operationTime, operator, id, nodeA, nodeB, parallelDevices);
    this.type = type;
    this.length = length.to(StandardUnits.LINE_LENGTH);
    this.geoPosition = geoPosition;
    this.characteristic = new LineCharacteristicInput(UUID.randomUUID());
  }

  /**
   * Constructor for a non-operated line
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA Grid node at one side of the line
   * @param nodeB Grid node at the other side of the line
   * @param parallelDevices Amount of parallel lines
   * @param type of line
   * @param length of this line
   * @param geoPosition Coordinates of this line
   * @param characteristic Description of a weather dependent operation curve
   */
  public LineInput(
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      LineTypeInput type,
      Quantity<Length> length,
      LineString geoPosition,
      LineCharacteristicInput characteristic) {
    super(uuid, id, nodeA, nodeB, parallelDevices);
    this.type = type;
    this.length = length;
    this.geoPosition = geoPosition;
    this.characteristic = characteristic;
  }

  /**
   * Constructor for a non-operated line without a weather dependent operation curve
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA Grid node at one side of the line
   * @param nodeB Grid node at the other side of the line
   * @param parallelDevices Amount of parallel lines
   * @param type of line
   * @param length of this line
   * @param geoPosition Coordinates of this line
   */
  public LineInput(
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      LineTypeInput type,
      Quantity<Length> length,
      LineString geoPosition) {
    super(uuid, id, nodeA, nodeB, parallelDevices);
    this.type = type;
    this.length = length;
    this.geoPosition = geoPosition;
    this.characteristic = new LineCharacteristicInput(UUID.randomUUID());
  }

  public LineTypeInput getType() {
    return type;
  }

  public Quantity<Length> getLength() {
    return length;
  }

  public LineString getGeoPosition() {
    return geoPosition;
  }

  public LineCharacteristicInput getOlmCharacteristic() {
    return characteristic;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LineInput lineInput = (LineInput) o;
    return type.equals(lineInput.type)
        && length.equals(lineInput.length)
        && geoPosition.equals(lineInput.geoPosition)
        && characteristic.equals(lineInput.characteristic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, length, geoPosition, characteristic);
  }

  @Override
  public String toString() {
    return "LineInput{"
        + "type="
        + type
        + ", length="
        + length
        + ", geoPosition="
        + geoPosition
        + ", characteristic="
        + characteristic
        + '}';
  }
}
