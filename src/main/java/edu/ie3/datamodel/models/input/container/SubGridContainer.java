/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.ContainerUtils;
import java.util.Objects;

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
      SystemParticipants systemParticipants,
      GraphicElements graphics) {
    super(gridName, rawGrid, systemParticipants, graphics);
    this.subnet = subnet;

    try {
      this.predominantVoltageLevel = ContainerUtils.determinePredominantVoltLvl(rawGrid);
    } catch (InvalidGridException e) {
      throw new InvalidGridException(
          "Cannot build sub grid model for ("
              + gridName
              + ", "
              + subnet
              + "), as the predominant voltage level cannot be determined.");
    }
  }

  public int getSubnet() {
    return subnet;
  }

  public VoltageLevel getPredominantVoltageLevel() {
    return predominantVoltageLevel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SubGridContainer that = (SubGridContainer) o;
    return subnet == that.subnet && predominantVoltageLevel.equals(that.predominantVoltageLevel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), subnet, predominantVoltageLevel);
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
