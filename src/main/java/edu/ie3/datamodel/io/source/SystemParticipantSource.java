/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.input.EvcsInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.Optional;
import java.util.Set;

/** Describes a data source for system participants */
public interface SystemParticipantSource extends DataSource {

  /** @return system participant data as an aggregation of all elements in this grid */
  Optional<SystemParticipants> getSystemParticipants();

  Set<FixedFeedInInput> getFixedFeedIns();

  Set<FixedFeedInInput> getFixedFeedIns(Set<NodeInput> nodes, Set<OperatorInput> operators);

  Set<PvInput> getPvPlants();

  Set<PvInput> getPvPlants(Set<NodeInput> nodes, Set<OperatorInput> operators);

  Set<LoadInput> getLoads();

  Set<LoadInput> getLoads(Set<NodeInput> nodes, Set<OperatorInput> operators);

  Set<EvcsInput> getEvCS();

  Set<EvcsInput> getEvCS(Set<NodeInput> nodes, Set<OperatorInput> operators);

  Set<BmInput> getBmPlants();

  Set<BmInput> getBmPlants(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<BmTypeInput> types);

  Set<StorageInput> getStorages();

  Set<StorageInput> getStorages(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<StorageTypeInput> types);

  Set<WecInput> getWecPlants();

  Set<WecInput> getWecPlants(
      Set<NodeInput> nodes, Set<OperatorInput> operators, Set<WecTypeInput> types);

  Set<EvInput> getEvs();

  Set<EvInput> getEvs(Set<NodeInput> nodes, Set<OperatorInput> operators, Set<EvTypeInput> types);

  Set<ChpInput> getChpPlants();

  Set<ChpInput> getChpPlants(
      Set<NodeInput> nodes,
      Set<OperatorInput> operators,
      Set<ChpTypeInput> types,
      Set<ThermalBusInput> thermalBuses,
      Set<ThermalStorageInput> thermalStorages);

  Set<HpInput> getHeatPumps();

  Set<HpInput> getHeatPumps(
      Set<NodeInput> nodes,
      Set<OperatorInput> operators,
      Set<HpTypeInput> types,
      Set<ThermalBusInput> thermalBuses);
}
