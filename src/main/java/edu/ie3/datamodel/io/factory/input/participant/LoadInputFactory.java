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
import edu.ie3.datamodel.models.input.system.LoadInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.profile.PowerProfileKey;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Factory to create instances of {@link LoadInput}s. */
public class LoadInputFactory extends SystemParticipantInputEntityFactory<LoadInput> {

  public LoadInputFactory(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, EmInput> emUnits) {
    super(operators, nodes, emUnits, LoadInput.class);
  }

  @Override
  protected LoadInput buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime,
      EmInput controllingEm) {
    PowerProfileKey loadProfile = PowerProfileKey.parse(data.getField(LOAD_PROFILE));

    ComparableQuantity<Energy> eConsAnnual =
        getQuantity(data, E_CONS_ANNUAL, StandardUnits.ENERGY_IN);
    ComparableQuantity<Power> sRated = getQuantity(data, S_RATED, StandardUnits.S_RATED);
    double cosPhi = getDouble(data, COS_PHI_RATED);

    return new LoadInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        controllingEm,
        loadProfile,
        eConsAnnual,
        sRated,
        cosPhi,
        data);
  }
}
