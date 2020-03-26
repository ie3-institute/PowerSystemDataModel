/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.models.input.system.type.WecTypeInput;
import java.util.UUID;

/** Characteristic mapping the wind velocity to its corresponding Betz coefficient */
public class WecCharacteristicInput extends AssetCharacteristicInput<WecTypeInput> {
  /**
   * @param uuid of the input entity
   * @param type of this characteristic
   * @param characteristic Curve of the Betz coefficient in the form "{(v1,cp1),(v2,cp2), ...}"
   */
  public WecCharacteristicInput(UUID uuid, WecTypeInput type, String characteristic) {
    super(uuid, type, characteristic);
  }

  @Override
  public String toString() {
    return "WecCharacteristicInput{"
        + "type="
        + type
        + ", characteristic='"
        + characteristic
        + '\''
        + '}';
  }
}
