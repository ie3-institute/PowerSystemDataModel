/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.LoadInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.profile.LoadProfile;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;

public class LoadInputFactory
    extends SystemParticipantInputEntityFactory<LoadInput, SystemParticipantEntityData> {
  private static final Logger logger = LoggerFactory.getLogger(LoadInputFactory.class);

  private static final String LOAD_PROFILE = "loadprofile";
  private static final String DSM = "dsm";
  private static final String E_CONS_ANNUAL = "econsannual";
  private static final String S_RATED = "srated";
  private static final String COS_PHI = "cosphirated";

  public LoadInputFactory() {
    super(LoadInput.class);
  }

  @Override
  protected String[] getAdditionalFields() {
    return new String[] {LOAD_PROFILE, DSM, E_CONS_ANNUAL, S_RATED, COS_PHI};
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
    LoadProfile loadProfile;
    try {
      loadProfile = LoadProfile.parse(data.getField(LOAD_PROFILE));
    } catch (ParsingException e) {
      logger.warn(
          "Cannot parse the standard load profile \"{}\" of load \"{}\". Assign no load profile instead.",
          data.getField(LOAD_PROFILE),
          id);
      loadProfile = LoadProfile.DefaultLoadProfiles.NO_LOAD_PROFILE;
    }
    final EmInput em = data.getEm().orElse(null);
    final boolean dsm = data.getBoolean(DSM);
    final ComparableQuantity<Energy> eConsAnnual =
        data.getQuantity(E_CONS_ANNUAL, StandardUnits.ENERGY_IN);
    final ComparableQuantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    final double cosPhi = data.getDouble(COS_PHI);

    return new LoadInput(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        em,
        loadProfile,
        dsm,
        eConsAnnual,
        sRated,
        cosPhi);
  }
}
