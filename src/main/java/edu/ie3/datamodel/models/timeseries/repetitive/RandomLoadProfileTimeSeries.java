/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import de.lmu.ifi.dbs.elki.math.statistics.distribution.GeneralizedExtremeValueDistribution;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.value.load.RandomLoadValues;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import tech.units.indriya.quantity.Quantities;

/**
 * Describes a random load profile time series based on a {@link
 * GeneralizedExtremeValueDistribution}. Each value of this# timeseries is given in kW.
 */
public class RandomLoadProfileTimeSeries extends LoadProfileTimeSeries<RandomLoadValues> {

  public RandomLoadProfileTimeSeries(
      UUID uuid, LoadProfile loadProfile, Set<LoadProfileEntry<RandomLoadValues>> entries) {
    super(
        uuid,
        loadProfile,
        entries,
        Optional.empty(),
        Optional.of(Quantities.getQuantity(716.5416966513656, PowerSystemUnits.KILOWATTHOUR)));
  }

  @Override
  public LoadProfile.RandomLoadProfile getLoadProfile() {
    return (LoadProfile.RandomLoadProfile) super.getLoadProfile();
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
    return "RandomLoadProfileTimeSeries{"
        + "uuid="
        + getUuid()
        + "loadProfile="
        + getLoadProfile()
        + ", valueMapping="
        + getValueMapping()
        + '}';
  }
}
