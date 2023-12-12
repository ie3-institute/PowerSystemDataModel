/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.system.EmInput;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Represents the accumulation of energy management units */
public class EnergyManagementUnits {

  protected final Map<UUID, EmInput> emUnits;

  public EnergyManagementUnits(Set<EmInput> emUnits) {
    this.emUnits =
        emUnits.stream().collect(Collectors.toMap(EmInput::getUuid, Function.identity()));
  }

  /**
   * Combine different already existing containers
   *
   * @param emUnits already existing containers
   */
  public EnergyManagementUnits(Collection<EnergyManagementUnits> emUnits) {
    this.emUnits =
        emUnits.stream()
            .flatMap(units -> units.emUnits.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Set<EmInput> getEmUnits() {
    return new HashSet<>(emUnits.values());
  }

  // TODO useful once #957 is implemented
  public Map<UUID, EmInput> getEmUnitsMap() {
    return emUnits;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EnergyManagementUnits that)) return false;
    return Objects.equals(emUnits, that.emUnits);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emUnits);
  }
}
