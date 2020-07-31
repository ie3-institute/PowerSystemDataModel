/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.io.extractor.HasThermalBus;
import edu.ie3.datamodel.io.extractor.HasThermalStorage;
import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.Operable;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.utils.ValidationUtils;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GridContainer implements InputContainer<InputEntity> {
  /** Name of this grid */
  protected final String gridName;
  /** Accumulated raw grid elements (lines, nodes, transformers, switches) */
  protected final RawGridElements rawGrid;
  /** Accumulated system participant elements */
  protected final SystemParticipants systemParticipants;
  /** Accumulated graphic data entities (node graphics, line graphics) */
  protected final GraphicElements graphics;

  protected GridContainer(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      GraphicElements graphics) {
    this.gridName = gridName;

    this.rawGrid = rawGrid;
    this.systemParticipants = systemParticipants;
    this.graphics = graphics;
    validate();
  }

  @Override
  public List<InputEntity> allEntitiesAsList() {
    List<InputEntity> allEntities = new LinkedList<>();
    allEntities.addAll(rawGrid.allEntitiesAsList());
    allEntities.addAll(systemParticipants.allEntitiesAsList());
    allEntities.addAll(graphics.allEntitiesAsList());
    return Collections.unmodifiableList(allEntities);
  }

  /**
   * Flattens the nested structure of the container taking care of avoiding duplicates
   *
   * @return A List of {@link InputEntity}s as a flattened representation of the containers nested
   *     structure
   */
  public List<InputEntity> flatten() {
    List<InputEntity> allEntities = allEntitiesAsList();

    /* Only types, thermal busses and storages as well as operators have to be extracted */
    List<InputEntity> operators =
        allEntities.stream()
            .filter(entity -> Operable.class.isAssignableFrom(entity.getClass()))
            .map(operable -> ((Operable) operable).getOperator())
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    List<InputEntity> types =
        allEntities.stream()
            .filter(entity -> HasType.class.isAssignableFrom(entity.getClass()))
            .map(hasType -> ((HasType) hasType).getType())
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    List<InputEntity> thermalBusses =
        allEntities.stream()
            .filter(entity -> HasThermalBus.class.isAssignableFrom(entity.getClass()))
            .map(hasThermalBus -> ((HasThermalBus) hasThermalBus).getThermalBus())
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    List<InputEntity> thermalStorages =
        allEntities.stream()
            .filter(entity -> HasThermalStorage.class.isAssignableFrom(entity.getClass()))
            .map(hasThermalStorage -> ((HasThermalStorage) hasThermalStorage).getThermalStorage())
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

    /* Put everything together */
    return Stream.of(types, operators, thermalBusses, thermalStorages, allEntities)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  @Override
  public void validate() {
    // sanity check to ensure distinct UUIDs
    Optional<String> exceptionString =
        ValidationUtils.checkForDuplicateUuids(new HashSet<>(this.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      throw new InvalidGridException(
          "The provided entities in '"
              + this.getClass().getSimpleName()
              + "' contains duplicate UUIDs. "
              + "This is not allowed!\nDuplicated uuids:\n\n"
              + exceptionString);
    }

    ValidationUtils.checkGrid(this);
  }

  /**
   * @return true, as we are positive people and believe in what we do. Just kidding. Checks are
   *     made during initialisation.
   */
  public String getGridName() {
    return gridName;
  }

  public RawGridElements getRawGrid() {
    return rawGrid;
  }

  public SystemParticipants getSystemParticipants() {
    return systemParticipants;
  }

  public GraphicElements getGraphics() {
    return graphics;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GridContainer that = (GridContainer) o;
    return gridName.equals(that.gridName)
        && rawGrid.equals(that.rawGrid)
        && systemParticipants.equals(that.systemParticipants)
        && graphics.equals(that.graphics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gridName, rawGrid, systemParticipants, graphics);
  }

  @Override
  public String toString() {
    return "GridContainer{" + "gridName='" + gridName + '\'' + '}';
  }
}
