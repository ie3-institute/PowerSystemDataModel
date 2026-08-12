/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.models.input.UniqueInputEntity;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Length;
import org.jspecify.annotations.NonNull;
import tech.units.indriya.ComparableQuantity;

/**
 * Describes the installation environment and deployment parameters of a cable. This entity is a
 * unique input entity and can be persisted independently (e.g. to CSV). It is associated with a
 * Line via lineUuid.
 */
public class CableDeploymentInput extends UniqueInputEntity {

  private final UUID lineUuid;
  private final String layoutFormation;
  private final ComparableQuantity<Length> depthCables;
  private final ComparableQuantity<Length> distanceCables;

  /**
   * @param uuid unique identifier of this cable deployment
   * @param lineUuid uuid of the connected line
   * @param layoutFormation Layout formation type (e.g., "TREFOIL", "FLAT").
   * @param depthCables Laying depth of the cables from ground level to cable center. We keep the
   *     negative sign for easier integration with Coordinates, thus depthCables must be negative or
   *     zero.
   * @param distanceCables Distance between cable phases/cores from center to center.
   */
  public CableDeploymentInput(
      UUID uuid,
      UUID lineUuid,
      String layoutFormation,
      ComparableQuantity<Length> depthCables,
      ComparableQuantity<Length> distanceCables) {
    super(uuid);
    this.lineUuid = lineUuid;
    this.layoutFormation = layoutFormation;
    this.depthCables = depthCables;
    this.distanceCables = distanceCables;
  }

  public CableDeploymentInput(
      UUID uuid,
      UUID lineUuid,
      String layoutFormation,
      ComparableQuantity<Length> depthCables,
      ComparableQuantity<Length> distanceCables,
      Map<String, String> additionalInformation) {
    super(uuid);
    this.lineUuid = lineUuid;
    this.layoutFormation = layoutFormation;
    this.depthCables = depthCables;
    this.distanceCables = distanceCables;
    setAdditionalInformation(additionalInformation);
  }

  public UUID getLineUuid() {
    return lineUuid;
  }

  public String getLayoutFormation() {
    return layoutFormation;
  }

  public ComparableQuantity<Length> getDepthCables() {
    return depthCables;
  }

  public ComparableQuantity<Length> getDistanceCables() {
    return distanceCables;
  }

  public CableDeploymentInputCopyBuilder copy() {
    return new CableDeploymentInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CableDeploymentInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(lineUuid, that.lineUuid)
        && Objects.equals(layoutFormation, that.layoutFormation)
        && Objects.equals(depthCables, that.depthCables)
        && Objects.equals(distanceCables, that.distanceCables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lineUuid, layoutFormation, depthCables, distanceCables);
  }

  @Override
  public @NonNull String toString() {
    return "CableDeploymentInput{"
        + "uuid="
        + getUuid()
        + ", lineUuid="
        + lineUuid
        + ", layoutFormation='"
        + layoutFormation
        + '\''
        + ", depthCables="
        + depthCables
        + ", distanceCables="
        + distanceCables
        + ", additionalInformation="
        + getAdditionalInformation()
        + '}';
  }

  public static class CableDeploymentInputCopyBuilder
      extends edu.ie3.datamodel.models.UniqueEntity.UniqueEntityCopyBuilder<
          CableDeploymentInputCopyBuilder> {

    private UUID lineUuid;
    private String layoutFormation;
    private ComparableQuantity<Length> depthCables;
    private ComparableQuantity<Length> distanceCables;

    private CableDeploymentInputCopyBuilder(CableDeploymentInput entity) {
      super(entity);
      this.lineUuid = entity.getLineUuid();
      this.layoutFormation = entity.getLayoutFormation();
      this.depthCables = entity.getDepthCables();
      this.distanceCables = entity.getDistanceCables();
    }

    @Override
    public CableDeploymentInput build() {
      return new CableDeploymentInput(
          getUuid(), lineUuid, layoutFormation, depthCables, distanceCables);
    }

    public CableDeploymentInputCopyBuilder lineUuid(UUID lineUuid) {
      this.lineUuid = lineUuid;
      return thisInstance();
    }

    public CableDeploymentInputCopyBuilder layoutFormation(String layoutFormation) {
      this.layoutFormation = layoutFormation;
      return thisInstance();
    }

    public CableDeploymentInputCopyBuilder depthCables(ComparableQuantity<Length> depthCables) {
      this.depthCables = depthCables;
      return thisInstance();
    }

    public CableDeploymentInputCopyBuilder distanceCables(
        ComparableQuantity<Length> distanceCables) {
      this.distanceCables = distanceCables;
      return thisInstance();
    }

    @Override
    protected CableDeploymentInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
