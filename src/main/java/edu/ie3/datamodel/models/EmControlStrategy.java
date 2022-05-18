/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public enum EmControlStrategy implements ControlStrategy {
  SELF_OPTIMIZATION("self_optimization");

  private final String key;

  EmControlStrategy(String key) {
    this.key = key.toLowerCase(Locale.ROOT);
  }

  public static EmControlStrategy get(String key) {
    return Arrays.stream(EmControlStrategy.values())
        .filter(controlStrategy -> controlStrategy.key.equalsIgnoreCase(key))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "No predefined energy management control strategy '"
                        + key
                        + "' found. Please provide one of the following keys: "
                        + Arrays.stream(EmControlStrategy.values())
                            .map(EmControlStrategy::getKey)
                            .collect(Collectors.joining(", "))));
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String toString() {
    return "EmControlStrategy{" + "key='" + key + '\'' + '}';
  }
}
