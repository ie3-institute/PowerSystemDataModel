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
import java.util.Collection;

/** Describes a data source for system participants */
public interface SystemParticipantSource extends DataSource {

  /** @return system participant data as an aggregation of all elements in this grid */
  SystemParticipants getSystemParticipants();

  Collection<FixedFeedInInput> getFixedFeedIns();

  Collection<FixedFeedInInput> getFixedFeedIns(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators);

  Collection<PvInput> getPvPlants();

  Collection<PvInput> getPvPlants(Collection<NodeInput> nodes, Collection<OperatorInput> operators);

  Collection<LoadInput> getLoads();

  Collection<LoadInput> getLoads(Collection<NodeInput> nodes, Collection<OperatorInput> operators);

  Collection<EvcsInput> getEvCS();

  Collection<EvcsInput> getEvCS(Collection<NodeInput> nodes, Collection<OperatorInput> operators);

  Collection<BmInput> getBmPlants();

  Collection<BmInput> getBmPlants(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<BmTypeInput> types);

  Collection<StorageInput> getStorages();

  Collection<StorageInput> getStorages(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<StorageTypeInput> types);

  Collection<WecInput> getWecPlants();

  Collection<WecInput> getWecPlants(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<WecTypeInput> types);

  Collection<EvInput> getEvs();

  Collection<EvInput> getEvs(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<EvTypeInput> types);

  Collection<ChpInput> getChpPlants();

  Collection<ChpInput> getChpPlants(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<ChpTypeInput> types,
      Collection<ThermalBusInput> thermalBuses,
      Collection<ThermalStorageInput> thermalStorages);

  Collection<HpInput> getHeatPumps();

  Collection<HpInput> getHeatPumps(
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<HpTypeInput> types,
      Collection<ThermalBusInput> thermalBuses);
}
