/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.csv.CsvIndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.factory.timeseries.*;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesSource;
import edu.ie3.datamodel.models.value.*;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.util.*;

/** Source that is capable of providing information around time series from csv files. */
public class CsvTimeSeriesSource<V extends Value> extends TimeSeriesSource<V> {

  public CsvTimeSeriesSource(
          String csvSep,
          String folderPath,
          FileNamingStrategy fileNamingStrategy,
          UUID timeSeriesUuid,
          String filePath,
          Class<V> valueClass,
          TimeBasedSimpleValueFactory<V> factory
  ) {
    super(new CsvDataSource(csvSep, folderPath, fileNamingStrategy), timeSeriesUuid, valueClass, factory);
  }






  /**
   * Factory method to build a source from given meta information
   *
   * @param csvSep the separator string for csv columns
   * @param folderPath path to the folder holding the time series files
   * @param fileNamingStrategy strategy for the file naming of time series files / data sinks
   * @param metaInformation The given meta information
   * @throws SourceException If the given meta information are not supported
   * @return The source
   * @deprecated since 3.0. Use {@link CsvTimeSeriesSource#getSource(java.lang.String,
   *     java.lang.String, edu.ie3.datamodel.io.naming.FileNamingStrategy,
   *     edu.ie3.datamodel.io.csv.CsvIndividualTimeSeriesMetaInformation)} instead.
   */
  @Deprecated(since = "3.0", forRemoval = true)
  public static CsvTimeSeriesSource<? extends Value> getSource(
          String csvSep,
          String folderPath,
          FileNamingStrategy fileNamingStrategy,
          edu.ie3.datamodel.io.connectors.CsvFileConnector.CsvIndividualTimeSeriesMetaInformation
                  metaInformation)
          throws SourceException {
    return null;
    /*
    if (!TimeSeriesSource.isSchemeAccepted(metaInformation.getColumnScheme()))
      throw new SourceException(
              "Unsupported column scheme '" + metaInformation.getColumnScheme() + "'.");

    Class<? extends Value> valClass = metaInformation.getColumnScheme().getValueClass();

    return create(csvSep, folderPath, fileNamingStrategy, metaInformation, valClass);

     */
  }

  /** @deprecated since 3.0 */
  @Deprecated(since = "3.0", forRemoval = true)
  private static <T extends Value> CsvTimeSeriesSource<T> create(
          String csvSep,
          String folderPath,
          FileNamingStrategy fileNamingStrategy,
          edu.ie3.datamodel.io.connectors.CsvFileConnector.CsvIndividualTimeSeriesMetaInformation
                  metaInformation,
          Class<T> valClass) {
    TimeBasedSimpleValueFactory<T> valueFactory = new TimeBasedSimpleValueFactory<>(valClass);
    return new CsvTimeSeriesSource<>(
            csvSep,
            folderPath,
            fileNamingStrategy,
            metaInformation.getUuid(),
            metaInformation.getFullFilePath(),
            valClass,
            valueFactory);
  }


  /**
   * Factory method to build a source from given meta information
   *
   * @param csvSep the separator string for csv columns
   * @param folderPath path to the folder holding the time series files
   * @param fileNamingStrategy strategy for the file naming of time series files / data sinks
   * @param metaInformation The given meta information
   * @throws SourceException If the given meta information are not supported
   * @return The source
   */
  public static CsvTimeSeriesSource<? extends Value> getSource(
      String csvSep,
      String folderPath,
      FileNamingStrategy fileNamingStrategy,
      CsvIndividualTimeSeriesMetaInformation metaInformation)
      throws SourceException {
    if (!TimeSeriesUtils.isSchemeAccepted(metaInformation.getColumnScheme()))
      throw new SourceException(
          "Unsupported column scheme '" + metaInformation.getColumnScheme() + "'.");

    Class<? extends Value> valClass = metaInformation.getColumnScheme().getValueClass();

    return create(csvSep, folderPath, fileNamingStrategy, metaInformation, valClass);
  }

  private static <T extends Value> CsvTimeSeriesSource<T> create(
      String csvSep,
      String folderPath,
      FileNamingStrategy fileNamingStrategy,
      CsvIndividualTimeSeriesMetaInformation metaInformation,
      Class<T> valClass) {
    TimeBasedSimpleValueFactory<T> valueFactory = new TimeBasedSimpleValueFactory<>(valClass);
    return new CsvTimeSeriesSource<>(
        csvSep,
        folderPath,
        fileNamingStrategy,
        metaInformation.getUuid(),
        metaInformation.getFullFilePath(),
        valClass,
        valueFactory);
  }
}
