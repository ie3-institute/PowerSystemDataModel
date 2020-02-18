/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.LoadInput;
import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

public class LoadInputFactory
    extends SystemParticipantInputEntityFactory<LoadInput, SystemParticipantEntityData> {
  private static final String DSM = "dsm";
  private static final String E_CONS_ANNUAL = "econsannual";
  private static final String S_RATED = "srated";
  private static final String COS_PHI = "cosphi";

  public LoadInputFactory() {
    super(LoadInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {DSM, E_CONS_ANNUAL, S_RATED, COS_PHI};
  }

  @Override
  protected LoadInput buildModel(
      SystemParticipantEntityData data,
      java.util.UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    final boolean dsm = data.getBoolean(DSM);
    final Quantity<Energy> eConsAnnual = data.getQuantity(E_CONS_ANNUAL, StandardUnits.ENERGY_IN);
    final Quantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    final double cosPhi = data.getDouble(COS_PHI);

    return new LoadInput(
        uuid,
        operationTime,
        operatorInput,
        id,
        node,
        qCharacteristics,
        dsm,
        eConsAnnual,
        sRated,
        cosPhi);
  }

  @Override
  protected LoadInput buildModel(
      SystemParticipantEntityData data,
      java.util.UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics) {
    final boolean dsm = data.getBoolean(DSM);
    final Quantity<Energy> eConsAnnual = data.getQuantity(E_CONS_ANNUAL, StandardUnits.ENERGY_IN);
    final Quantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    final double cosPhi = data.getDouble(COS_PHI);

    return new LoadInput(uuid, id, node, qCharacteristics, dsm, eConsAnnual, sRated, cosPhi);
  }
}
