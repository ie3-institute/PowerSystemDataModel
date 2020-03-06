/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input;

import edu.ie3.models.CommonVoltageLevel;
import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;
import org.locationtech.jts.geom.Point;

/** Describes an electrical grid node, that other assets can connect to */
public class NodeInput extends AssetInput {
  /** Target voltage magnitude of the node with regard to its rated voltage (typically in p.u.) */
  private final Quantity<Dimensionless> vTarget;
  /** Rated voltage magnitude of the node (typically in kV) */
  private final Quantity<ElectricPotential> vRated;
  /** Is this node a slack node? */
  private final boolean slack;
  /**
   * The coordinates of this node, especially relevant for geo-dependant systems, that are connected
   * to this node
   */
  private final Point geoPosition;
  /** Voltage level of this node */
  private final CommonVoltageLevel voltLvl;
  /** Subnet of this node */
  private final int subnet;
  /**
   * Constructor for an operated node
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param vTarget Target voltage magnitude of the node with regard to its rated voltage
   * @param vRated Rated voltage magnitude of the node
   * @param slack Is this node a slack node?
   * @param geoPosition Coordinates of this node, especially relevant for geo-dependant systems,
   *     that are connected to this node
   * @param voltLvl Voltage level of this node
   * @param subnet of this node
   */
  public NodeInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      Quantity<Dimensionless> vTarget,
      Quantity<ElectricPotential> vRated,
      boolean slack,
      Point geoPosition,
      CommonVoltageLevel voltLvl,
      int subnet) {
    super(uuid, operationTime, operator, id);
    this.vTarget = vTarget.to(StandardUnits.TARGET_VOLTAGE_MAGNITUDE);
    this.vRated = vRated.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
  }

  /**
   * Constructor for a non-operated asset
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param vTarget Target voltage magnitude of the node with regard to its rated voltage
   * @param vRated Rated voltage magnitude of the node
   * @param slack Is this node a slack node?
   * @param geoPosition Coordinates of this node, especially relevant for geo-dependant systems,
   *     that are connected to this node
   * @param voltLvl Voltage level of this node
   * @param subnet of this node
   */
  public NodeInput(
      UUID uuid,
      String id,
      Quantity<Dimensionless> vTarget,
      Quantity<ElectricPotential> vRated,
      boolean slack,
      Point geoPosition,
      CommonVoltageLevel voltLvl,
      int subnet) {
    super(uuid, id);
    this.vTarget = vTarget.to(StandardUnits.TARGET_VOLTAGE_MAGNITUDE);
    this.vRated = vRated.to(StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
  }

  public Quantity<Dimensionless> getvTarget() {
    return vTarget;
  }

  public Quantity<ElectricPotential> getvRated() {
    return vRated;
  }

  public boolean isSlack() {
    return slack;
  }

  public Point getGeoPosition() {
    return geoPosition;
  }

  public CommonVoltageLevel getVoltLvl() {
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
        && Objects.equals(vRated, nodeInput.vRated)
        && Objects.equals(geoPosition, nodeInput.geoPosition)
        && Objects.equals(voltLvl, nodeInput.voltLvl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), vTarget, vRated, slack, geoPosition, voltLvl, subnet);
  }
}
