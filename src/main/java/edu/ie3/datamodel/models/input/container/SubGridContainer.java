/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.exceptions.AggregationException;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;

/** Represents the accumulation of all data needed to create a complete single grid */
public class SubGridContainer extends GridContainer {
  /** subnet number of this grid */
  private final int subnet;
  /** Predominantly apparent voltage level in this single grid */
  private final VoltageLevel predominantVoltageLevel;

  public SubGridContainer(
      String gridName,
      int subnet,
      RawGridElements rawGrid,
      SystemParticipantElements systemParticipants,
      GraphicElements graphics)
      throws AggregationException {
    super(gridName, rawGrid, systemParticipants, graphics);
    this.subnet = subnet;

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

  public int getSubnet() {
    return subnet;
  }

  public VoltageLevel getPredominantVoltageLevel() {
    return predominantVoltageLevel;
  }

  @Override
  public String toString() {
    return "SubGridContainer{"
        + "gridName='"
        + gridName
        + '\''
        + ", subnet="
        + subnet
        + ", predominantVoltageLevel="
        + predominantVoltageLevel
        + '}';
  }
}
