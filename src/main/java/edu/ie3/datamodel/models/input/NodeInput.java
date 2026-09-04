/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.QuantityUtils;
import edu.ie3.util.geo.GeoUtils;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

/** Describes an electrical grid node, that other assets can connect to. */
public class NodeInput extends AssetInput {
  /** Use this default value if geoPosition is unknown. */
  public static final Point DEFAULT_GEO_POSITION = GeoUtils.buildPoint(51.4843281, 7.4116482);

  /** Target voltage magnitude of the node with regard to its rated voltage (typically in p.u.). */
  private final ComparableQuantity<Dimensionless> vTarget;

  /** Is this node a slack node? */
  private final boolean slack;

  /**
   * The coordinates of this node, especially relevant for geo-dependant systems, that are connected
   * to this node.
   */
  private final Point geoPosition;

  /** Voltage level of this node. */
  private final VoltageLevel voltLvl;

  /** Subgrid of this node. */
  private final int subnet;

  /**
   * Constructor for an operated node.
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
    this.vTarget = vTarget;
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
  }

  /**
   * Constructor for an operated node.
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
   * @param additionalInformation That were provided by the source
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
      int subnet,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime);
    this.vTarget = vTarget;
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated node.
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
    this.vTarget = vTarget;
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
  }

  public ComparableQuantity<Dimensionless> getVTarget() {
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
    if (!(o instanceof NodeInput that)) return false;
    if (!super.equals(o)) return false;
    return QuantityUtils.equals(vTarget, that.vTarget)
        && slack == that.slack
        && Objects.equals(geoPosition, that.geoPosition)
        && Objects.equals(voltLvl, that.voltLvl)
        && subnet == that.subnet;
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
        + ", id="
        + getId()
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
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public NodeInputCopyBuilder copy() {
    return new NodeInputCopyBuilder(this);
  }

  public static class NodeInputCopyBuilder extends AssetInputCopyBuilder<NodeInputCopyBuilder> {
    private ComparableQuantity<Dimensionless> vTarget;

    private boolean slack;

    private Point geoPosition;

    private VoltageLevel voltLvl;

    private int subnet;

    protected NodeInputCopyBuilder(NodeInput entity) {
      super(entity);
      this.vTarget = entity.vTarget;
      this.slack = entity.slack;
      this.geoPosition = entity.geoPosition;
      this.voltLvl = entity.voltLvl;
      this.subnet = entity.subnet;
    }

    public NodeInputCopyBuilder vTarget(ComparableQuantity<Dimensionless> vTarget) {
      this.vTarget = vTarget;
      return thisInstance();
    }

    protected ComparableQuantity<Dimensionless> getVTarget() {
      return vTarget;
    }

    public NodeInputCopyBuilder slack(boolean slack) {
      this.slack = slack;
      return thisInstance();
    }

    protected boolean isSlack() {
      return slack;
    }

    public NodeInputCopyBuilder geoPosition(Point geoPosition) {
      this.geoPosition = geoPosition;
      return thisInstance();
    }

    protected Point getGeoPosition() {
      return geoPosition;
    }

    public NodeInputCopyBuilder voltLvl(VoltageLevel voltLvl) {
      this.voltLvl = voltLvl;
      return thisInstance();
    }

    protected VoltageLevel getVoltLvl() {
      return voltLvl;
    }

    public NodeInputCopyBuilder subnet(int subnet) {
      this.subnet = subnet;
      return thisInstance();
    }

    protected int getSubnet() {
      return subnet;
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
          subnet,
          getAdditionalInformation());
    }

    @Override
    protected NodeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
