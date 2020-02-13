/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.io.factory.SystemParticipantEntityData;
import edu.ie3.io.factory.SystemParticipantInputFactory;
import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.FixedFeedInInput;
import javax.measure.Quantity;
import javax.measure.quantity.Power;

public class FixedFeedInInputEntityFactory
    extends SystemParticipantInputFactory<FixedFeedInInput, SystemParticipantEntityData> {

  private static final String S_RATED = "srated";
  private static final String COSPHI_RATED = "cosphirated";

  public FixedFeedInInputEntityFactory() {
    super(FixedFeedInInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {S_RATED, COSPHI_RATED};
  }

  @Override
  protected FixedFeedInInput buildModel(
      SystemParticipantEntityData data,
      java.util.UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      OperatorInput operatorInput,
      OperationTime operationTime) {
    final Quantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    final double cosPhiRated = Double.parseDouble(data.getField(COSPHI_RATED));

    return new FixedFeedInInput(
        uuid, operationTime, operatorInput, id, node, qCharacteristics, sRated, cosPhiRated);
  }

  @Override
  protected FixedFeedInInput buildModel(
      SystemParticipantEntityData data,
      java.util.UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics) {
    final Quantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    final double cosPhiRated = Double.parseDouble(data.getField(COSPHI_RATED));

    return new FixedFeedInInput(uuid, id, node, qCharacteristics, sRated, cosPhiRated);
  }
}
