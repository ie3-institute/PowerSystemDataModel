/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.system.EvInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the accumulation of electric vehicle participants.
 *
 * @since 1.0
 */
public class SystemParticipants implements InputContainer<SystemParticipantInput> {
  private final Set<EvInput> evs;

  public SystemParticipants(Set<EvInput> evs) {
    this.evs = evs;
  }

  /**
   * Combine different already existing containers.
   *
   * @param systemParticipants already existing containers
   */
  public SystemParticipants(Collection<SystemParticipants> systemParticipants) {
    this.evs =
        systemParticipants.stream()
            .flatMap(participants -> participants.evs.stream())
            .collect(Collectors.toSet());
  }

  /**
   * Create an instance based on a list of {@link SystemParticipantInput} entities.
   *
   * @param systemParticipants list of system participants this container instance should be created
   *     from
   */
  public SystemParticipants(List<SystemParticipantInput> systemParticipants) {
    this.evs =
        systemParticipants.parallelStream()
            .filter(EvInput.class::isInstance)
            .map(EvInput.class::cast)
            .collect(Collectors.toSet());
  }

  @Override
  public final List<SystemParticipantInput> allEntitiesAsList() {
    List<SystemParticipantInput> allEntities = new ArrayList<>(evs);
    return Collections.unmodifiableList(allEntities);
  }

  public SystemParticipantsCopyBuilder copy() {
    return new SystemParticipantsCopyBuilder(this);
  }

  /**
   * @return unmodifiable Set of all electric vehicles in this grid
   */
  public Set<EvInput> getEvs() {
    return Collections.unmodifiableSet(evs);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SystemParticipants that)) return false;
    return Objects.equals(evs, that.evs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(evs);
  }

  /**
   * A builder pattern based approach to create copies of {@link SystemParticipants} containers with
   * altered field values.
   *
   * @since 1.0
   */
  public static class SystemParticipantsCopyBuilder
      extends InputContainerCopyBuilder<SystemParticipantInput> {
    private Set<EvInput> evs;

    protected SystemParticipantsCopyBuilder(SystemParticipants systemParticipants) {
      this.evs = systemParticipants.evs;
    }

    /**
     * Method to alter {@link EvInput}
     *
     * @param evs set of altered electric vehicles
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder evs(Set<EvInput> evs) {
      this.evs = evs;
      return thisInstance();
    }

    @Override
    protected SystemParticipantsCopyBuilder thisInstance() {
      return this;
    }

    @Override
    public SystemParticipants build() {
      return new SystemParticipants(evs);
    }
  }
}
