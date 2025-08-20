/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.io.extractor.HasNodes;
import edu.ie3.datamodel.models.OperationTime;
import java.util.*;

/** Model of a measuring unit attached to a certain {@link NodeInput}. */
public class MeasurementUnitInput extends AssetInput implements HasNodes {
  /** Grid node, the asset is attached to */
  private final NodeInput node;

  /** True, if the voltage magnitude is measured */
  private final boolean vMag;

  /** True, if the voltage angle is measured */
  private final boolean vAng;

  /** True, if the nodal residual active power is measured */
  private final boolean p;

  /** True, if the reactive power is measured */
  private final boolean q;

  /**
   * Constructor for an operated measurement unit
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node Grid node, the asset is attached to
   * @param vMag True, if the voltage magnitude is measured
   * @param vAng True, if the voltage angle is measured
   * @param p True, if the nodal residual active power is measured
   * @param q True, if the reactive power is measured
   */
  public MeasurementUnitInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      boolean vMag,
      boolean vAng,
      boolean p,
      boolean q) {
    super(uuid, id, operator, operationTime);
    this.node = node;
    this.vMag = vMag;
    this.vAng = vAng;
    this.p = p;
    this.q = q;
  }

  /**
   * Constructor for an operated, always on measurement unit
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node Grid node, the asset is attached to
   * @param vMag True, if the voltage magnitude is measured
   * @param vAng True, if the voltage angle is measured
   * @param p True, if the nodal residual active power is measured
   * @param q True, if the reactive power is measured
   */
  public MeasurementUnitInput(
      UUID uuid, String id, NodeInput node, boolean vMag, boolean vAng, boolean p, boolean q) {
    super(uuid, id);
    this.node = node;
    this.vMag = vMag;
    this.vAng = vAng;
    this.p = p;
    this.q = q;
  }

  /**
   * Gets node.
   *
   * @return the node
   */
  public NodeInput getNode() {
    return node;
  }

  /**
   * Gets v mag.
   *
   * @return the v mag
   */
  public boolean getVMag() {
    return vMag;
  }

  /**
   * Gets v ang.
   *
   * @return the v ang
   */
  public boolean getVAng() {
    return vAng;
  }

  /**
   * Gets p.
   *
   * @return the p
   */
  public boolean getP() {
    return p;
  }

  /**
   * Gets q.
   *
   * @return the q
   */
  public boolean getQ() {
    return q;
  }

  @Override
  public MeasurementUnitInputCopyBuilder copy() {
    return new MeasurementUnitInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MeasurementUnitInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(node, that.node)
        && vMag == that.vMag
        && vAng == that.vAng
        && p == that.p
        && q == that.q;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), node, vMag, vAng, p, q);
  }

  @Override
  public String toString() {
    return "MeasurementUnitInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", node="
        + node.getUuid()
        + ", vMag="
        + vMag
        + ", vAng="
        + vAng
        + ", p="
        + p
        + ", q="
        + q
        + '}';
  }

  @Override
  public List<NodeInput> allNodes() {
    return Collections.singletonList(node);
  }

  /**
   * A builder pattern based approach to create copies of {@link MeasurementUnitInput} entities with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * MeasurementUnitInput}*
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class MeasurementUnitInputCopyBuilder
      extends AssetInputCopyBuilder<MeasurementUnitInputCopyBuilder> {

    private NodeInput node;
    private boolean vMag;
    private boolean vAng;
    private boolean p;
    private boolean q;

    private MeasurementUnitInputCopyBuilder(MeasurementUnitInput entity) {
      super(entity);
      this.node = entity.getNode();
      this.vAng = entity.getVAng();
      this.vMag = entity.getVMag();
      this.p = entity.getP();
      this.q = entity.getQ();
    }

    @Override
    public MeasurementUnitInput build() {
      return new MeasurementUnitInput(
          getUuid(), getId(), getOperator(), getOperationTime(), node, vMag, vAng, p, q);
    }

    /**
     * Node measurement unit input copy builder.
     *
     * @param node the node
     * @return the measurement unit input copy builder
     */
    public MeasurementUnitInputCopyBuilder node(NodeInput node) {
      this.node = node;
      return thisInstance();
    }

    /**
     * V mag measurement unit input copy builder.
     *
     * @param vMag the v mag
     * @return the measurement unit input copy builder
     */
    public MeasurementUnitInputCopyBuilder vMag(boolean vMag) {
      this.vMag = vMag;
      return thisInstance();
    }

    /**
     * V ang measurement unit input copy builder.
     *
     * @param vAng the v ang
     * @return the measurement unit input copy builder
     */
    public MeasurementUnitInputCopyBuilder vAng(boolean vAng) {
      this.vAng = vAng;
      return thisInstance();
    }

    /**
     * P measurement unit input copy builder.
     *
     * @param p the p
     * @return the measurement unit input copy builder
     */
    public MeasurementUnitInputCopyBuilder p(boolean p) {
      this.p = p;
      return thisInstance();
    }

    /**
     * Q measurement unit input copy builder.
     *
     * @param q the q
     * @return the measurement unit input copy builder
     */
    public MeasurementUnitInputCopyBuilder q(boolean q) {
      this.q = q;
      return thisInstance();
    }

    @Override
    protected MeasurementUnitInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
