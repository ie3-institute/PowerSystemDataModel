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
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import java.util.UUID;

/**
 * Abstract factory for thermal system participants that share common construction patterns
 *
 * @param <M> The model type (AcInput, HpInput, etc.)
 * @param <D> The entity data type
 */
public abstract class ThermalSystemParticipantInputFactory<
        M extends SystemParticipantInput, D extends ThermalSystemParticipantEntityData<?>>
    extends SystemParticipantInputEntityFactory<M, D> {

  protected static final String TYPE = "type";
  protected static final String THERMAL_BUS = "thermalBus";

  protected ThermalSystemParticipantInputFactory(Class<M> modelClass) {
    super(modelClass);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {TYPE, THERMAL_BUS};
  }

  @Override
  protected final M buildModel(
      D data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {

    final EmInput em = data.getControllingEm().orElse(null);

    return createThermalSystemModel(
        uuid,
        id,
        operator,
        operationTime,
        node,
        data.getThermalBusInput(),
        qCharacteristics,
        em,
        data.getTypeInput());
  }

  /**
   * Creates the specific thermal system model instance
   *
   * @param uuid The UUID of the system participant
   * @param id The ID of the system participant
   * @param operator The operator input
   * @param operationTime The operation time
   * @param node The node input
   * @param thermalBusInput The thermal bus input
   * @param qCharacteristics The reactive power characteristics
   * @param em The energy management input (can be null)
   * @param typeInput The type input for the specific system participant
   * @return The created thermal system model
   */
  protected abstract M createThermalSystemModel(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ThermalBusInput thermalBusInput,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      Object typeInput);
}
