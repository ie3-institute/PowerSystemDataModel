/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/** Describes a data source for raw grid data */
public interface RawGridSource extends DataSource {
  /** @return grid data as an aggregation of its elements */
  Optional<RawGridElements> getGridData();

  Set<NodeInput> getNodes();

  Set<NodeInput> getNodes(Collection<OperatorInput> operators);

  Set<LineInput> getLines();

  Set<LineInput> getLines(
      Set<NodeInput> nodes, Set<LineTypeInput> lineTypeInputs, Set<OperatorInput> operators);

  Set<Transformer2WInput> get2WTransformers();

  Set<Transformer2WInput> get2WTransformers(
      Set<NodeInput> nodes,
      Set<Transformer2WTypeInput> transformer2WTypes,
      Set<OperatorInput> operators);

  Set<Transformer3WInput> get3WTransformers();

  Set<Transformer3WInput> get3WTransformers(
      Set<NodeInput> nodes,
      Set<Transformer3WTypeInput> transformer3WTypeInputs,
      Set<OperatorInput> operators);

  Set<SwitchInput> getSwitches();

  Set<SwitchInput> getSwitches(Set<NodeInput> nodes, Set<OperatorInput> operators);

  Set<MeasurementUnitInput> getMeasurementUnits();

  Set<MeasurementUnitInput> getMeasurementUnits(Set<NodeInput> nodes, Set<OperatorInput> operators);
}
