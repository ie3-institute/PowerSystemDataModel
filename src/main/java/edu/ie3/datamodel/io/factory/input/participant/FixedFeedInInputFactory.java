/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.io.factory.input.UntypedSingleNodeEntityData;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.FixedFeedInInput;
import javax.measure.quantity.Power;
import tec.uom.se.ComparableQuantity;

public class FixedFeedInInputFactory
    extends SystemParticipantInputEntityFactory<FixedFeedInInput, UntypedSingleNodeEntityData> {

  private static final String S_RATED = "srated";
  private static final String COSPHI_RATED = "cosphirated";

  public FixedFeedInInputFactory() {
    super(FixedFeedInInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {S_RATED, COSPHI_RATED};
  }

  @Override
  protected FixedFeedInInput buildModel(
      UntypedSingleNodeEntityData data,
      java.util.UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {
    final ComparableQuantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    final double cosPhiRated = data.getDouble(COSPHI_RATED);

    return new FixedFeedInInput(
        uuid, id, operator, operationTime, node, qCharacteristics, sRated, cosPhiRated);
  }
}
