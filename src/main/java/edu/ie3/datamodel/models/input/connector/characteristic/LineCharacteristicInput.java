/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.characteristic;

import edu.ie3.datamodel.models.input.InputEntity;
import java.util.Objects;
import java.util.UUID;

public class LineCharacteristicInput extends InputEntity {

  protected final String characteristic;
  protected static final String DEFAULT_CHARACTERISTIC = "";

  public LineCharacteristicInput(UUID uuid, String characteristic) {
    super(uuid);
    this.characteristic = characteristic;
  }

  public LineCharacteristicInput(UUID uuid) {
    super(uuid);
    this.characteristic = DEFAULT_CHARACTERISTIC;
  }

  public String getCharacteristic() {
    return characteristic;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LineCharacteristicInput that = (LineCharacteristicInput) o;
    return Objects.equals(characteristic, that.characteristic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), characteristic);
  }

  @Override
  public String toString() {
    return "LineCharacteristicInput{" + "characteristic='" + characteristic + '\'' + '}';
  }
}
