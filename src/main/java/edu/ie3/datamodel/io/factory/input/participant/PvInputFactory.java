/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.PvInput;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;

public class PvInputFactory
    extends SystemParticipantInputEntityFactory<PvInput, SystemParticipantEntityData> {
  private static final String ALBEDO = "albedo";
  private static final String AZIMUTH = "azimuth";
  private static final String ETA_CONV = "etaconv";
  private static final String HEIGHT = "height";
  private static final String KG = "kg";
  private static final String KT = "kt";
  private static final String MARKET_REACTION = "marketreaction";
  private static final String S_RATED = "srated";
  private static final String COS_PHI = "cosphi";

  public PvInputFactory() {
    super(PvInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {
      ALBEDO, AZIMUTH, ETA_CONV, HEIGHT, KG, KT, MARKET_REACTION, S_RATED, COS_PHI
    };
  }

  @Override
  protected PvInput buildModel(
      SystemParticipantEntityData data,
      java.util.UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    final double albedo = data.getDouble(ALBEDO);
    final Quantity<Angle> azimuth = data.getQuantity(AZIMUTH, StandardUnits.AZIMUTH);
    final Quantity<Dimensionless> etaConv = data.getQuantity(ETA_CONV, StandardUnits.EFFICIENCY);
    final Quantity<Angle> height = data.getQuantity(HEIGHT, StandardUnits.SOLAR_HEIGHT);
    final double kG = data.getDouble(KG);
    final double kT = data.getDouble(KT);
    final boolean marketReaction = data.getBoolean(MARKET_REACTION);
    final Quantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    final double cosPhi = data.getDouble(COS_PHI);

    return new PvInput(
        uuid,
        id,
        operatorInput,
        operationTime,
        node,
        qCharacteristics,
        albedo,
        azimuth,
        etaConv,
        height,
        kG,
        kT,
        marketReaction,
        sRated,
        cosPhi);
  }
}
