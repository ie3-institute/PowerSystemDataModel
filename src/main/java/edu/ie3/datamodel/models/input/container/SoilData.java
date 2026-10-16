/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.UniqueInputEntity;
import java.util.*;

/** Immutable holder for soil related input entities (soil types and soil layers). */
public class SoilData {
  private final Set<SoilType> soilTypes;
  private final Set<SoilLayer> soilLayers;

  /**
   * Constructor for a soil data holder with soil types only.
   *
   * @param soilTypes the soil types; may be {@code null}
   */
  public SoilData(Set<SoilType> soilTypes) {
    this(soilTypes, Collections.emptySet());
  }

  /**
   * Constructor for a soil data holder.
   *
   * @param soilTypes the soil types; may be {@code null}
   * @param soilLayers the soil layers; may be {@code null}
   */
  public SoilData(Set<SoilType> soilTypes, Set<SoilLayer> soilLayers) {
    this.soilTypes = soilTypes == null ? Collections.emptySet() : Set.copyOf(soilTypes);
    this.soilLayers = soilLayers == null ? Collections.emptySet() : Set.copyOf(soilLayers);
  }

  /**
   * Returns the soil types held by this instance.
   *
   * @return an unmodifiable set of soil types
   */
  public Set<SoilType> getSoilTypes() {
    return soilTypes;
  }

  /**
   * Returns the soil layers held by this instance.
   *
   * @return an unmodifiable set of soil layers
   */
  public Set<SoilLayer> getSoilLayers() {
    return soilLayers;
  }

  /**
   * Returns all soil related entities (types and layers) as a list of UniqueInputEntity. The
   * returned list is unmodifiable.
   */
  public List<UniqueInputEntity> allEntitiesAsList() {
    List<UniqueInputEntity> entities = new ArrayList<>();
    entities.addAll(soilTypes);
    entities.addAll(soilLayers);
    return Collections.unmodifiableList(entities);
  }

  public static SoilData empty() {
    return new SoilData(Collections.emptySet(), Collections.emptySet());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SoilData)) return false;
    SoilData soilData = (SoilData) o;
    return Objects.equals(soilTypes, soilData.soilTypes)
        && Objects.equals(soilLayers, soilData.soilLayers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(soilTypes, soilLayers);
  }

  @Override
  public String toString() {
    return "SoilData{" + "soilTypes=" + soilTypes + ", soilLayers=" + soilLayers + '}';
  }
}
