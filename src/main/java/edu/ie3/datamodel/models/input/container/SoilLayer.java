/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import static edu.ie3.util.quantities.PowerSystemUnits.METRE;

import edu.ie3.datamodel.models.input.UniqueInputEntity;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Length;
import org.jspecify.annotations.NonNull;
import org.locationtech.jts.geom.Geometry;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Class defining a layer of soil represented by a horizontal geometry (e.g. Polygon) and a vertical
 * interval [zFrom, zTo].
 */
public class SoilLayer extends UniqueInputEntity {

  private final Geometry geometry;
  private final ComparableQuantity<Length> zFrom;
  private final ComparableQuantity<Length> zTo;
  private final UUID soilType;

  /**
   * Constructor of a soil layer.
   *
   * @param uuid unique id of the layer
   * @param geometry horizontal footprint
   * @param zFrom depth where this layer starts
   * @param zTo depth where this layer ends
   * @param soilType uuid of the soil type of this layer
   */
  public SoilLayer(
      UUID uuid,
      Geometry geometry,
      ComparableQuantity<Length> zFrom,
      ComparableQuantity<Length> zTo,
      UUID soilType) {
    this(uuid, geometry, zFrom, zTo, soilType, null);
  }

  /**
   * Constructor of a soil layer with additional information.
   *
   * @param uuid unique id of the layer
   * @param geometry horizontal footprint
   * @param zFrom depth where this layer starts
   * @param zTo depth where this layer ends
   * @param soilType uuid of the soil type of this layer
   * @param additionalInformation additional information; may be {@code null}
   */
  public SoilLayer(
      UUID uuid,
      Geometry geometry,
      ComparableQuantity<Length> zFrom,
      ComparableQuantity<Length> zTo,
      UUID soilType,
      Map<String, String> additionalInformation) {
    super(uuid);
    this.geometry = geometry;
    this.zFrom = zFrom;
    this.zTo = zTo;
    this.soilType = soilType;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Returns the horizontal footprint of this layer.
   *
   * @return the geometry of this layer
   */
  public Geometry getGeometry() {
    return geometry;
  }

  /**
   * Returns the depth at which this layer starts.
   *
   * @return the starting depth of this layer
   */
  public ComparableQuantity<Length> getZFrom() {
    return zFrom;
  }

  /**
   * Returns the depth at which this layer ends.
   *
   * @return the ending depth of this layer
   */
  public ComparableQuantity<Length> getZTo() {
    return zTo;
  }

  /**
   * Returns the uuid of the soil type of this layer.
   *
   * @return the uuid of the soil type of this layer
   */
  public UUID getSoilType() {
    return soilType;
  }

  /**
   * Returns the thickness of this layer as the absolute distance between the given start and end
   * depths.
   *
   * @return the thickness of this layer
   */
  public ComparableQuantity<Length> thickness() {
    ZInterval interval = zInterval();
    return Quantities.getQuantity(interval.upper() - interval.lower(), METRE);
  }

  /**
   * Returns the vertical depth interval of this layer in metres, normalized such that the lower
   * bound is the smaller (deeper) depth and the upper bound the larger one.
   *
   * @return the normalized depth interval of this layer in metres
   * @throws IllegalStateException if {@code zFrom} or {@code zTo} is not set
   */
  public ZInterval zInterval() {
    if (zFrom == null || zTo == null) {
      throw new IllegalStateException(
          "Cannot determine z interval: zFrom and zTo must both be set for SoilLayer '"
              + getUuid()
              + "'.");
    }
    double fromMeters = zFrom.to(METRE).getValue().doubleValue();
    double toMeters = zTo.to(METRE).getValue().doubleValue();
    return new ZInterval(Math.min(fromMeters, toMeters), Math.max(fromMeters, toMeters));
  }

  /**
   * A normalized vertical depth interval in metres, always satisfying {@code lower <= upper}.
   *
   * @param lower the smaller (deeper) depth of the interval in metres
   * @param upper the larger (shallower) depth of the interval in metres
   */
  public record ZInterval(double lower, double upper) {

    /**
     * Checks whether this interval overlaps the given one, i.e. they share more than just a
     * boundary.
     *
     * @param other the interval to compare against
     * @return {@code true} if the two intervals overlap
     */
    public boolean overlaps(ZInterval other) {
      return lower < other.upper() && other.lower() < upper;
    }

    /**
     * Checks whether this interval starts strictly above the given one, i.e. there is a gap between
     * the upper bound of the given interval and the lower bound of this one.
     *
     * @param other the interval that lies below this one
     * @return {@code true} if there is a gap between the two intervals
     */
    public boolean gapAbove(ZInterval other) {
      return lower > other.upper();
    }
  }

  /**
   * Creates a copy builder pre-populated with the current field values of this layer.
   *
   * @return a copy builder for this layer
   */
  public SoilLayerCopyBuilder copy() {
    return new SoilLayerCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SoilLayer that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(geometry, that.geometry)
        && Objects.equals(zFrom, that.zFrom)
        && Objects.equals(zTo, that.zTo)
        && Objects.equals(soilType, that.soilType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), geometry, zFrom, zTo, soilType);
  }

  @Override
  public @NonNull String toString() {
    return "SoilLayer{"
        + "uuid="
        + getUuid()
        + ", geometry="
        + geometry
        + ", zFrom="
        + zFrom
        + ", zTo="
        + zTo
        + ", soilType="
        + soilType
        + ", additionalInformation="
        + getAdditionalInformation()
        + '}';
  }

  public static class SoilLayerCopyBuilder extends UniqueEntityCopyBuilder<SoilLayerCopyBuilder> {

    private Geometry geometry;
    private ComparableQuantity<Length> zFrom;
    private ComparableQuantity<Length> zTo;
    private UUID soilType;
    private Map<String, String> additionalInformation;

    private SoilLayerCopyBuilder(SoilLayer entity) {
      super(entity);
      this.geometry = entity.getGeometry();
      this.zFrom = entity.getZFrom();
      this.zTo = entity.getZTo();
      this.soilType = entity.getSoilType();
      this.additionalInformation = entity.getAdditionalInformation();
    }

    public SoilLayerCopyBuilder geometry(Geometry geometry) {
      this.geometry = geometry;
      return thisInstance();
    }

    public SoilLayerCopyBuilder zFrom(ComparableQuantity<Length> zFrom) {
      this.zFrom = zFrom;
      return thisInstance();
    }

    public SoilLayerCopyBuilder zTo(ComparableQuantity<Length> zTo) {
      this.zTo = zTo;
      return thisInstance();
    }

    public SoilLayerCopyBuilder soilType(UUID soilType) {
      this.soilType = soilType;
      return thisInstance();
    }

    @Override
    protected SoilLayerCopyBuilder thisInstance() {
      return this;
    }

    @Override
    public SoilLayer build() {
      return new SoilLayer(getUuid(), geometry, zFrom, zTo, soilType, additionalInformation);
    }
  }
}
