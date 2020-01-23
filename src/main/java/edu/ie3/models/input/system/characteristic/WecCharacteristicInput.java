/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system.characteristic;

import java.util.Objects;
import java.util.UUID;

/** Characteristic mapping the wind velocity to its corresponding Betz coefficient */
public class WecCharacteristicInput extends AssetCharacteristicInput {
  /** Curve of the Betz coefficient as semicolon-separated String */
  String cpCharacteristic;

  /**
   * @param uuid of the input entity
   * @param type of this characteristic
   * @param cpCharacteristic Power curve as semicolon-separated String
   */
  public WecCharacteristicInput(UUID uuid, String type, String cpCharacteristic) {
    super(uuid, type);
    this.cpCharacteristic = cpCharacteristic;
  }

  public String getCpCharacteristic() {
    return cpCharacteristic;
  }

  public void setCpCharacteristic(String cpCharacteristic) {
    this.cpCharacteristic = cpCharacteristic;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    WecCharacteristicInput that = (WecCharacteristicInput) o;
    return Objects.equals(cpCharacteristic, that.cpCharacteristic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), cpCharacteristic);
  }
}
