/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.aggregated;

import edu.ie3.models.UniqueEntity;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Represents the aggregation of all data needed to create a complete single grid */
public class AggregatedGridInput implements AggregatedEntities {

  /** Name of this grid */
  private final String gridName;
  /** subnet number of this grid */
  private final int subnet;
  /** Voltlevel of this grid */
  private final String voltLvl;

  /** Aggregated raw grid elements (lines, nodes, transformers, switches) */
  private final RawGridElements rawGrid;
  /** Aggregated system participant elements */
  private final SystemParticipantElements systemParticipants;
  /** Aggregated graphic data entities (node graphics, line graphics) */
  private final GraphicElements graphics;

  public AggregatedGridInput(
      String gridName,
      int subnet,
      String voltLvl,
      RawGridElements rawGrid,
      SystemParticipantElements systemParticipants,
      GraphicElements graphics) {
    this.gridName = gridName;
    this.subnet = subnet;
    this.voltLvl = voltLvl;
    this.rawGrid = rawGrid;
    this.systemParticipants = systemParticipants;
    this.graphics = graphics;
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

  public String getVoltLvl() {
    return voltLvl;
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
}
