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
public class EnergyManagementUnits implements InputContainer<EmInput> {

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

  @Override
  public List<EmInput> allEntitiesAsList() {
    return emUnits.values().stream().toList();
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

  @Override
  public EnergyManagementUnitsCopyBuilder copy() {
    return new EnergyManagementUnitsCopyBuilder(this);
  }

  /**
   * A builder pattern based approach to create copies of {@link EnergyManagementUnits} containers
   * with altered field values. For detailed field descriptions refer to java docs of {@link
   * EnergyManagementUnits}
   */
  public static class EnergyManagementUnitsCopyBuilder
      implements InputContainerCopyBuilder<EmInput> {
    protected Set<EmInput> emUnits;

    /**
     * Constructor for {@link EnergyManagementUnits.EnergyManagementUnitsCopyBuilder}
     *
     * @param energyManagementUnits instance of {@link EnergyManagementUnits}
     */
    protected EnergyManagementUnitsCopyBuilder(EnergyManagementUnits energyManagementUnits) {
      this.emUnits = energyManagementUnits.getEmUnits();
    }

    /**
     * Method to alter the {@link EmInput}s
     *
     * @param emUnits set of altered {@link EmInput}s
     * @return this instance of {@link EnergyManagementUnits.EnergyManagementUnitsCopyBuilder}
     */
    public EnergyManagementUnits.EnergyManagementUnitsCopyBuilder emUnits(Set<EmInput> emUnits) {
      this.emUnits = emUnits;
      return this;
    }

    @Override
    public EnergyManagementUnits build() {
      return new EnergyManagementUnits(emUnits);
    }
  }
}
