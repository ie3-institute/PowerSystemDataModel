/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source;

import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.SwitchInput;
import edu.ie3.models.input.connector.Transformer2WInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import java.util.Collection;
import java.util.Optional;

/** Describes a data source for raw grid data */
public interface RawGridSource extends DataSource {
  /** @return grid data as an aggregation of its elements */
  AggregatedRawGridInput getGridData();

  Collection<NodeInput> getNodes();

  Collection<LineInput> getLines();

  Collection<Transformer2WInput> get2WTransformers();

  Collection<Transformer3WInput> get3WTransformers();

  Collection<SwitchInput> getSwitches();

  // ** For Performance Measurement Purposes only */
  Collection<NodeInput> getNeighborNodesOfSubnet(Integer subnet);

  // ** For Performance Measurement Purposes only */
  Optional<AggregatedRawGridInput> getSubnet(Integer subnet);
}
