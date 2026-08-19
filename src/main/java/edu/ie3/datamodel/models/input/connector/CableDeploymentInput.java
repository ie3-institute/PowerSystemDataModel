/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import java.util.Objects;
import javax.measure.quantity.Length;
import org.jspecify.annotations.NonNull;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents the installation environment and deployment parameters of a cable. This data describes
 * the concrete installation level of a cable and is associated with some LineInput.
 */
public class CableDeploymentInput {

  private final String layoutFormation;
  private final ComparableQuantity<Length> depthCables;
  private final ComparableQuantity<Length> distanceCables;

  /**
   * Constructor for the cable deployment.
   *
   * @param layoutFormation Layout formation type (e.g., "TREFOIL", "FLAT").
   * @param depthCables Laying depth of the cables from ground level to cable center. We keep the
   *     negative sign for easier integration with Coordinates, thus depthCables must be negative or
   *     zero.
   * @param distanceCables Distance between cable phases/cores from center to center.
   */
  public CableDeploymentInput(
      String layoutFormation,
      ComparableQuantity<Length> depthCables,
      ComparableQuantity<Length> distanceCables) {
    this.layoutFormation = layoutFormation;
    this.depthCables = depthCables;
    this.distanceCables = distanceCables;
  }

  public String layoutFormation() {
    return layoutFormation;
  }

  public ComparableQuantity<Length> depthCables() {
    return depthCables;
  }

  public ComparableQuantity<Length> distanceCables() {
    return distanceCables;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof CableDeploymentInput)) {
      return false;
    }

    CableDeploymentInput that = (CableDeploymentInput) o;

    return layoutFormation.equals(that.layoutFormation)
        && depthCables.equals(that.depthCables)
        && distanceCables.equals(that.distanceCables);
  }

  @Override
  public @NonNull String toString() {
    return "CableDeploymentInput{"
        + "layoutFormation='"
        + layoutFormation
        + '\''
        + ", depthCables="
        + depthCables
        + ", distanceCables="
        + distanceCables
        + '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(layoutFormation, depthCables, distanceCables);
  }
}
