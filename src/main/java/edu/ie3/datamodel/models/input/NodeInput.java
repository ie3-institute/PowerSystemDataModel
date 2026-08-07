package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.ConversionUtils;
import edu.ie3.util.geo.GeoUtils;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

public class NodeInput extends AssetInput {
  /**
   * Use this default value if geoPosition is unknown.
   */
  public static final Point DEFAULT_GEO_POSITION = GeoUtils.buildPoint(51.4843281, 7.4116482);

  /**
   * Target voltage magnitude of the node with regard to its rated voltage (typically in p.u.).
   */
  private final ComparableQuantity<Dimensionless> vTarget;

  /**
   * Is this node a slack node?
   */
  private final boolean slack;

  /**
   * The coordinates of this node, especially relevant for geo-dependant systems, that are connected to this node.
   */
  private final Point geoPosition;

  /**
   * Voltage level of this node.
   */
  private final VoltageLevel voltLvl;

  /**
   * Subgrid of this node.
   */
  private final int subnet;

  public NodeInput(UUID uuid, String id, ComparableQuantity<Dimensionless> vTarget, boolean slack,
      Point geoPosition, VoltageLevel voltLvl, int subnet) {
    super(uuid, id);
    this.vTarget = vTarget;
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
  }

  public NodeInput(UUID uuid, String id, OperatorInput operator, OperationTime operationTime,
      ComparableQuantity<Dimensionless> vTarget, boolean slack, Point geoPosition,
      VoltageLevel voltLvl, int subnet) {
    super(uuid, id, operator, operationTime);
    this.vTarget = vTarget;
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
  }

  public NodeInput(UUID uuid, String id, OperatorInput operator, OperationTime operationTime,
      ComparableQuantity<Dimensionless> vTarget, boolean slack, Point geoPosition,
      VoltageLevel voltLvl, int subnet, Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime);
    this.vTarget = vTarget;
    this.slack = slack;
    this.geoPosition = geoPosition;
    this.voltLvl = voltLvl;
    this.subnet = subnet;
    setAdditionalInformation(additionalInformation);
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

  public static NodeInput fromMap(Map<String, String> data, Map<UUID, OperatorInput> operators) {
    UUID uuid = ConversionUtils.getUUID(data, "uuid");
    String id = ConversionUtils.getField(data, "id");
    OperatorInput operator = ConversionUtils.getEntity(data, "operator", operators);
    OperationTime operationTime = ConversionUtils.buildOperationTime(data, "operationStart", "operationEnd");
    ComparableQuantity<Dimensionless> vTarget = ConversionUtils.getDimensionless(data, "vTarget");
    boolean slack = ConversionUtils.getBoolean(data, "slack");
    Point geoPosition = ConversionUtils.getNodePoint(data, "geoPosition");
    VoltageLevel voltLvl = ConversionUtils.getVoltageLvl(data, "voltLvl", "vRated");
    int subnet = ConversionUtils.getInt(data, "subnet");

    NodeInput model = new NodeInput(
          uuid,
          id,
          operator,
          operationTime,
          vTarget,
          slack,
          geoPosition,
          voltLvl,
          subnet
        );

    model.setAdditionalInformation(data);
    return model;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof NodeInput that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(vTarget, that.vTarget)
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
        + "uuid=" + getUuid()
        + ", id=" + getId()
        + ", operator=" + getOperator()
        + ", operationTime=" + getOperationTime()
        + ", vTarget=" + getvTarget()
        + ", slack=" + isSlack()
        + ", geoPosition=" + getGeoPosition()
        + ", voltLvl=" + getVoltLvl()
        + ", subnet=" + getSubnet()
        + '}';
  }

  public static class NodeInputCopyBuilder extends AssetInput.AssetInputCopyBuilder<NodeInputCopyBuilder> {
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

    public NodeInputCopyBuilder vTarget(ComparableQuantity<Dimensionless> vTarget) {
      this.vTarget = vTarget;
      return thisInstance();
    }

    protected ComparableQuantity<Dimensionless> getvTarget() {
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
      NodeInput result = new NodeInput(getUuid(), getId(), getOperator(), getOperationTime(), getvTarget(), isSlack(), getGeoPosition(), getVoltLvl(), getSubnet(), getAdditionalInformation());
      result.setAdditionalInformation(getAdditionalInformation());
      return result;
    }

    @Override
    protected NodeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
