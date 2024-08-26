/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.csv.CsvLoadProfileMetaInformation;
import edu.ie3.datamodel.io.factory.timeseries.BDEWLoadProfileFactory;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileData;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileFactory;
import edu.ie3.datamodel.io.factory.timeseries.RandomLoadProfileFactory;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.io.source.csv.CsvLoadProfileSource;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.*;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.Try;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class LoadProfileSource<P extends LoadProfile, E extends LoadProfileEntry>
    extends EntitySource {
  private static final CsvDataSource buildInSource = getBuildInSource(Path.of("load"));

  protected final Class<E> entryClass;
  protected final LoadProfileFactory<P, E> entryFactory;

  protected LoadProfileSource(Class<E> entryClass, LoadProfileFactory<P, E> entryFactory) {
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
  protected Try<Set<E>, FactoryException> createEntries(Map<String, String> fieldToValues) {
    LoadProfileData<E> factoryData = new LoadProfileData<>(fieldToValues, entryClass);
    return entryFactory.get(factoryData);
  }

  public abstract LoadProfileTimeSeries<E> getTimeSeries();

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
  public static Map<BdewStandardLoadProfile, BDEWLoadProfileTimeSeries> getBDEWLoadProfiles() {
    BDEWLoadProfileFactory factory = new BDEWLoadProfileFactory();

    return buildInSource.getCsvLoadProfileMetaInformation(BdewStandardLoadProfile.values()).stream()
        .map(
            metaInformation ->
                (BDEWLoadProfileTimeSeries)
                    new CsvLoadProfileSource<>(
                            buildInSource, metaInformation, BDEWLoadProfileEntry.class, factory)
                        .getTimeSeries())
        .collect(Collectors.toMap(BDEWLoadProfileTimeSeries::getLoadProfile, Function.identity()));
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
                RandomLoadProfileEntry.class,
                new RandomLoadProfileFactory())
            .getTimeSeries();
  }
}
