/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.ChpInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.Map;
import java.util.UUID;

/** Factory to create instances of {@link ChpInput}s. */
public class ChpInputFactory extends SystemParticipantInputEntityFactory<ChpInput> {

  private final Map<UUID, ChpTypeInput> types;
  private final Map<UUID, ThermalBusInput> thermalBuses;
  private final Map<UUID, ThermalStorageInput> thermalStorages;

  public ChpInputFactory(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, EmInput> emUnits,
      Map<UUID, ChpTypeInput> types,
      Map<UUID, ThermalBusInput> thermalBuses,
      Map<UUID, ThermalStorageInput> thermalStorages) {
    super(operators, nodes, emUnits, ChpInput.class);
    this.types = types;
    this.thermalBuses = thermalBuses;
    this.thermalStorages = thermalStorages;
  }

  @Override
  protected ChpInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime,
      EmInput controllingEm) {
    return new ChpInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        getEntity(data, THERMAL_BUS, thermalBuses),
        qCharacteristics,
        controllingEm,
        getEntity(data, TYPE, types),
        getEntity(data, THERMAL_STORAGE, thermalStorages),
        data);
  }
}
