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
 *
 * @param layoutFormation Layout formation type (e.g., "TREFOIL", "FLAT").
 * @param depthCables Laying depth of the cables from ground level to cable center. We keep the
 *     negative sign for easier integration with Coordinates, thus depthCables must be negative.
 * @param distanceCables Distance between cable phases/cores from center to center.
 */
public record CableDeploymentInput(
    String layoutFormation,
    ComparableQuantity<Length> depthCables,
    ComparableQuantity<Length> distanceCables) {
  /**
   * Create a new CableDeploymentInput with all required parameters.
   *
   * @param layoutFormation Layout formation type.
   * @param depthCables Laying depth of the cables from ground level to cable center. We keep the
   *     negative sign for easier integration with Coordinates, thus depthCables must be negative.
   * @param distanceCables Distance between cables/phases from center to center.
   * @throws IllegalArgumentException if validation constraints are violated.
   */
  public CableDeploymentInput { // Validation
    Objects.requireNonNull(layoutFormation, "Layout formation cannot be null");
    Objects.requireNonNull(depthCables, "Depth cables cannot be null");
    Objects.requireNonNull(distanceCables, "Distance cables cannot be null");

    if (layoutFormation.isEmpty()) {
      throw new IllegalArgumentException("Layout formation cannot be empty");
    }

    // Not possible values check
    if (depthCables.getValue().doubleValue() >= 0) {
      throw new IllegalArgumentException("Depth cables must be < 0");
    }
    if (distanceCables.getValue().doubleValue() <= 0) {
      throw new IllegalArgumentException("Distance cables must be > 0");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CableDeploymentInput that)) return false;
    return layoutFormation.equals(that.layoutFormation())
        && depthCables.equals(that.depthCables())
        && distanceCables.equals(that.distanceCables());
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
