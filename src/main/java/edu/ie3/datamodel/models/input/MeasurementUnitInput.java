/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.models.Nodes;
import edu.ie3.datamodel.models.OperationTime;

import java.util.*;


/**
 * Model of a measuring unit attached to a certain {@link NodeInput}.
 */
public class MeasurementUnitInput extends AssetInput implements Nodes {
    /**
     * Grid node, the asset is attached to
     */
    private final NodeInput node;

    /**
     * True, if the voltage magnitude is measured
     */
    private final boolean vMag;

    /**
     * True, if the voltage angle is measured
     */
    private final boolean vAng;

    /**
     * True, if the nodal residual active power is measured
     */
    private final boolean p;

    /**
     * True, if the reactive power is measured
     */
    private final boolean q;

    /**
     * Constructor for an operated measurement unit
     *
     * @param uuid          of the input entity
     * @param operationTime Time for which the entity is operated
     * @param operator      of the asset
     * @param id            of the asset
     * @param node          Grid node, the asset is attached to
     * @param vMag          True, if the voltage magnitude is measured
     * @param vAng          True, if the voltage angle is measured
     * @param p             True, if the nodal residual active power is measured
     * @param q             True, if the reactive power is measured
     */
    public MeasurementUnitInput(UUID uuid,
                                OperationTime operationTime,
                                OperatorInput operator,
                                String id,
                                NodeInput node,
                                boolean vMag,
                                boolean vAng,
                                boolean p,
                                boolean q) {
        super(uuid, operationTime, operator, id);
        this.node = node;
        this.vMag = vMag;
        this.vAng = vAng;
        this.p = p;
        this.q = q;
    }

    /**
     * Constructor for a non-operated measurement unit
     *
     * @param uuid of the input entity
     * @param id   of the asset
     * @param node Grid node, the asset is attached to
     * @param vMag True, if the voltage magnitude is measured
     * @param vAng True, if the voltage angle is measured
     * @param p    True, if the nodal residual active power is measured
     * @param q    True, if the reactive power is measured
     */
    public MeasurementUnitInput(UUID uuid,
                                String id,
                                NodeInput node,
                                boolean vMag,
                                boolean vAng,
                                boolean p,
                                boolean q) {
        super(uuid, id);
        this.node = node;
        this.vMag = vMag;
        this.vAng = vAng;
        this.p = p;
        this.q = q;
    }

    public NodeInput getNode() {
        return node;
    }

    public boolean getVMag() {
        return vMag;
    }

    public boolean getVAng() {
        return vAng;
    }

    public boolean getP() {
        return p;
    }

    public boolean getQ() {
        return q;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        if(!super.equals(o))
            return false;
        MeasurementUnitInput that = (MeasurementUnitInput) o;
        return Objects.equals(node, that.node) && Objects.equals(vMag, that.vMag) && Objects.equals(vAng, that.vAng) &&
               Objects.equals(p, that.p) && Objects.equals(q, that.q);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), node, vMag, vAng, p, q);
    }

    @Override
    public String toString() {
        return "MeasurementUnitInput{" + "node=" + node + ", vMag=" + vMag + ", vAng=" + vAng + ", p=" + p + ", q=" +
               q + '}';
    }

    @Override
    public List<NodeInput> getNodes() {
        return Collections.unmodifiableList(Collections.singletonList(node));
    }
}
