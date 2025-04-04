/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import edu.ie3.datamodel.models.value.load.LoadValues;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Describes a bdew load profile time series with repetitive values that can be calculated from a
 * pattern. Each value of this timeseries is given in W.
 */
public class BdewLoadProfileTimeSeries extends LoadProfileTimeSeries<BdewLoadValues> {

  public BdewLoadProfileTimeSeries(
      UUID uuid,
      BdewStandardLoadProfile loadProfile,
      Set<LoadProfileEntry<BdewLoadValues>> values,
      ComparableQuantity<Power> maxPower,
      ComparableQuantity<Energy> profileEnergyScaling) {
    super(uuid, loadProfile, values, maxPower, profileEnergyScaling);
  }

  @Override
  protected LoadValues.Provider buildFunction(BdewLoadValues loadValue, ZonedDateTime time) {
    return last -> loadValue.getValue(time, (BdewStandardLoadProfile) loadProfile);
  }

  @Override
  public BdewStandardLoadProfile getLoadProfile() {
    return (BdewStandardLoadProfile) super.getLoadProfile();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    return "BDEWLoadProfileTimeSeries{"
        + "uuid="
        + getUuid()
        + "loadProfile="
        + getLoadProfile()
        + ", valueMapping="
        + getValueMapping()
        + '}';
  }
}
