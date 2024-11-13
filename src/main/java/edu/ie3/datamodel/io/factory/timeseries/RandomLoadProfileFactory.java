/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE;

import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.RandomLoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.load.RandomLoadValues;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public class RandomLoadProfileFactory extends LoadProfileFactory<LoadProfile, RandomLoadValues> {
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
    return new RandomLoadProfileTimeSeries(metaInformation.getUuid(), RANDOM_LOAD_PROFILE, entries);
  }

  @Override
  public LoadProfile parseProfile(String profile) {
    return RANDOM_LOAD_PROFILE;
  }

  @Override
  public Optional<ComparableQuantity<Power>> calculateMaxPower(
      LoadProfile loadProfile, Set<LoadProfileEntry<RandomLoadValues>> loadProfileEntries) {
    return Optional.of(Quantities.getQuantity(159d, PowerSystemUnits.WATT));
  }

  @Override
  public double getLoadProfileEnergyScaling() {
    return 716.5416966513656;
  }
}
