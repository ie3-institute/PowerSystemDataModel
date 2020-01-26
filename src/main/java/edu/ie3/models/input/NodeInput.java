/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.VoltageLevel;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;

/** Describes an electrical grid node, that other assets can connect to */
public class NodeInput extends AssetInput {
  /** Target voltage magnitude of the node with regard to its rated voltage (typically in p.u.) */
  private Quantity<Dimensionless> vTarget;
  /** Rated voltage magnitude of the node (typically in kV) */
  private Quantity<ElectricPotential> vRated;
  /** Is this node a slack node? */
  private boolean slack;
  /**
   * The coordinates of this node, especially relevant for geo-dependant systems, that are connected
   * to this node
   */
  private Point geoPosition;
  /** Voltage level of this node */
  private VoltageLevel voltLvl;
  /** Subnet of this node */
  private int subnet;
  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
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
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      Quantity<Dimensionless> vTarget,
      Quantity<ElectricPotential> vRated,
      boolean slack,
      Point geoPosition,
      VoltageLevel voltLvl,
      int subnet) {
    super(uuid, operationInterval, operator, id);
    this.vTarget = vTarget.to(StandardUnits.TARGET_VOLTAGE);
    this.vRated = vRated.to(StandardUnits.V_RATED);
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
  }

  /**
   * If both operatesFrom and operatesUntil are Empty, it is assumed that the asset is non-operated.
   *
   * @param uuid of the input entity
   * @param operatesFrom start of operation period, will be replaced by LocalDateTime.MIN if Empty
   * @param operatesUntil end of operation period, will be replaced by LocalDateTime.MAX if Empty
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
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      Quantity<Dimensionless> vTarget,
      Quantity<ElectricPotential> vRated,
      boolean slack,
      Point geoPosition,
      VoltageLevel voltLvl,
      int subnet) {
    super(uuid, operatesFrom, operatesUntil, operator, id);
    this.vTarget = vTarget.to(StandardUnits.TARGET_VOLTAGE);
    this.vRated = vRated.to(StandardUnits.V_RATED);
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
      VoltageLevel voltLvl,
      int subnet) {
    super(uuid, id);
    this.vTarget = vTarget.to(StandardUnits.TARGET_VOLTAGE);
    this.vRated = vRated.to(StandardUnits.V_RATED);
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
  }

  public Quantity<Dimensionless> getVTarget() {
    return vTarget;
  }

  public void setVTarget(Quantity<Dimensionless> vTarget) {
    this.vTarget = vTarget.to(StandardUnits.TARGET_VOLTAGE);
  }

  public Quantity<ElectricPotential> getVRated() {
    return vRated;
  }

  public void setVRated(Quantity<ElectricPotential> vRated) {
    this.vRated = vRated.to(StandardUnits.V_RATED);
  }

  public boolean getSlack() {
    return slack;
  }

  public void setSlack(boolean slack) {
    this.slack = slack;
  }

  public Point getGeoPosition() {
    return geoPosition;
  }

  public void setGeoPosition(Point geoPosition) {
    this.geoPosition = geoPosition;
  }

  public VoltageLevel getVoltLvl() {
    return voltLvl;
  }

  public void setVoltLvl(VoltageLevel voltLvl) {
    this.voltLvl = voltLvl;
  }

  public int getSubnet() {
    return subnet;
  }

  public void setSubnet(int subnet) {
    this.subnet = subnet;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o)
      return true;
    if(o == null || getClass() != o.getClass())
      return false;
    if(!super.equals(o))
      return false;
    NodeInput nodeInput = (NodeInput) o;
    return slack == nodeInput.slack && subnet == nodeInput.subnet && Objects.equals(vTarget, nodeInput.vTarget) &&
           Objects.equals(vRated, nodeInput.vRated) && Objects.equals(geoPosition, nodeInput.geoPosition) &&
           Objects.equals(voltLvl, nodeInput.voltLvl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), vTarget, vRated, slack, geoPosition, voltLvl, subnet);
  }
}
