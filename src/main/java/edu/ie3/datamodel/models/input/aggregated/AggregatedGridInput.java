/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.aggregated;

import edu.ie3.datamodel.exceptions.AggregationException;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.*;
import java.util.stream.Collectors;

/** Represents the aggregation of all data needed to create a complete single grid */
public class AggregatedGridInput implements AggregatedEntities {

  /** Name of this grid */
  private final String gridName;
  /** subnet number of this grid */
  private final int subnet;

  private final VoltageLevel predominantVoltageLevel;

  /** Aggregated raw grid elements (lines, nodes, transformers, switches) */
  private final RawGridElements rawGrid;
  /** Aggregated system participant elements */
  private final SystemParticipantElements systemParticipants;
  /** Aggregated graphic data entities (node graphics, line graphics) */
  private final GraphicElements graphics;

  public AggregatedGridInput(
      String gridName,
      int subnet,
      RawGridElements rawGrid,
      SystemParticipantElements systemParticipants,
      GraphicElements graphics)
      throws AggregationException {
    this.gridName = gridName;
    this.subnet = subnet;
    this.rawGrid = rawGrid;
    this.systemParticipants = systemParticipants;
    this.graphics = graphics;

    try {
      this.predominantVoltageLevel = determinePredominantVoltLvl(rawGrid);
    } catch (AggregationException e) {
      throw new AggregationException(
          "Cannot build aggregated input model for ("
              + gridName
              + ", "
              + subnet
              + "), as the predominant voltage level cannot be determined.",
          e);
    }
  }

  @Override
  public void add(UniqueEntity entity) {
    try {
      rawGrid.add(entity);
      return;
    } catch (IllegalArgumentException ignored) { // No further exception handling needed
    }
    try {
      systemParticipants.add(entity);
      return;
    } catch (IllegalArgumentException ignored) { // No further exception handling needed
    }
    try {
      graphics.add(entity);
      return;
    } catch (IllegalArgumentException ignored) { // No further exception handling needed
    }
    throw new IllegalArgumentException(
        "Entity type is unknown, cannot add entity [" + entity + "]");
  }

  @Override
  public List<UniqueEntity> allEntitiesAsList() {
    List<UniqueEntity> allEntities = new LinkedList<>();
    allEntities.addAll(rawGrid.allEntitiesAsList());
    allEntities.addAll(systemParticipants.allEntitiesAsList());
    allEntities.addAll(graphics.allEntitiesAsList());
    return Collections.unmodifiableList(allEntities);
  }

  @Override
  public boolean areValuesValid() {
    if (!rawGrid.areValuesValid()) return false;
    if (!systemParticipants.areValuesValid()) return false;
    return graphics.areValuesValid();
  }

  public String getGridName() {
    return gridName;
  }

  public int getSubnet() {
    return subnet;
  }

  public VoltageLevel getPredominantVoltageLevel() {
    return predominantVoltageLevel;
  }

  public RawGridElements getRawGrid() {
    return rawGrid;
  }

  public SystemParticipantElements getSystemParticipants() {
    return systemParticipants;
  }

  public GraphicElements getGraphics() {
    return graphics;
  }

  /**
   * Determining the predominant voltage level in this grid by counting the occurrences of the
   * different voltage levels
   *
   * @param rawGrid Raw grid elements
   * @return The predominant voltage level in this grid
   * @throws AggregationException If not a single, predominant voltage level can be determined
   */
  private static VoltageLevel determinePredominantVoltLvl(RawGridElements rawGrid)
      throws AggregationException {
    return rawGrid.getNodes().stream()
        .map(NodeInput::getVoltLvl)
        .collect(Collectors.groupingBy(voltLvl -> voltLvl, Collectors.counting()))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElseThrow(
            () -> new AggregationException("Cannot determine the predominant voltage level."));
  }

  @Override
  public String toString() {
    return "AggregatedGridInput{" +
            "gridName='" + gridName + '\'' +
            ", subnet=" + subnet +
            ", predominantVoltageLevel=" + predominantVoltageLevel +
            ", rawGrid=" + rawGrid +
            ", systemParticipants=" + systemParticipants +
            ", graphics=" + graphics +
            '}';
  }
}
