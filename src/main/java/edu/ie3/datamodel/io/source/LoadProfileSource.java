/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.csv.CsvLoadProfileMetaInformation;
import edu.ie3.datamodel.io.factory.timeseries.BdewLoadProfileFactory;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileData;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileFactory;
import edu.ie3.datamodel.io.factory.timeseries.RandomLoadProfileFactory;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.io.source.csv.CsvLoadProfileSource;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.*;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import edu.ie3.datamodel.models.value.load.LoadValues;
import edu.ie3.datamodel.models.value.load.RandomLoadValues;
import edu.ie3.datamodel.utils.Try;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class LoadProfileSource<P extends LoadProfile, V extends LoadValues>
    extends EntitySource {
  private static final CsvDataSource buildInSource = getBuildInSource(Path.of("load"));

  protected final Class<V> entryClass;
  protected final LoadProfileFactory<P, V> entryFactory;

  protected LoadProfileSource(Class<V> entryClass, LoadProfileFactory<P, V> entryFactory) {
    this.entryClass = entryClass;
    this.entryFactory = entryFactory;
  }

  /**
   * Build a list of type {@code E}, whereas the underlying {@link Value} does not need any
   * additional information.
   *
   * @param fieldToValues Mapping from field id to values
   * @return {@link Try} of simple time based value
   */
  protected Try<LoadProfileEntry<V>, FactoryException> createEntries(
      Map<String, String> fieldToValues) {
    LoadProfileData<V> factoryData = new LoadProfileData<>(fieldToValues, entryClass);
    return entryFactory.get(factoryData);
  }

  public abstract LoadProfileTimeSeries<V> getTimeSeries();

  /**
   * Method to return all time keys after a given timestamp.
   *
   * @param time given time
   * @return a list of time keys
   */
  public abstract List<ZonedDateTime> getTimeKeysAfter(ZonedDateTime time);

  /**
   * Method to read in the build-in {@link BdewStandardLoadProfile}s.
   *
   * @return a map: load profile to time series
   */
  public static Map<BdewStandardLoadProfile, BdewLoadProfileTimeSeries> getBDEWLoadProfiles() {
    BdewLoadProfileFactory factory = new BdewLoadProfileFactory();

    return buildInSource.getCsvLoadProfileMetaInformation(BdewStandardLoadProfile.values()).stream()
        .map(
            metaInformation ->
                (BdewLoadProfileTimeSeries)
                    new CsvLoadProfileSource<>(
                            buildInSource, metaInformation, BdewLoadValues.class, factory)
                        .getTimeSeries())
        .collect(Collectors.toMap(BdewLoadProfileTimeSeries::getLoadProfile, Function.identity()));
  }

  /**
   * Method to read in the build-in {@link RandomLoadProfileTimeSeries}.
   *
   * @return the random load profile time series
   */
  public static RandomLoadProfileTimeSeries getRandomLoadProfile() {
    CsvLoadProfileMetaInformation metaInformation =
        buildInSource.getCsvLoadProfileMetaInformation(RANDOM_LOAD_PROFILE).stream()
            .findAny()
            .orElseThrow();
    return (RandomLoadProfileTimeSeries)
        new CsvLoadProfileSource<>(
                buildInSource,
                metaInformation,
                RandomLoadValues.class,
                new RandomLoadProfileFactory())
            .getTimeSeries();
  }
}
