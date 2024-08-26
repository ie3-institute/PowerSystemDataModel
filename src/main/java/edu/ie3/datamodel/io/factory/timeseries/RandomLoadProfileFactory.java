/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE;
import static java.time.DayOfWeek.*;

import de.lmu.ifi.dbs.elki.math.statistics.distribution.GeneralizedExtremeValueDistribution;
import de.lmu.ifi.dbs.elki.utilities.random.RandomFactory;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.RandomLoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.RandomLoadProfileTimeSeries;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomLoadProfileFactory
    extends LoadProfileFactory<LoadProfile, RandomLoadProfileEntry> {
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
    super(RandomLoadProfileEntry.class);
  }

  @Override
  protected Set<RandomLoadProfileEntry> buildModel(LoadProfileData<RandomLoadProfileEntry> data) {
    int quarterHour = data.getInt(QUARTER_HOUR);
    return Set.of(
        new RandomLoadProfileEntry(
            new GeneralizedExtremeValueDistribution(
                data.getDouble(MY_WEEKDAY),
                data.getDouble(SIGMA_WEEKDAY),
                data.getDouble(K_WEEKDAY),
                RandomFactory.get(new Random().nextLong())),
            MONDAY,
            quarterHour),
        new RandomLoadProfileEntry(
            new GeneralizedExtremeValueDistribution(
                data.getDouble(MY_SATURDAY),
                data.getDouble(SIGMA_SATURDAY),
                data.getDouble(K_SATURDAY),
                RandomFactory.get(new Random().nextLong())),
            SATURDAY,
            quarterHour),
        new RandomLoadProfileEntry(
            new GeneralizedExtremeValueDistribution(
                data.getDouble(MY_SUNDAY),
                data.getDouble(SIGMA_SUNDAY),
                data.getDouble(K_SUNDAY),
                RandomFactory.get(new Random().nextLong())),
            SUNDAY,
            quarterHour));
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
  public LoadProfileTimeSeries<RandomLoadProfileEntry> build(
      LoadProfileTimeSeriesMetaInformation metaInformation, Set<RandomLoadProfileEntry> entries) {
    return new RandomLoadProfileTimeSeries(metaInformation.getUuid(), RANDOM_LOAD_PROFILE, entries);
  }

  @Override
  public LoadProfile parseProfile(String profile) {
    return RANDOM_LOAD_PROFILE;
  }
}
