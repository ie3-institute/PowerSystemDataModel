/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import de.lmu.ifi.dbs.elki.math.statistics.distribution.GeneralizedExtremeValueDistribution;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.value.load.RandomLoadValues;
import java.util.Set;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Describes a random load profile time series based on a {@link
 * GeneralizedExtremeValueDistribution}. Each value of this# timeseries is given in kW.
 */
public class RandomLoadProfileTimeSeries extends LoadProfileTimeSeries<RandomLoadValues> {

  public RandomLoadProfileTimeSeries(
      UUID uuid,
      LoadProfile loadProfile,
      Set<LoadProfileEntry<RandomLoadValues>> entries,
      ComparableQuantity<Power> maxPower,
      ComparableQuantity<Energy> profileEnergyScaling) {
    super(uuid, loadProfile, entries, maxPower, profileEnergyScaling);
  }

  @Override
  public LoadProfile.RandomLoadProfile getLoadProfile() {
    return (LoadProfile.RandomLoadProfile) super.getLoadProfile();
  }

  @Override
  public String toString() {
    return "Random" + super.toString();
  }
}
