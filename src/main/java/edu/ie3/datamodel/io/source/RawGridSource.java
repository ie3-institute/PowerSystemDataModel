/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import java.util.Collection;

/** Describes a data source for raw grid data */
public interface RawGridSource extends DataSource {
  /** @return grid data as an aggregation of its elements */
  RawGridElements getGridData();

  // todo
  Collection<NodeInput> getNodes();

  Collection<NodeInput> getNodes(Collection<OperatorInput> operators);

  //  Collection<LineInput> getLines();
  //
  Collection<Transformer2WInput> get2WTransformers();

  Collection<Transformer2WInput> get2WTransformers(
      Collection<NodeInput> nodes,
      Collection<Transformer2WTypeInput> transformer2WTypes,
      Collection<OperatorInput> operators);
  //
  //  Collection<Transformer3WInput> get3WTransformers();
  //
  //  Collection<SwitchInput> getSwitches();

  //  // ** For Performance Measurement Purposes only */
  //  Collection<NodeInput> getNeighborNodesOfSubnet(Integer subnet);
  //
  //  // ** For Performance Measurement Purposes only */
  //  Optional<AggregatedRawGridInput> getSubnet(Integer subnet);
}
