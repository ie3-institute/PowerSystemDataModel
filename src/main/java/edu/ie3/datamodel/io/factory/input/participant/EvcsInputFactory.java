/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.exceptions.ChargingPointTypeException;
import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.EvcsInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointType;
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils;

/**
 * //ToDo: Class Description // todo test
 *
 * @version 0.1
 * @since 26.07.20
 */
public class EvcsInputFactory
    extends SystemParticipantInputEntityFactory<EvcsInput, NodeAssetInputEntityData> {

  private static final String TYPE = "type";
  private static final String CHARGING_POINTS = "chargingpoints";
  private static final String COS_PHI_RATED = "cosphirated";

  public EvcsInputFactory() {
    super(EvcsInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {TYPE, CHARGING_POINTS, COS_PHI_RATED};
  }

  @Override
  protected EvcsInput buildModel(
      NodeAssetInputEntityData data,
      java.util.UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {

    final ChargingPointType type;
    try {
      type = ChargingPointTypeUtils.parse(data.getField(TYPE));
    } catch (ChargingPointTypeException e) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse field \"%s\" with supposed int value \"%s\"",
              TYPE, data.getField(TYPE)),
          e);
    }
    final int chargingPoints = data.getInt(CHARGING_POINTS);
    final double cosPhi = data.getDouble(COS_PHI_RATED);

    return new EvcsInput(
        uuid, id, operator, operationTime, node, qCharacteristics, type, chargingPoints, cosPhi);
  }
}
