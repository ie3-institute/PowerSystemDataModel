/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system;

import com.sun.org.apache.xalan.internal.lib.NodeInfo;
import edu.ie3.datamodel.models.Nodes;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;

import java.util.*;


/**
 * Describes a system asset that is connected to a node
 */
public abstract class SystemParticipantInput extends AssetInput implements Nodes {

    /**
     * The node that the asset is connected to
     */
    private final NodeInput node;

    /**
     * Description of a reactive power characteristic. For details see further documentation
     */
    private final String qCharacteristics;

    /**
     * Constructor for an operated system participant
     *
     * @param uuid             of the input entity
     * @param operationTime    Time for which the entity is operated
     * @param operator         of the asset
     * @param id               of the asset
     * @param node             that the asset is connected to
     * @param qCharacteristics Description of a reactive power characteristic
     */
    public SystemParticipantInput(UUID uuid,
                                  OperationTime operationTime,
                                  OperatorInput operator,
                                  String id,
                                  NodeInput node,
                                  String qCharacteristics) {
        super(uuid, operationTime, operator, id);
        this.node = node;
        this.qCharacteristics = qCharacteristics;
    }

    /**
     * Constructor for a non-operated system participant
     *
     * @param uuid             of the input entity
     * @param id               of the asset
     * @param node             that the asset is connected to
     * @param qCharacteristics Description of a reactive power characteristic
     */
    public SystemParticipantInput(UUID uuid, String id, NodeInput node, String qCharacteristics) {
        super(uuid, id);
        this.node = node;
        this.qCharacteristics = qCharacteristics;
    }

    public String getqCharacteristics() {
        return qCharacteristics;
    }

    public NodeInput getNode() {
        return node;
    }

    public List<NodeInput> getNodes() {
        return Collections.singletonList(node);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        if(!super.equals(o))
            return false;
        SystemParticipantInput that = (SystemParticipantInput) o;
        return Objects.equals(node, that.node) && Objects.equals(qCharacteristics, that.qCharacteristics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), node, qCharacteristics);
    }

    @Override
    public String toString() {
        return "SystemParticipantInput{" + "node=" + node + ", qCharacteristics='" + qCharacteristics + '\'' + '}';
    }
}
