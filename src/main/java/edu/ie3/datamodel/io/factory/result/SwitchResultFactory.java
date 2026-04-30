/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.result.connector.SwitchResult;
import java.time.ZonedDateTime;
import java.util.UUID;

public class SwitchResultFactory extends ResultEntityFactory<SwitchResult> {

  public SwitchResultFactory() {
    super(SwitchResult.class);
  }

  @Override
  protected SwitchResult buildModel(EntityData data) {
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
    UUID inputModel = data.getUUID(INPUT_MODEL);

    final boolean closed = data.getBoolean(CLOSED);

    return new SwitchResult(time, inputModel, closed);
  }
}
