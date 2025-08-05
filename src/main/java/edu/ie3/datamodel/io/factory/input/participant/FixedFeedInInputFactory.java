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
import edu.ie3.datamodel.models.input.system.FixedFeedInInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** The type Fixed feed in input factory. */
public class FixedFeedInInputFactory
    extends SystemParticipantInputEntityFactory<FixedFeedInInput, SystemParticipantEntityData> {

  private static final String S_RATED = "sRated";
  private static final String COSPHI_RATED = "cosPhiRated";

  /** Instantiates a new Fixed feed in input factory. */
  public FixedFeedInInputFactory() {
    super(FixedFeedInInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {S_RATED, COSPHI_RATED};
  }

  @Override
  protected FixedFeedInInput buildModel(
      SystemParticipantEntityData data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {
    final EmInput em = data.getControllingEm().orElse(null);
    final ComparableQuantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    final double cosPhiRated = data.getDouble(COSPHI_RATED);

    return new FixedFeedInInput(
        uuid, id, operator, operationTime, node, qCharacteristics, em, sRated, cosPhiRated);
  }
}
