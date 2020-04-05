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

/** Describes a data source for raw grid data */
public interface RawGridSource extends DataSource {
  /** @return grid data as an aggregation of its elements */
  RawGridElements getGridData();

  Collection<NodeInput> getNodes();

  Collection<NodeInput> getNodes(Collection<OperatorInput> operators);

  Collection<LineInput> getLines();

  Collection<LineInput> getLines(
      Collection<NodeInput> nodes,
      Collection<LineTypeInput> lineTypeInputs,
      Collection<OperatorInput> operators);

  Collection<Transformer2WInput> get2WTransformers();

  Collection<Transformer2WInput> get2WTransformers(
      Collection<NodeInput> nodes,
      Collection<Transformer2WTypeInput> transformer2WTypes,
      Collection<OperatorInput> operators);

  Collection<Transformer3WInput> get3WTransformers();

  Collection<Transformer3WInput> get3WTransformers(
      Collection<NodeInput> nodes,
      Collection<Transformer3WTypeInput> transformer3WTypeInputs,
      Collection<OperatorInput> operators);

  Collection<SwitchInput> getSwitches();

  Collection<SwitchInput> getSwitches(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators);

  Collection<MeasurementUnitInput> getMeasurementUnits();

  Collection<MeasurementUnitInput> getMeasurementUnits(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators);
}
