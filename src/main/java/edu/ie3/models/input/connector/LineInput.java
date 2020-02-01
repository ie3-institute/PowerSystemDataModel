/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.connector;

import com.vividsolutions.jts.geom.LineString;
import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.connector.type.LineTypeInput;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Describes an electrical grid line that connects two {@link edu.ie3.models.input.NodeInput}s */
public class LineInput extends ConnectorInput {

  /** Type of this line, containing default values for lines of this kind */
  private LineTypeInput type;
  /** Length of this line */
  private Quantity<Length> length;
  /** Coordinates of this line */
  private LineString geoPosition;
  /** Description of an optional weather dependent operation curve */
  private Optional<String> olmCharacteristic;

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
   * @param olmCharacteristic Description of an optional weather dependent operation curve
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
      Optional<String> olmCharacteristic) {
    super(uuid, operationTime, operator, id, nodeA, nodeB, parallelDevices);
    this.type = type;
    this.length = length;
    this.geoPosition = geoPosition;
    this.olmCharacteristic = olmCharacteristic;
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
   * @param olmCharacteristic Description of an optional weather dependent operation curve
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
      Optional<String> olmCharacteristic) {
    super(uuid, id, nodeA, nodeB, parallelDevices);
    this.type = type;
    this.length = length;
    this.geoPosition = geoPosition;
    this.olmCharacteristic = olmCharacteristic;
  }

  public LineTypeInput getType() {
    return type;
  }

  public void setType(LineTypeInput type) {
    this.type = type;
  }

  public Quantity<Length> getLength() {
    return length;
  }

  public void setLength(Quantity<Length> length) {
    this.length = length;
  }

  public LineString getGeoPosition() {
    return geoPosition;
  }

  public void setGeoPosition(LineString geoPosition) {
    this.geoPosition = geoPosition;
  }

  public Optional<String> getOlm() {
    return olmCharacteristic;
  }

  public void setOlm(Optional<String> olmCharacteristic) {
    this.olmCharacteristic = olmCharacteristic;
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
        && olmCharacteristic.equals(lineInput.olmCharacteristic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, length, geoPosition, olmCharacteristic);
  }
}
