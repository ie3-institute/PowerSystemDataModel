/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/** Interface defining a power profile. */
public final class PowerProfileKey implements Serializable {

  private final Type type;
  private final String value;
  public final boolean noKeyAssigned;

  public PowerProfileKey(String key) {
    this(key, Type.TS);
  }

  public PowerProfileKey(String key, Type type) {
    this.type = type;
    if (key == null || key.isEmpty()) {
      this.value = "No profile assigned.";
      this.noKeyAssigned = true;
    } else {
      this.value = toUniformKey(key);
      this.noKeyAssigned = false;
    }
  }

  public String getValue() {
    return value;
  }

  public Type getType() {
    return type;
  }

  public boolean equals(PowerProfile other) {
    return equals(other.getKey());
  }

  public boolean equalsAny(PowerProfile... others) {
    return Arrays.stream(others).anyMatch(this::equals);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    PowerProfileKey that = (PowerProfileKey) o;
    return noKeyAssigned == that.noKeyAssigned
        && type == that.type
        && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, value, noKeyAssigned);
  }

  @Override
  public String toString() {
    return "PowerProfileKey{"
        + "type="
        + type
        + ", value='"
        + value
        + '\''
        + ", noKeyAssigned="
        + noKeyAssigned
        + '}';
  }

  // static

  public static final PowerProfileKey NO_KEY_ASSIGNED = new PowerProfileKey("", Type.TS);

  public static String toUniformKey(String key) {
    return key.toLowerCase().replaceAll("[-_]*", "");
  }

  public enum Type {
    TS,
    MARKOV
  }
}
