/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityFactory;
import edu.ie3.datamodel.models.ControlStrategy;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.EmInput;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmInputFactory extends AssetInputEntityFactory<EmInput, AssetInputEntityData> {
  private static final Logger logger = LoggerFactory.getLogger(EmInputFactory.class);

  private static final String CONNECTED_ASSETS = "connectedAssets";

  private static final String CONTROL_STRATEGY = "controlStrategy";

  public EmInputFactory() {
    super(EmInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {CONNECTED_ASSETS, CONTROL_STRATEGY};
  }

  @Override
  protected EmInput buildModel(
      AssetInputEntityData data,
      UUID uuid,
      String id,
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

    if (connectedAssets.length == 0)
      logger.warn("There are no connected assets for energy management system \"{}\".", id);

    return new EmInput(uuid, id, operator, operationTime, connectedAssets, controlStrategy);
  }
}
