/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.csv.CsvLoadProfileMetaInformation;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileFactory;
import edu.ie3.datamodel.io.source.LoadProfileSource;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.load.LoadValues;
import edu.ie3.datamodel.utils.Try;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Source that is capable of providing information around load profile time series from csv files.
 */
public class CsvLoadProfileSource<P extends LoadProfile, V extends LoadValues>
    extends LoadProfileSource<P, V> {
  private final LoadProfileTimeSeries<V> loadProfileTimeSeries;
  private final CsvDataSource dataSource;
  private final Path filePath;

  public CsvLoadProfileSource(
      CsvDataSource source,
      CsvLoadProfileMetaInformation metaInformation,
      Class<V> entryClass,
      LoadProfileFactory<P, V> entryFactory) {
    super(entryClass, entryFactory);
    this.dataSource = source;
    this.filePath = metaInformation.getFullFilePath();

    /* Read in the full time series */
    try {
      this.loadProfileTimeSeries = buildLoadProfileTimeSeries(metaInformation, this::createEntries);
    } catch (SourceException e) {
      throw new IllegalArgumentException(
          "Unable to obtain time series with UUID '"
              + metaInformation.getUuid()
              + "'. Please check arguments!",
          e);
    }
  }

  @Override
  public void validate() throws ValidationException {
    validate(entryClass, () -> dataSource.getSourceFields(filePath), entryFactory);
  }

  @Override
  public LoadProfileTimeSeries<V> getTimeSeries() {
    return loadProfileTimeSeries;
  }

  @Override
  public List<ZonedDateTime> getTimeKeysAfter(ZonedDateTime time) {
    return loadProfileTimeSeries.getTimeKeysAfter(time);
  }

  @Override
  public Optional<PValue> getValue(ZonedDateTime time) throws SourceException {
    return loadProfileTimeSeries.getValue(time);
  }

  @Override
  @SuppressWarnings("unchecked")
  public P getLoadProfile() {
    return (P) getTimeSeries().getLoadProfile();
  }

  @Override
  public Optional<ComparableQuantity<Power>> getMaxPower() {
    return loadProfileTimeSeries.maxPower();
  }

  @Override
  public Optional<ComparableQuantity<Energy>> getLoadProfileEnergyScaling() {
    return loadProfileTimeSeries.loadProfileScaling();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Attempts to read a load profile time series with given unique identifier and file path. Single
   * entries are obtained entries with the help of {@code fieldToValueFunction}. If the file does
   * not exist, an empty Stream is returned.
   *
   * @param metaInformation containing an unique identifier of the time series, a path to the file
   *     to read as well as the profile
   * @param fieldToValueFunction function, that is able to transfer a mapping (from field to value)
   *     onto a specific instance of the targeted entry class
   * @throws SourceException If the file cannot be read properly
   * @return an individual time series
   */
  protected LoadProfileTimeSeries<V> buildLoadProfileTimeSeries(
      CsvLoadProfileMetaInformation metaInformation,
      Function<Map<String, String>, Try<LoadProfileEntry<V>, FactoryException>>
          fieldToValueFunction)
      throws SourceException {
    Set<LoadProfileEntry<V>> entries =
        dataSource
            .buildStreamWithFieldsToAttributesMap(filePath, false)
            .flatMap(
                stream ->
                    Try.scanStream(stream.map(fieldToValueFunction), "LoadProfileEntry")
                        .transformF(SourceException::new))
            .getOrThrow()
            .collect(Collectors.toSet());

    return entryFactory.build(metaInformation, entries);
  }
}
