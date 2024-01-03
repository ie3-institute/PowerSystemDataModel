/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.exceptions.InvalidGridException;
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
      EnergyManagementUnits emUnits,
      GraphicElements graphics)
      throws InvalidGridException {
    super(gridName, rawGrid, systemParticipants, emUnits, graphics);
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

  @Override
  public SubGridContainerCopyBuilder copy() {
    return new SubGridContainerCopyBuilder(this);
  }

  /**
   * A builder pattern based approach to create copies of {@link SubGridContainer} containers with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * SubGridContainer}
   *
   * @version 3.1
   * @since 14.02.23
   */
  public static class SubGridContainerCopyBuilder
      extends GridContainerCopyBuilder<SubGridContainerCopyBuilder> {
    private int subnet;

    /**
     * Constructor for {@link SubGridContainerCopyBuilder}
     *
     * @param subGridContainer instance of {@link SubGridContainer}
     */
    protected SubGridContainerCopyBuilder(SubGridContainer subGridContainer) {
      super(subGridContainer);
      this.subnet = subGridContainer.getSubnet();
    }

    /**
     * Method to alter the subnet number.
     *
     * @param subnet altered subnet number.
     * @return this instance of {@link SubGridContainerCopyBuilder}
     */
    public SubGridContainerCopyBuilder subnet(int subnet) {
      this.subnet = subnet;
      return thisInstance();
    }

    @Override
    protected SubGridContainerCopyBuilder thisInstance() {
      return this;
    }

    @Override
    public SubGridContainer build() throws InvalidGridException {
      return new SubGridContainer(
          getGridName(),
          subnet,
          getRawGrid(),
          getSystemParticipants(),
          getEmUnits(),
          getGraphics());
    }
  }
}
