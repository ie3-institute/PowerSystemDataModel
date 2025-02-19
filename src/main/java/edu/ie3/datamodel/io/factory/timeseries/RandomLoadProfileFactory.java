/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE;
import static tech.units.indriya.unit.Units.WATT;

import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.RandomLoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.load.RandomLoadValues;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.List;
import java.util.Set;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public class RandomLoadProfileFactory
    extends LoadProfileFactory<RandomLoadProfile, RandomLoadValues> {
  public static final String K_WEEKDAY = "kWd";
  public static final String K_SATURDAY = "kSa";
  public static final String K_SUNDAY = "kSu";
  public static final String MY_WEEKDAY = "myWd";
  public static final String MY_SATURDAY = "mySa";
  public static final String MY_SUNDAY = "mySu";
  public static final String SIGMA_WEEKDAY = "sigmaWd";
  public static final String SIGMA_SATURDAY = "sigmaSa";
  public static final String SIGMA_SUNDAY = "sigmaSu";

  public RandomLoadProfileFactory() {
    super(RandomLoadValues.class);
  }

  @Override
  protected LoadProfileEntry<RandomLoadValues> buildModel(LoadProfileData<RandomLoadValues> data) {
    int quarterHour = data.getInt(QUARTER_HOUR);

    return new LoadProfileEntry<>(
        new RandomLoadValues(
            data.getDouble(K_SATURDAY),
            data.getDouble(K_SUNDAY),
            data.getDouble(K_WEEKDAY),
            data.getDouble(MY_SATURDAY),
            data.getDouble(MY_SUNDAY),
            data.getDouble(MY_WEEKDAY),
            data.getDouble(SIGMA_SATURDAY),
            data.getDouble(SIGMA_SUNDAY),
            data.getDouble(SIGMA_WEEKDAY)),
        quarterHour);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return List.of(
        newSet(
            QUARTER_HOUR,
            K_WEEKDAY,
            K_SATURDAY,
            K_SUNDAY,
            MY_WEEKDAY,
            MY_SATURDAY,
            MY_SUNDAY,
            SIGMA_WEEKDAY,
            SIGMA_SATURDAY,
            SIGMA_SUNDAY));
  }

  @Override
  public RandomLoadProfileTimeSeries build(
      LoadProfileMetaInformation metaInformation, Set<LoadProfileEntry<RandomLoadValues>> entries) {
    RandomLoadProfile profile = RANDOM_LOAD_PROFILE;

    ComparableQuantity<Power> maxPower = calculateMaxPower(profile, entries);
    ComparableQuantity<Energy> profileEnergyScaling = getLoadProfileEnergyScaling(profile);

    return new RandomLoadProfileTimeSeries(
        metaInformation.getUuid(), profile, entries, maxPower, profileEnergyScaling);
  }

  @Override
  public RandomLoadProfile parseProfile(String profile) {
    return RANDOM_LOAD_PROFILE;
  }

  /**
   * This is the 95 % quantile resulting from 10,000 evaluations of the year 2019. It is only
   * needed, when the load is meant to be scaled to rated active power.
   *
   * @return Reference active power to use for later model calculations
   */
  @Override
  public ComparableQuantity<Power> calculateMaxPower(
      RandomLoadProfile loadProfile, Set<LoadProfileEntry<RandomLoadValues>> loadProfileEntries) {
    return Quantities.getQuantity(159d, WATT);
  }

  /**
   * Returns the profile energy scaling factor, the random profile is scaled to.
   *
   * <p>It is said in 'Kays - Agent-based simulation environment for improving the planning of
   * distribution grids', that the Generalized Extreme Value distribution's parameters are sampled
   * from input data, that is normalized to 1,000 kWh annual energy consumption. However, due to
   * inaccuracies in random data reproduction, the sampled values will lead to an average annual
   * energy consumption of approx. this value. It has been found by 1,000 evaluations of the year
   * 2019.
   */
  @Override
  public ComparableQuantity<Energy> getLoadProfileEnergyScaling(RandomLoadProfile loadProfile) {
    return Quantities.getQuantity(716.5416966513656, PowerSystemUnits.KILOWATTHOUR);
  }
}
