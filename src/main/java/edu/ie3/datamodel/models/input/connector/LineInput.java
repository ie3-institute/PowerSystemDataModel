/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput;
import edu.ie3.datamodel.utils.QuantityUtils;
import edu.ie3.util.geo.GeoUtils;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.LineString;
import tech.units.indriya.ComparableQuantity;

/** Describes an electrical grid line that connects two {@link NodeInput}s. */
public class LineInput extends ConnectorInput implements HasType {
  /** Type of this line, containing default values for lines of this kind. */
  private final LineTypeInput type;

  /** Length of this line. */
  private final ComparableQuantity<Length> length;

  /** Coordinates of this line. */
  private final LineString geoPosition;

  /** Description of an optional weather dependent operation curve. */
  private final OlmCharacteristicInput olmCharacteristic;

  /**
   * Constructor for an operated line.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA Grid node at one side of the line
   * @param nodeB Grid node at the other side of the line
   * @param parallelDevices overall amount of parallel lines to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two lines using the specified parameters)
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
    this.length = length;
    this.geoPosition = GeoUtils.buildSafeLineString(geoPosition);
    this.olmCharacteristic = olmCharacteristic;
  }

  /**
   * Constructor for an operated line.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA Grid node at one side of the line
   * @param nodeB Grid node at the other side of the line
   * @param parallelDevices overall amount of parallel lines to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two lines using the specified parameters)
   * @param type of line
   * @param length of this line
   * @param geoPosition Coordinates of this line
   * @param olmCharacteristic Description of an optional weather dependent operation curve
   * @param additionalInformation That were provided by the source
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
      OlmCharacteristicInput olmCharacteristic,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, nodeA, nodeB, parallelDevices);
    this.type = type;
    this.length = length;
    this.geoPosition = GeoUtils.buildSafeLineString(geoPosition);
    this.olmCharacteristic = olmCharacteristic;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated line.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA Grid node at one side of the line
   * @param nodeB Grid node at the other side of the line
   * @param parallelDevices overall amount of parallel lines to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two lines using the specified parameters)
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
    this.geoPosition = GeoUtils.buildSafeLineString(geoPosition);
    this.olmCharacteristic = olmCharacteristic;
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LineInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(type, that.type)
        && QuantityUtils.equals(length, that.length)
        && Objects.equals(geoPosition, that.geoPosition)
        && Objects.equals(olmCharacteristic, that.olmCharacteristic);
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
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", nodeA="
        + getNodeA().getUuid()
        + ", nodeB="
        + getNodeB().getUuid()
        + ", parallelDevices="
        + getParallelDevices()
        + ", type="
        + type.getUuid()
        + ", length="
        + length
        + ", geoPosition="
        + geoPosition
        + ", olmCharacteristic="
        + olmCharacteristic
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public LineInputCopyBuilder copy() {
    return new LineInputCopyBuilder(this);
  }

  public static class LineInputCopyBuilder extends ConnectorInputCopyBuilder<LineInputCopyBuilder> {
    private LineTypeInput type;

    private ComparableQuantity<Length> length;

    private LineString geoPosition;

    private OlmCharacteristicInput olmCharacteristic;

    protected LineInputCopyBuilder(LineInput entity) {
      super(entity);
      this.type = entity.type;
      this.length = entity.length;
      this.geoPosition = entity.geoPosition;
      this.olmCharacteristic = entity.olmCharacteristic;
    }

    public LineInputCopyBuilder type(LineTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    protected LineTypeInput getType() {
      return type;
    }

    public LineInputCopyBuilder length(ComparableQuantity<Length> length) {
      this.length = length;
      return thisInstance();
    }

    protected ComparableQuantity<Length> getLength() {
      return length;
    }

    public LineInputCopyBuilder geoPosition(LineString geoPosition) {
      this.geoPosition = geoPosition;
      return thisInstance();
    }

    protected LineString getGeoPosition() {
      return geoPosition;
    }

    public LineInputCopyBuilder olmCharacteristic(OlmCharacteristicInput olmCharacteristic) {
      this.olmCharacteristic = olmCharacteristic;
      return thisInstance();
    }

    protected OlmCharacteristicInput getOlmCharacteristic() {
      return olmCharacteristic;
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
          olmCharacteristic,
          getAdditionalInformation());
    }

    @Override
    protected LineInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
