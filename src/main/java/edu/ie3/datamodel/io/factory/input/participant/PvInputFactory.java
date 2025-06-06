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
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public class PvInputFactory
    extends SystemParticipantInputEntityFactory<PvInput, SystemParticipantEntityData> {
  private static final String ALBEDO = "albedo";
  private static final String AZIMUTH = "azimuth";
  private static final String ETA_CONV = "etaConv";
  private static final String ELEVATION_ANGLE = "elevationAngle";
  private static final String KG = "kG";
  private static final String KT = "kT";
  private static final String MARKET_REACTION = "marketReaction";
  private static final String S_RATED = "sRated";
  private static final String COS_PHI_RATED = "cosPhiRated";

  public PvInputFactory() {
    super(PvInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {
      ALBEDO, AZIMUTH, ETA_CONV, ELEVATION_ANGLE, KG, KT, MARKET_REACTION, S_RATED, COS_PHI_RATED
    };
  }

  @Override
  protected PvInput buildModel(
      SystemParticipantEntityData data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {
    final EmInput em = data.getControllingEm().orElse(null);
    final double albedo = data.getDouble(ALBEDO);
    final ComparableQuantity<Angle> azimuth = data.getQuantity(AZIMUTH, StandardUnits.AZIMUTH);
    final ComparableQuantity<Dimensionless> etaConv =
        data.getQuantity(ETA_CONV, StandardUnits.EFFICIENCY);
    final ComparableQuantity<Angle> elevationAngle =
        data.getQuantity(ELEVATION_ANGLE, StandardUnits.SOLAR_ELEVATION_ANGLE);
    final double kG = data.getDouble(KG);
    final double kT = data.getDouble(KT);
    final boolean marketReaction = data.getBoolean(MARKET_REACTION);
    final ComparableQuantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    final double cosPhi = data.getDouble(COS_PHI_RATED);

    return new PvInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        em,
        albedo,
        azimuth,
        etaConv,
        elevationAngle,
        kG,
        kT,
        marketReaction,
        sRated,
        cosPhi);
  }
}
