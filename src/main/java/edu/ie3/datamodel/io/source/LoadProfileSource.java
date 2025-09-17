/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.csv.CsvLoadProfileMetaInformation;
import edu.ie3.datamodel.io.factory.timeseries.BdewLoadProfileFactory;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileData;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileFactory;
import edu.ie3.datamodel.io.factory.timeseries.RandomLoadProfileFactory;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.io.source.csv.CsvLoadProfileSource;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.RandomLoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import edu.ie3.datamodel.models.value.load.LoadValues;
import edu.ie3.datamodel.models.value.load.RandomLoadValues;
import edu.ie3.datamodel.utils.Try;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class LoadProfileSource<P extends LoadProfile, V extends LoadValues<P>>
    extends EntitySource implements PowerValueSource.TimeSeriesBased {
  protected final P profile;
  protected final Class<V> entryClass;
  protected final LoadProfileFactory<P, V> entryFactory;

  protected LoadProfileSource(
      LoadProfileMetaInformation metaInformation,
      Class<V> entryClass,
      LoadProfileFactory<P, V> entryFactory) {
    this.profile = entryFactory.parseProfile(metaInformation.getProfile());
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

  /** Returns the load profile entries as a set. */
  public abstract Set<LoadProfileEntry<V>> getEntries();

  @Override
  public P getProfile() {
    return profile;
  }

  @Override
  public Optional<ZonedDateTime> getNextTimeKey(ZonedDateTime time) {
    return Optional.of(time.plusSeconds(getResolution(getProfile())));
  }

  /**
   * Returns the resolution for the given {@link LoadProfile}.
   *
   * @param loadProfile given load profile
   * @return the resolution in seconds.
   */
  public static long getResolution(LoadProfile loadProfile) {

    if (loadProfile == LoadProfile.DefaultLoadProfiles.NO_LOAD_PROFILE) {
      // since no load profile was assigned, we return the maximal possible value
      return Long.MAX_VALUE;
    } else {
      // currently all registered profiles and all sources use 15 minutes intervals
      return 900L;
    }
  }

  /**
   * Method to read in the build-in {@link BdewStandardLoadProfile}s.
   *
   * @return a map: load profile to load profile source
   */
  public static Map<
          BdewStandardLoadProfile, CsvLoadProfileSource<BdewStandardLoadProfile, BdewLoadValues>>
      getBdewLoadProfiles() throws SourceException {
    CsvDataSource buildInSource = getBuildInSource(LoadProfileSource.class, "/load");

    BdewLoadProfileFactory factory = new BdewLoadProfileFactory();

    return buildInSource
        .getCsvLoadProfileMetaInformation(BdewStandardLoadProfile.values())
        .values()
        .stream()
        .map(
            metaInformation ->
                new CsvLoadProfileSource<>(
                    buildInSource, metaInformation, BdewLoadValues.class, factory))
        .collect(Collectors.toMap(CsvLoadProfileSource::getProfile, Function.identity()));
  }

  /**
   * Method to read in the build-in {@link RandomLoadProfileTimeSeries}.
   *
   * @return the random load profile source
   */
  public static CsvLoadProfileSource<LoadProfile.RandomLoadProfile, RandomLoadValues>
      getRandomLoadProfile() throws SourceException {
    CsvDataSource buildInSource = getBuildInSource(LoadProfileSource.class, "/load");

    CsvLoadProfileMetaInformation metaInformation =
        buildInSource.getCsvLoadProfileMetaInformation(RANDOM_LOAD_PROFILE).values().stream()
            .findAny()
            .orElseThrow();
    return new CsvLoadProfileSource<>(
        buildInSource, metaInformation, RandomLoadValues.class, new RandomLoadProfileFactory());
  }
}
