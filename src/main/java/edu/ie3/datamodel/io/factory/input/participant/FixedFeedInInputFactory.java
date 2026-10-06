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
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Power;
import java.util.Map;
import java.util.UUID;

/** Factory to create instances of {@link FixedFeedInInput}s. */
public class FixedFeedInInputFactory extends SystemParticipantInputEntityFactory<FixedFeedInInput> {

  public FixedFeedInInputFactory(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits) {
    super(operators, nodes, emUnits, FixedFeedInInput.class);
  }

  @Override
  protected FixedFeedInInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime,
      EmInput controllingEm) {
    ComparableQuantity<Power> sRated = getQuantity(data, S_RATED, StandardUnits.S_RATED);
    double cosPhiRated = getDouble(data, COS_PHI_RATED);

    return new FixedFeedInInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        controllingEm,
        sRated,
        cosPhiRated,
        data);
  }
}
