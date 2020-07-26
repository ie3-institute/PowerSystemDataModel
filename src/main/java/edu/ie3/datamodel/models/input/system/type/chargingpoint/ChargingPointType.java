/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type.chargingpoint;

import edu.ie3.datamodel.models.ElectricCurrentType;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Power;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 25.07.20
 */
public class ChargingPointType {

  private final String id;
  private final Quantity<Power> sRated;
  private final ElectricCurrentType electricCurrentType;

  private final Set<String> synonymousIds;

  public ChargingPointType(
      String id, Quantity<Power> sRated, ElectricCurrentType electricCurrentType) {
    this.id = id;
    this.sRated = sRated;
    this.electricCurrentType = electricCurrentType;
    this.synonymousIds = new HashSet<>();
  }

  public ChargingPointType(
      String id,
      Quantity<Power> sRated,
      ElectricCurrentType electricCurrentType,
      Set<String> synonymousIds) {
    this.id = id;
    this.sRated = sRated;
    this.electricCurrentType = electricCurrentType;
    this.synonymousIds = synonymousIds;
  }

  public String getId() {
    return id;
  }

  public Quantity<Power> getsRated() {
    return sRated;
  }

  public ElectricCurrentType getElectricCurrentType() {
    return electricCurrentType;
  }

  public Set<String> getSynonymousIds() {
    return synonymousIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChargingPointType that = (ChargingPointType) o;
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
}
