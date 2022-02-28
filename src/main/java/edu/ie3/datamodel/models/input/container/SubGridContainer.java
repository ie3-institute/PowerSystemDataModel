/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.ContainerUtils;
import java.util.Objects;

/**
 * Represents the accumulation of all data needed to create one galvanically complete single grid
 */
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
    this.predominantVoltageLevel = ContainerUtils.determinePredominantVoltLvl(rawGrid, subnet);
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
    if (!(o instanceof SubGridContainer container)) return false;
    if (!super.equals(o)) return false;
    return subnet == container.subnet
        && predominantVoltageLevel.equals(container.predominantVoltageLevel);
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
