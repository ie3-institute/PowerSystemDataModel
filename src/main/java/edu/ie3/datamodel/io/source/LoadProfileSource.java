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
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.io.source.csv.CsvLoadProfileSource;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.RandomLoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import edu.ie3.datamodel.models.value.load.LoadValues;
import edu.ie3.datamodel.models.value.load.RandomLoadValues;
import edu.ie3.datamodel.utils.Try;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public abstract class LoadProfileSource<P extends LoadProfile, V extends LoadValues>
    extends EntitySource {
  protected final Class<V> entryClass;
  protected final LoadProfileFactory<P, V> entryFactory;

  protected LoadProfileSource(Class<V> entryClass, LoadProfileFactory<P, V> entryFactory) {
    this.entryClass = entryClass;
    this.entryFactory = entryFactory;
  }

  /**
   * Build a {@link LoadProfileEntry} of type {@code V}.
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
   * Method to get the value for a given time.
   *
   * @param time for which a value is needed
   * @return an optional
   * @throws SourceException if an exception occurred
   */
  public abstract Optional<LoadValues.Provider> getValue(ZonedDateTime time) throws SourceException;

  /** Returns the load profile of this source. */
  public abstract P getLoadProfile();

  /** Returns the maximal power value of the time series */
  public abstract Optional<ComparableQuantity<Power>> getMaxPower();

  /** Returns the load profile energy scaling for this load profile time series. */
  public abstract Optional<ComparableQuantity<Energy>> getLoadProfileEnergyScaling();

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
        .collect(Collectors.toMap(CsvLoadProfileSource::getLoadProfile, Function.identity()));
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
