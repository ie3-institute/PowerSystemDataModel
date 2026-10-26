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
  private final ComparableQuantity<Length> cableBedWidth;
  private final ComparableQuantity<Length> cableBedHeight;
  private final UUID cableBedSoilType;

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
    this(uuid, lineUuid, layoutFormation, depthCables, distanceCables, null, null, null);
  }

  public CableDeploymentInput(
      UUID uuid,
      UUID lineUuid,
      String layoutFormation,
      ComparableQuantity<Length> depthCables,
      ComparableQuantity<Length> distanceCables,
      Map<String, String> additionalInformation) {
    this(
        uuid,
        lineUuid,
        layoutFormation,
        depthCables,
        distanceCables,
        null,
        null,
        null,
        additionalInformation);
  }

  /**
   * @param uuid unique identifier of this cable deployment
   * @param lineUuid uuid of the connected line
   * @param layoutFormation Layout formation type (e.g., "TREFOIL", "FLAT").
   * @param depthCables Laying depth of the cables from ground level to cable center.
   * @param distanceCables Distance between cable phases/cores from center to center.
   * @param cableBedWidth Width of the rectangular cable bed centered around the middle cable in a
   *     flat parallel formation or the center of a trefoil formation.
   * @param cableBedHeight Height of the rectangular cable bed centered around the middle cable in a
   *     flat parallel formation or the center of a trefoil formation.
   * @param cableBedSoilType UUID of the soil type used for the cable bed.
   */
  public CableDeploymentInput(
      UUID uuid,
      UUID lineUuid,
      String layoutFormation,
      ComparableQuantity<Length> depthCables,
      ComparableQuantity<Length> distanceCables,
      ComparableQuantity<Length> cableBedWidth,
      ComparableQuantity<Length> cableBedHeight,
      UUID cableBedSoilType) {
    this(
        uuid,
        lineUuid,
        layoutFormation,
        depthCables,
        distanceCables,
        cableBedWidth,
        cableBedHeight,
        cableBedSoilType,
        null);
  }

  public CableDeploymentInput(
      UUID uuid,
      UUID lineUuid,
      String layoutFormation,
      ComparableQuantity<Length> depthCables,
      ComparableQuantity<Length> distanceCables,
      ComparableQuantity<Length> cableBedWidth,
      ComparableQuantity<Length> cableBedHeight,
      UUID cableBedSoilType,
      Map<String, String> additionalInformation) {
    super(uuid);
    this.lineUuid = lineUuid;
    this.layoutFormation = layoutFormation;
    this.depthCables = depthCables;
    this.distanceCables = distanceCables;
    this.cableBedWidth = cableBedWidth;
    this.cableBedHeight = cableBedHeight;
    this.cableBedSoilType = cableBedSoilType;
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

  public ComparableQuantity<Length> getCableBedWidth() {
    return cableBedWidth;
  }

  public ComparableQuantity<Length> getCableBedHeight() {
    return cableBedHeight;
  }

  public UUID getCableBedSoilType() {
    return cableBedSoilType;
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
        && Objects.equals(distanceCables, that.distanceCables)
        && Objects.equals(cableBedWidth, that.cableBedWidth)
        && Objects.equals(cableBedHeight, that.cableBedHeight)
        && Objects.equals(cableBedSoilType, that.cableBedSoilType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        lineUuid,
        layoutFormation,
        depthCables,
        distanceCables,
        cableBedWidth,
        cableBedHeight,
        cableBedSoilType);
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
        + ", cableBedWidth="
        + cableBedWidth
        + ", cableBedHeight="
        + cableBedHeight
        + ", cableBedSoilType="
        + cableBedSoilType
        + ", additionalInformation="
        + getAdditionalInformation()
        + '}';
  }

  public static class CableDeploymentInputCopyBuilder
      extends UniqueInputEntityCopyBuilder<CableDeploymentInputCopyBuilder> {

    private UUID lineUuid;
    private String layoutFormation;
    private ComparableQuantity<Length> depthCables;
    private ComparableQuantity<Length> distanceCables;
    private ComparableQuantity<Length> cableBedWidth;
    private ComparableQuantity<Length> cableBedHeight;
    private UUID cableBedSoilType;

    private CableDeploymentInputCopyBuilder(CableDeploymentInput entity) {
      super(entity);
      this.lineUuid = entity.getLineUuid();
      this.layoutFormation = entity.getLayoutFormation();
      this.depthCables = entity.getDepthCables();
      this.distanceCables = entity.getDistanceCables();
      this.cableBedWidth = entity.getCableBedWidth();
      this.cableBedHeight = entity.getCableBedHeight();
      this.cableBedSoilType = entity.getCableBedSoilType();
    }

    @Override
    public CableDeploymentInput build() {
      return new CableDeploymentInput(
          getUuid(),
          lineUuid,
          layoutFormation,
          depthCables,
          distanceCables,
          cableBedWidth,
          cableBedHeight,
          cableBedSoilType,
          getAdditionalInformation());
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

    public CableDeploymentInputCopyBuilder cableBedWidth(ComparableQuantity<Length> cableBedWidth) {
      this.cableBedWidth = cableBedWidth;
      return thisInstance();
    }

    public CableDeploymentInputCopyBuilder cableBedHeight(
        ComparableQuantity<Length> cableBedHeight) {
      this.cableBedHeight = cableBedHeight;
      return thisInstance();
    }

    public CableDeploymentInputCopyBuilder cableBedSoilType(UUID cableBedSoilType) {
      this.cableBedSoilType = cableBedSoilType;
      return thisInstance();
    }

    @Override
    protected CableDeploymentInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
