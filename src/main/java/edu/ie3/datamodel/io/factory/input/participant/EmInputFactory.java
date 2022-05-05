/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.models.ControlStrategy;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.EnergyManagementInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmInputFactory
    extends SystemParticipantInputEntityFactory<EnergyManagementInput, NodeAssetInputEntityData> {
  private static final Logger logger = LoggerFactory.getLogger(EmInputFactory.class);

  private static final String CONNECTED_ASSETS = "connectedassets";

  private static final String CONTROL_STRATEGY = "controlstrategy";

  public EmInputFactory() {
    super(EnergyManagementInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {CONNECTED_ASSETS, CONTROL_STRATEGY};
  }

  @Override
  protected EnergyManagementInput buildModel(
      NodeAssetInputEntityData data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {
    ControlStrategy controlStrategy;
    try {
      controlStrategy = ControlStrategy.parse(data.getField(CONTROL_STRATEGY));
    } catch (ParsingException e) {
      logger.warn(
          "Cannot parse control strategy \"{}\" of energy management system \"{}\". Assign no control strategy instead.",
          data.getField(CONTROL_STRATEGY),
          id);
      controlStrategy = ControlStrategy.DefaultControlStrategies.NO_CONTROL_STRATEGY;
    }
    final UUID[] connectedAssets = data.getUUIDs(CONNECTED_ASSETS);
    return new EnergyManagementInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        connectedAssets,
        controlStrategy);
  }
}
