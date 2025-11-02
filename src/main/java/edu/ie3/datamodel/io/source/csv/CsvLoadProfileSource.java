/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileFactory;
import edu.ie3.datamodel.io.naming.timeseries.FileLoadProfileMetaInformation;
import edu.ie3.datamodel.io.source.LoadProfileSource;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.load.LoadValues;
import edu.ie3.datamodel.utils.Try;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Source that is capable of providing information around load profile time series from csv files.
 */
public class CsvLoadProfileSource<P extends LoadProfile, V extends LoadValues<P>>
    extends LoadProfileSource<P, V> {
  private final LoadProfileTimeSeries<P, V> loadProfileTimeSeries;
  private final CsvDataSource dataSource;
  private final Path filePath;

  public CsvLoadProfileSource(
      CsvDataSource source,
      FileLoadProfileMetaInformation metaInformation,
      Class<V> entryClass,
      LoadProfileFactory<P, V> entryFactory) {
    super(metaInformation, entryClass, entryFactory);
    this.dataSource = source;
    this.filePath = metaInformation.getFullFilePath();

    /* Read in the full time series */
    try {
      this.loadProfileTimeSeries = buildLoadProfileTimeSeries(this::createEntries);
    } catch (SourceException e) {
      throw new IllegalArgumentException(
          "Unable to obtain load profile time series with profile '"
              + metaInformation.getProfile()
              + "'. Please check arguments!",
          e);
    }
  }

  @Override
  public void validate() throws ValidationException {
    validate(entryClass, () -> dataSource.getSourceFields(filePath), entryFactory);
  }

  public LoadProfileTimeSeries<P, V> getTimeSeries() {
    return loadProfileTimeSeries;
  }

  @Override
  public Set<LoadProfileEntry<V>> getEntries() {
    return loadProfileTimeSeries.getEntries();
  }

  @Override
  public Supplier<Optional<PValue>> getValueSupplier(TimeSeriesInputValue data) {
    return loadProfileTimeSeries.supplyValue(data.time());
  }

  @Override
  public Optional<ComparableQuantity<Power>> getMaxPower() {
    return loadProfileTimeSeries.maxPower();
  }

  @Override
  public Optional<ComparableQuantity<Energy>> getProfileEnergyScaling() {
    return loadProfileTimeSeries.loadProfileScaling();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Attempts to read a load profile time series with given unique identifier and file path. Single
   * entries are obtained entries with the help of {@code fieldToValueFunction}. If the file does
   * not exist, an empty Stream is returned.
   *
   * @param fieldToValueFunction function, that is able to transfer a mapping (from field to value)
   *     onto a specific instance of the targeted entry class
   * @throws SourceException If the file cannot be read properly
   * @return an individual time series
   */
  protected LoadProfileTimeSeries<P, V> buildLoadProfileTimeSeries(
      Function<Map<String, String>, Try<LoadProfileEntry<V>, FactoryException>>
          fieldToValueFunction)
      throws SourceException {
    Set<LoadProfileEntry<V>> entries =
        dataSource
            .buildStreamWithFieldsToAttributesMap(filePath, false)
            .flatMap(
                stream ->
                    Try.scanStream(
                        stream.map(fieldToValueFunction), "LoadProfileEntry", SourceException::new))
            .getOrThrow()
            .collect(Collectors.toSet());

    return entryFactory.build(profile, entries);
  }
}
