/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.PvInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import java.util.Map;
import java.util.UUID;

/** Factory to create instances of {@link PvInput}s. */
public class PvInputFactory extends SystemParticipantInputEntityFactory<PvInput> {

  public PvInputFactory(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits) {
    super(operators, nodes, emUnits, PvInput.class);
  }

  @Override
  protected PvInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime,
      EmInput controllingEm) {
    double albedo = getDouble(data, ALBEDO);
    ComparableQuantity<Angle> azimuth = getQuantity(data, AZIMUTH, StandardUnits.AZIMUTH);
    ComparableQuantity<Dimensionless> etaConv = getQuantity(data, ETA_CONV, StandardUnits.EFFICIENCY);
    ComparableQuantity<Angle> elevationAngle = getQuantity(data, ELEVATION_ANGLE, StandardUnits.SOLAR_ELEVATION_ANGLE);
    double kG = getDouble(data, KG);
    double kT = getDouble(data, KT);
    ComparableQuantity<Power> sRated = getQuantity(data, S_RATED, StandardUnits.S_RATED);
    double cosPhi = getDouble(data, COS_PHI_RATED);

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
        data);
  }
}
