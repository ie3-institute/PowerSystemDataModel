/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import tec.uom.se.ComparableQuantity;

/** Describes an electrical grid node, that other assets can connect to */
public class NodeInput extends AssetInput {
  /** Target voltage magnitude of the node with regard to its rated voltage (typically in p.u.) */
  private final ComparableQuantity<Dimensionless> vTarget;
  /** Is this node a slack node? */
  private final boolean slack;
  /**
   * The coordinates of this node, especially relevant for geo-dependant systems, that are connected
   * to this node
   */
  private final Point geoPosition;

  /** Use this default value if geoPosition is unknown */
  public static final Point DEFAULT_GEO_POSITION =
      new GeometryFactory().createPoint(new Coordinate(51.4843281, 7.4116482));

  /** Voltage level of this node */
  private final VoltageLevel voltLvl;
  /** Subnet of this node */
  private final int subnet;
  /**
   * Constructor for an operated node
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param vTarget Target voltage magnitude of the node with regard to its rated voltage
   * @param slack Is this node a slack node?
   * @param geoPosition Coordinates of this node, especially relevant for geo-dependant systems,
   *     that are connected to this node
   * @param voltLvl Voltage level of this node
   * @param subnet of this node
   */
  public NodeInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ComparableQuantity<Dimensionless> vTarget,
      boolean slack,
      Point geoPosition,
      VoltageLevel voltLvl,
      int subnet) {
    super(uuid, id, operator, operationTime);
    this.vTarget = vTarget.to(StandardUnits.TARGET_VOLTAGE_MAGNITUDE);
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
  }

  /**
   * Constructor for an operated, always on asset
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param vTarget Target voltage magnitude of the node with regard to its rated voltage
   * @param slack Is this node a slack node?
   * @param geoPosition Coordinates of this node, especially relevant for geo-dependant systems,
   *     that are connected to this node
   * @param voltLvl Voltage level of this node
   * @param subnet of this node
   */
  public NodeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Dimensionless> vTarget,
      boolean slack,
      Point geoPosition,
      VoltageLevel voltLvl,
      int subnet) {
    super(uuid, id);
    this.vTarget = vTarget.to(StandardUnits.TARGET_VOLTAGE_MAGNITUDE);
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
  }

  public ComparableQuantity<Dimensionless> getvTarget() {
    return vTarget;
  }

  public boolean isSlack() {
    return slack;
  }

  public Point getGeoPosition() {
    return geoPosition;
  }

  public VoltageLevel getVoltLvl() {
    return voltLvl;
  }

  public int getSubnet() {
    return subnet;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    NodeInput nodeInput = (NodeInput) o;
    return slack == nodeInput.slack
        && subnet == nodeInput.subnet
        && Objects.equals(vTarget, nodeInput.vTarget)
        && Objects.equals(geoPosition, nodeInput.geoPosition)
        && Objects.equals(voltLvl, nodeInput.voltLvl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), vTarget, slack, geoPosition, voltLvl, subnet);
  }

  @Override
  public String toString() {
    return "NodeInput{"
        + "vTarget="
        + vTarget
        + ", slack="
        + slack
        + ", geoPosition="
        + geoPosition
        + ", voltLvl="
        + voltLvl
        + ", subnet="
        + subnet
        + '}';
  }
}
