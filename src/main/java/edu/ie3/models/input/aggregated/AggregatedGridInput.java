/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.aggregated;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.MeasurementUnitInput;
import edu.ie3.utils.ValidationUtils;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Represents the aggregation of all data needed to create a complete grid */
public class AggregatedGridInput implements AggregatedEntities {

  /** Name of this grid */
  private String gridName;
  /** subnet number of this grid */
  private int subnet;
  /** Voltlevel of this grid */
  private String voltLvl;

  /** Aggregated raw grid elements (lines, nodes, transformers, switches) */
  private AggregatedRawGridInput rawGrid;
  /** Aggregated system participant elements */
  private AggregatedSystemInput systemParticipants;
  /** Aggregated graphic data entities (node graphics, line graphics) */
  private AggregatedGraphicInput graphics;

  /** Measurement units in this grid */
  private List<MeasurementUnitInput> measurementUnits = new LinkedList<>();

  @Override
  public void add(UniqueEntity entity) {
    if (entity instanceof MeasurementUnitInput) {
      measurementUnits.add((MeasurementUnitInput) entity);
      return;
    }
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
    allEntities.addAll(measurementUnits);
    return Collections.unmodifiableList(allEntities);
  }

  @Override
  public boolean areValuesValid() {
    if (!rawGrid.areValuesValid()) return false;
    if (!systemParticipants.areValuesValid()) return false;
    if (!graphics.areValuesValid()) return false;
    for (MeasurementUnitInput measurementUnit : measurementUnits) {
      if (!ValidationUtils.checkMeasurementUnit(measurementUnit)) return false;
    }
    return true;
  }

  public String getGridName() {
    return gridName;
  }

  public void setGridName(String gridName) {
    this.gridName = gridName;
  }

  public int getSubnet() {
    return subnet;
  }

  public void setSubnet(int subnet) {
    this.subnet = subnet;
  }

  public String getVoltLvl() {
    return voltLvl;
  }

  public void setVoltLvl(String voltLvl) {
    this.voltLvl = voltLvl;
  }

  public AggregatedRawGridInput getRawGrid() {
    return rawGrid;
  }

  public void setRawGrid(AggregatedRawGridInput rawGrid) {
    this.rawGrid = rawGrid;
  }

  public AggregatedSystemInput getSystemParticipants() {
    return systemParticipants;
  }

  public void setSystemParticipants(AggregatedSystemInput systemParticipants) {
    this.systemParticipants = systemParticipants;
  }

  public AggregatedGraphicInput getGraphics() {
    return graphics;
  }

  public void setGraphics(AggregatedGraphicInput graphics) {
    this.graphics = graphics;
  }

  public List<MeasurementUnitInput> getMeasurementUnits() {
    return measurementUnits;
  }

  public void setMeasurementUnits(List<MeasurementUnitInput> measurementUnits) {
    this.measurementUnits = measurementUnits;
  }
}
