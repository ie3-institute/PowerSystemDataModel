/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import javax.measure.quantity.Length;
import org.jspecify.annotations.NonNull;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents the installation environment and deployment parameters of a cable. This data describes
 * the concrete installation level of a cable and is associated with some LineInput.
 *
 * @param layoutFormation Layout formation type (e.g., "TREFOIL", "FLAT").
 * @param depthCables Laying depth of the cables from ground level to cable center. We keep the
 *     negative sign for easier integration with Coordinates, thus depthCables must be negative or
 *     zero.
 * @param distanceCables Distance between cable phases/cores from center to center.
 */
public record CableDeploymentInput(
    String layoutFormation,
    ComparableQuantity<Length> depthCables,
    ComparableQuantity<Length> distanceCables) {
  /** Create a new CableDeploymentInput with all required parameters. */
  public CableDeploymentInput {}

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o
        instanceof
        CableDeploymentInput(
            String thatLayoutFormation,
            ComparableQuantity<Length> thatDepthCables,
            ComparableQuantity<Length> thatDistanceCables))) {
      return false;
    }

    return layoutFormation.equals(thatLayoutFormation)
        && depthCables.equals(thatDepthCables)
        && distanceCables.equals(thatDistanceCables);
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
}
