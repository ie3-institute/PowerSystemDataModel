/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.exceptions.ChargingPointTypeException;
import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.EvcsInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointType;
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils;
import edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationType;
import edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationTypeUtils;

/**
 * Factory to create instances of {@link EvcsInput}s based on {@link NodeAssetInputEntityData} and
 * additional fields.
 *
 * @version 0.1
 * @since 26.07.20
 */
public class EvcsInputFactory
    extends SystemParticipantInputEntityFactory<EvcsInput, NodeAssetInputEntityData> {

  private static final String TYPE = "type";
  private static final String CHARGING_POINTS = "chargingPoints";
  private static final String COS_PHI_RATED = "cosPhiRated";
  private static final String LOCATION_TYPE = "locationType";
  private static final String V2G_SUPPORT = "v2gSupport";

  public EvcsInputFactory() {
    super(EvcsInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {TYPE, CHARGING_POINTS, COS_PHI_RATED, LOCATION_TYPE, V2G_SUPPORT};
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

    final EvcsLocationType locationType;
    try {
      locationType = EvcsLocationTypeUtils.parse(data.getField(LOCATION_TYPE));
    } catch (ParsingException e) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse field \"%s\" with supposed int value \"%s\"",
              LOCATION_TYPE, data.getField(LOCATION_TYPE)),
          e);
    }

    final boolean v2gSupport = data.getBoolean(V2G_SUPPORT);

    return new EvcsInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        type,
        chargingPoints,
        cosPhi,
        locationType,
        v2gSupport);
  }
}
