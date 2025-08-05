/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type.chargingpoint;

import edu.ie3.datamodel.models.ElectricCurrentType;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * The actual implementation {@link edu.ie3.datamodel.models.input.system.EvcsInput} types. Default
 * type implementations as well as methods to parse a type from a string can be found in {@link
 * ChargingPointTypeUtils}*
 *
 * @version 0.1
 * @since 25.07.20
 */
public class ChargingPointType implements Serializable {

  private final String id;
  private final ComparableQuantity<Power> sRated;
  private final ElectricCurrentType electricCurrentType;

  private final Set<String> synonymousIds;

  /**
   * Instantiates a new Charging point type.
   *
   * @param id the id
   * @param sRated the s rated
   * @param electricCurrentType the electric current type
   */
  public ChargingPointType(
      String id, ComparableQuantity<Power> sRated, ElectricCurrentType electricCurrentType) {
    this.id = id;
    this.sRated = sRated;
    this.electricCurrentType = electricCurrentType;
    this.synonymousIds = new HashSet<>();
  }

  /**
   * Instantiates a new Charging point type.
   *
   * @param id the id
   * @param sRated the s rated
   * @param electricCurrentType the electric current type
   * @param synonymousIds the synonymous ids
   */
  public ChargingPointType(
      String id,
      ComparableQuantity<Power> sRated,
      ElectricCurrentType electricCurrentType,
      Set<String> synonymousIds) {
    this.id = id;
    this.sRated = sRated;
    this.electricCurrentType = electricCurrentType;
    this.synonymousIds = synonymousIds;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Gets rated.
   *
   * @return the rated
   */
  public ComparableQuantity<Power> getsRated() {
    return sRated;
  }

  /**
   * Gets electric current type.
   *
   * @return the electric current type
   */
  public ElectricCurrentType getElectricCurrentType() {
    return electricCurrentType;
  }

  /**
   * Gets synonymous ids.
   *
   * @return the synonymous ids
   */
  public Set<String> getSynonymousIds() {
    return synonymousIds;
  }

  /**
   * Copy charging point type copy builder.
   *
   * @return the charging point type copy builder
   */
  public ChargingPointTypeCopyBuilder copy() {
    return new ChargingPointTypeCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChargingPointType that)) return false;
    return id.equals(that.id)
        && sRated.equals(that.sRated)
        && electricCurrentType == that.electricCurrentType
        && synonymousIds.equals(that.synonymousIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, sRated, electricCurrentType, synonymousIds);
  }

  @Override
  public String toString() {
    return ChargingPointTypeUtils.fromIdString(id)
        .flatMap(
            commonType -> {
              if (commonType.getsRated().equals(sRated)
                  && commonType.getElectricCurrentType().equals(electricCurrentType)) {
                return Optional.of(commonType.id);
              } else {
                return Optional.empty();
              }
            })
        .orElseGet(
            () ->
                id
                    + "("
                    + sRated.to(PowerSystemUnits.KILOVOLTAMPERE).getValue().doubleValue()
                    + "|"
                    + electricCurrentType
                    + ")");
  }

  /**
   * A builder pattern based approach to create copies of {@link ChargingPointType} entities with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * ChargingPointType}*
   */
  public static class ChargingPointTypeCopyBuilder {

    private String id;
    private ComparableQuantity<Power> sRated;
    private ElectricCurrentType electricCurrentType;
    private Set<String> synonymousIds;

    private ChargingPointTypeCopyBuilder(ChargingPointType entity) {
      this.id = entity.getId();
      this.sRated = entity.getsRated();
      this.electricCurrentType = entity.getElectricCurrentType();
      this.synonymousIds = entity.getSynonymousIds();
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public ChargingPointTypeCopyBuilder setId(String id) {
      this.id = id;
      return thisInstance();
    }

    /**
     * Sets rated.
     *
     * @param sRated the s rated
     * @return the rated
     */
    public ChargingPointTypeCopyBuilder setsRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return thisInstance();
    }

    /**
     * Sets electric current type.
     *
     * @param electricCurrentType the electric current type
     * @return the electric current type
     */
    public ChargingPointTypeCopyBuilder setElectricCurrentType(
        ElectricCurrentType electricCurrentType) {
      this.electricCurrentType = electricCurrentType;
      return thisInstance();
    }

    /**
     * Sets synonymous ids.
     *
     * @param synonymousIds the synonymous ids
     * @return the synonymous ids
     */
    public ChargingPointTypeCopyBuilder setSynonymousIds(Set<String> synonymousIds) {
      this.synonymousIds = synonymousIds;
      return thisInstance();
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
      return id;
    }

    /**
     * Gets rated.
     *
     * @return the rated
     */
    public ComparableQuantity<Power> getsRated() {
      return sRated;
    }

    /**
     * Gets electric current type.
     *
     * @return the electric current type
     */
    public ElectricCurrentType getElectricCurrentType() {
      return electricCurrentType;
    }

    /**
     * Gets synonymous ids.
     *
     * @return the synonymous ids
     */
    public Set<String> getSynonymousIds() {
      return synonymousIds;
    }

    /**
     * Scale charging point type . charging point type copy builder.
     *
     * @param factor the factor
     * @return the charging point type . charging point type copy builder
     */
    public ChargingPointType.ChargingPointTypeCopyBuilder scale(Double factor) {
      setsRated(getsRated().multiply(factor));
      return this;
    }

    /**
     * Build charging point type.
     *
     * @return the charging point type
     */
    public ChargingPointType build() {
      return new ChargingPointType(
          getId(), getsRated(), getElectricCurrentType(), getSynonymousIds());
    }

    /**
     * This instance charging point type . charging point type copy builder.
     *
     * @return the charging point type . charging point type copy builder
     */
    protected ChargingPointType.ChargingPointTypeCopyBuilder thisInstance() {
      return this;
    }
  }
}
