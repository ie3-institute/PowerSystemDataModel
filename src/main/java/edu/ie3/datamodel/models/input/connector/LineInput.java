/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput;
import edu.ie3.datamodel.utils.GridAndGeoUtils;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.LineString;
import tec.uom.se.ComparableQuantity;

/**
 * Describes an electrical grid line that connects two {@link
 * edu.ie3.datamodel.models.input.NodeInput}s
 */
public class LineInput extends ConnectorInput implements HasType {

  /** Type of this line, containing default values for lines of this kind */
  private final LineTypeInput type;
  /** Length of this line */
  private final ComparableQuantity<Length> length;
  /** Coordinates of this line */
  private final LineString geoPosition;
  /** Description of an optional weather dependent operation curve */
  private final OlmCharacteristicInput olmCharacteristic;

  /**
   * Constructor for an operated line
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
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
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      LineTypeInput type,
      ComparableQuantity<Length> length,
      LineString geoPosition,
      OlmCharacteristicInput olmCharacteristic) {
    super(uuid, id, operator, operationTime, nodeA, nodeB, parallelDevices);
    this.type = type;
    this.length = length.to(StandardUnits.LINE_LENGTH);
    this.geoPosition = GridAndGeoUtils.buildSafeLineString(geoPosition);
    this.olmCharacteristic = olmCharacteristic;
  }

  /**
   * Constructor for an operated, always on line
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
      ComparableQuantity<Length> length,
      LineString geoPosition,
      OlmCharacteristicInput olmCharacteristic) {
    super(uuid, id, nodeA, nodeB, parallelDevices);
    this.type = type;
    this.length = length;
    this.geoPosition = GridAndGeoUtils.buildSafeLineString(geoPosition);
    this.olmCharacteristic = olmCharacteristic;
  }

  @Override
  public LineTypeInput getType() {
    return type;
  }

  public ComparableQuantity<Length> getLength() {
    return length;
  }

  public LineString getGeoPosition() {
    return geoPosition;
  }

  public OlmCharacteristicInput getOlmCharacteristic() {
    return olmCharacteristic;
  }

  public LineInputCopyBuilder copy() {
    return new LineInputCopyBuilder(this);
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

  @Override
  public String toString() {
    return "LineInput{"
        + "uuid="
        + getUuid()
        + ", id='"
        + getId()
        + '\''
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", nodeA="
            + getNodeA().getUuid()
            + ", nodeB="
            + getNodeB().getUuid()
            + ", noOfParallelDevices="
            + getParallelDevices()
            + ", type="
            + type.getUuid()
            + ", length="
            + length
            + ", geoPosition="
            + geoPosition
            + ", olmCharacteristic="
            + olmCharacteristic
            + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link LineInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link LineInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class LineInputCopyBuilder extends ConnectorInputCopyBuilder<LineInputCopyBuilder> {
    private LineTypeInput type;
    private ComparableQuantity<Length> length;
    private LineString geoPosition;
    private OlmCharacteristicInput olmCharacteristic;

    private LineInputCopyBuilder(LineInput entity) {
      super(entity);
      this.type = entity.getType();
      this.length = entity.getLength();
      this.geoPosition = entity.getGeoPosition();
      this.olmCharacteristic = entity.getOlmCharacteristic();
    }

    @Override
    public LineInput build() {
      return new LineInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNodeA(),
          getNodeB(),
          getParallelDevices(),
          type,
          length,
          geoPosition,
          olmCharacteristic);
    }

    public LineInputCopyBuilder geoPosition(LineString geoPosition) {
      this.geoPosition = geoPosition;
      return this;
    }

    public LineInputCopyBuilder type(LineTypeInput type) {
      this.type = type;
      return this;
    }

    public LineInputCopyBuilder length(ComparableQuantity<Length> length) {
      this.length = length;
      return this;
    }

    public LineInputCopyBuilder olmCharacteristic(OlmCharacteristicInput olmCharacteristic) {
      this.olmCharacteristic = olmCharacteristic;
      return this;
    }

    @Override
    protected LineInputCopyBuilder childInstance() {
      return this;
    }
  }
}
