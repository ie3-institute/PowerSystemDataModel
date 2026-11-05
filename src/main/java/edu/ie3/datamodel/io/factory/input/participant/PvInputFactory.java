/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.PvInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Factory to create instances of {@link PvInput}s. */
public class PvInputFactory extends SystemParticipantInputEntityFactory<PvInput> {

  public PvInputFactory(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits) {
    super(operators, nodes, emUnits, PvInput.class);
  }

  @Override
  protected PvInput buildModel(
      FieldMap data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime,
      EmInput controllingEm) {
    double albedo = data.getDouble(ALBEDO);
    ComparableQuantity<Angle> azimuth = data.getQuantity(AZIMUTH, StandardUnits.AZIMUTH);
    ComparableQuantity<Dimensionless> etaConv =
        data.getQuantity(ETA_CONV, StandardUnits.EFFICIENCY);
    ComparableQuantity<Angle> elevationAngle =
        data.getQuantity(ELEVATION_ANGLE, StandardUnits.SOLAR_ELEVATION_ANGLE);
    double kG = data.getDouble(KG);
    double kT = data.getDouble(KT);
    ComparableQuantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    double cosPhi = data.getDouble(COS_PHI_RATED);

    return new PvInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        controllingEm,
        albedo,
        azimuth,
        etaConv,
        elevationAngle,
        kG,
        kT,
        sRated,
        cosPhi,
        data.additionalInformation());
  }
}
