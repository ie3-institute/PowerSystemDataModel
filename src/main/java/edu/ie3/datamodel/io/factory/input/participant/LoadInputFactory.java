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
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public class LoadInputFactory
    extends SystemParticipantInputEntityFactory<LoadInput, SystemParticipantEntityData> {

  public LoadInputFactory() {
    super(LoadInput.class);
  }

  @Override
  protected LoadInput buildModel(
      SystemParticipantEntityData data,
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      OperatorInput operator,
      OperationTime operationTime) {
    PowerProfileKey loadProfile = new PowerProfileKey(data.getField(LOAD_PROFILE));

    final EmInput em = data.getControllingEm().orElse(null);

    final ComparableQuantity<Energy> eConsAnnual =
        data.getQuantity(E_CONS_ANNUAL, StandardUnits.ENERGY_IN);
    final ComparableQuantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    final double cosPhi = data.getDouble(COS_PHI_RATED);

    return new LoadInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        em,
        loadProfile,
        eConsAnnual,
        sRated,
        cosPhi,
        data.getFieldsToValues());
  }
}
