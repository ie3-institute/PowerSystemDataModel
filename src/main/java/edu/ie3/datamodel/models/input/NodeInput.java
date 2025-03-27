/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.util.geo.GeoUtils;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

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
  public static final Point DEFAULT_GEO_POSITION = GeoUtils.buildPoint(51.4843281, 7.4116482);

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
  public NodeInputCopyBuilder copy() {
    return new NodeInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof NodeInput nodeInput)) return false;
    if (!super.equals(o)) return false;
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
        + "uuid="
        + getUuid()
        + ", id='"
        + getId()
        + '\''
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", vTarget="
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

  /**
   * A builder pattern based approach to create copies of {@link NodeInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link NodeInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class NodeInputCopyBuilder extends AssetInputCopyBuilder<NodeInputCopyBuilder> {

    private ComparableQuantity<Dimensionless> vTarget;
    private boolean slack;
    private Point geoPosition;
    private VoltageLevel voltLvl;
    private int subnet;

    private NodeInputCopyBuilder(NodeInput entity) {
      super(entity);

      this.vTarget = entity.getvTarget();
      this.slack = entity.isSlack();
      this.geoPosition = entity.getGeoPosition();
      this.voltLvl = entity.getVoltLvl();
      this.subnet = entity.getSubnet();
    }

    @Override
    public NodeInput build() {
      return new NodeInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          vTarget,
          slack,
          geoPosition,
          voltLvl,
          subnet);
    }

    public NodeInputCopyBuilder vTarget(ComparableQuantity<Dimensionless> vTarget) {
      this.vTarget = vTarget;
      return thisInstance();
    }

    public NodeInputCopyBuilder slack(boolean isSlack) {
      this.slack = isSlack;
      return thisInstance();
    }

    public NodeInputCopyBuilder geoPosition(Point geoPosition) {
      this.geoPosition = geoPosition;
      return thisInstance();
    }

    public NodeInputCopyBuilder voltLvl(VoltageLevel voltLvl) {
      this.voltLvl = voltLvl;
      return thisInstance();
    }

    public NodeInputCopyBuilder subnet(int subnet) {
      this.subnet = subnet;
      return thisInstance();
    }

    @Override
    protected NodeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
