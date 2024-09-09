/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT;

import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import java.util.*;
import tech.units.indriya.quantity.Quantities;

/**
 * Describes a bdew load profile time series with repetitive values that can be calculated from a
 * pattern
 */
public class BdewLoadProfileTimeSeries extends LoadProfileTimeSeries<BdewLoadValues> {
  /**
   * The maximum average power consumption per quarter hour for a given load profile, calculated
   * over all seasons and weekday types of given load profile
   */
  public final PValue maxPower;

  public BdewLoadProfileTimeSeries(
      UUID uuid,
      BdewStandardLoadProfile loadProfile,
      Set<LoadProfileEntry<BdewLoadValues>> values) {
    super(uuid, loadProfile, values);

    double power;

    if (loadProfile == BdewStandardLoadProfile.H0) {
      power =
          values.stream()
              .map(TimeSeriesEntry::getValue)
              .map(v -> List.of(v.getWiSa(), v.getWiSu(), v.getWiWd()))
              .flatMap(Collection::stream)
              .max(Comparator.naturalOrder())
              .orElse(0d);

    } else {
      power =
          values.stream()
              .map(LoadProfileEntry::getValue)
              .map(
                  v ->
                      List.of(
                          v.getSuSa(),
                          v.getSuSu(),
                          v.getSuWd(),
                          v.getTrSa(),
                          v.getTrSu(),
                          v.getTrWd(),
                          v.getWiSa(),
                          v.getWiSu(),
                          v.getWiWd()))
              .flatMap(Collection::stream)
              .max(Comparator.naturalOrder())
              .orElse(0d);
    }

    this.maxPower = new PValue(Quantities.getQuantity(power, KILOWATT));
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
