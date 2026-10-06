/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.exceptions.ChargingPointTypeException;
import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.EvcsInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointType;
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils;
import edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationType;
import edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationTypeUtils;
import java.util.Map;
import java.util.UUID;

/** Factory to create instances of {@link EvcsInput}s. */
public class EvcsInputFactory extends SystemParticipantInputEntityFactory<EvcsInput> {

  public EvcsInputFactory(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits) {
    super(operators, nodes, emUnits, EvcsInput.class);
  }

  @Override
  protected EvcsInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime,
      EmInput controllingEm) {
    ChargingPointType type;
    String typeFieldValue = getField(data, TYPE);

    try {
      type = ChargingPointTypeUtils.parse(typeFieldValue);
    } catch (ChargingPointTypeException e) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse field \"%s\" with supposed int value \"%s\"",
              TYPE, typeFieldValue),
          e);
    }
    int chargingPoints = getInt(data, CHARGING_POINTS);
    double cosPhi = getDouble(data, COS_PHI_RATED);

    EvcsLocationType locationType;
    String locationFieldValue = getField(data, LOCATION_TYPE);
    try {
      locationType = EvcsLocationTypeUtils.parse(locationFieldValue);
    } catch (ParsingException e) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse field \"%s\" with supposed int value \"%s\"",
              LOCATION_TYPE, locationFieldValue),
          e);
    }

    boolean v2gSupport = getBoolean(data, V2G_SUPPORT);

    return new EvcsInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        controllingEm,
        type,
        chargingPoints,
        cosPhi,
        locationType,
        v2gSupport,
        data);
  }
}
