/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.models.result.connector.SwitchResult;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

public class SwitchResultFactory extends ResultEntityFactory<SwitchResult, SwitchResult> {

  public SwitchResultFactory() {
    super(SwitchResult.class);
  }

  @Override
  protected SwitchResult buildModel(Map<String, String> data, ZonedDateTime time, UUID inputModel) {
    boolean closed = getBoolean(data, CLOSED);
    return new SwitchResult(time, inputModel, closed);
  }
}
