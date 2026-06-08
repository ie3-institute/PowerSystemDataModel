/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type.chargingpoint;

import edu.ie3.datamodel.models.ElectricCurrentType;
import edu.ie3.datamodel.models.input.system.EvcsInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * The actual implementation {@link EvcsInput} types. Default type implementations as well as
 * methods to parse a type from a string can be found in {@link ChargingPointTypeUtils}
 */
public record ChargingPointType(
    String id,
    ComparableQuantity<Power> sRated,
    ElectricCurrentType electricCurrentType,
    Set<String> synonymousIds)
    implements Serializable {

  public ChargingPointType(
      String id, ComparableQuantity<Power> sRated, ElectricCurrentType electricCurrentType) {
    this(id, sRated, electricCurrentType, new HashSet<>());
  }

  public String getId() {
    return id;
  }

  public ComparableQuantity<Power> getsRated() {
    return sRated;
  }

  public ElectricCurrentType getElectricCurrentType() {
    return electricCurrentType;
  }

  public Set<String> getSynonymousIds() {
    return synonymousIds;
  }

  public ChargingPointTypeCopyBuilder copy() {
    return new ChargingPointTypeCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o
        instanceof
        ChargingPointType(
            String id1,
            ComparableQuantity<Power> rated,
            ElectricCurrentType currentType,
            Set<String> ids))) return false;
    return id.equals(id1)
        && sRated.equals(rated)
        && electricCurrentType == currentType
        && synonymousIds.equals(ids);
  }

  @Override
  public String toString() {
    return ChargingPointTypeUtils.fromIdString(id)
        .flatMap(
            commonType -> {
              if (commonType.sRated().equals(sRated)
                  && commonType.electricCurrentType().equals(electricCurrentType)) {
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
   * altered field values. For detailed field descriptions refer to Javadocs of {@link
   * ChargingPointType}
   */
  public static class ChargingPointTypeCopyBuilder {

    private String id;
    private ComparableQuantity<Power> sRated;
    private ElectricCurrentType electricCurrentType;
    private Set<String> synonymousIds;

    private ChargingPointTypeCopyBuilder(ChargingPointType entity) {
      this.id = entity.id();
      this.sRated = entity.sRated();
      this.electricCurrentType = entity.electricCurrentType();
      this.synonymousIds = entity.synonymousIds();
    }

    public ChargingPointTypeCopyBuilder setId(String id) {
      this.id = id;
      return thisInstance();
    }

    public ChargingPointTypeCopyBuilder setsRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return thisInstance();
    }

    public ChargingPointTypeCopyBuilder setElectricCurrentType(
        ElectricCurrentType electricCurrentType) {
      this.electricCurrentType = electricCurrentType;
      return thisInstance();
    }

    public ChargingPointTypeCopyBuilder setSynonymousIds(Set<String> synonymousIds) {
      this.synonymousIds = synonymousIds;
      return thisInstance();
    }

    public String getId() {
      return id;
    }

    public ComparableQuantity<Power> getsRated() {
      return sRated;
    }

    public ElectricCurrentType getElectricCurrentType() {
      return electricCurrentType;
    }

    public Set<String> getSynonymousIds() {
      return synonymousIds;
    }

    public ChargingPointTypeCopyBuilder scale(Double factor) {
      setsRated(getsRated().multiply(factor));
      return this;
    }

    public ChargingPointType build() {
      return new ChargingPointType(
          getId(), getsRated(), getElectricCurrentType(), getSynonymousIds());
    }

    protected ChargingPointTypeCopyBuilder thisInstance() {
      return this;
    }
  }
}
