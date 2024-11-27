/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.csv.CsvIndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.TimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.DataSource;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import edu.ie3.datamodel.utils.Try.Success;
import edu.ie3.util.StringUtils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class of all .csv file related sources containing methods and fields consumed by almost
 * all implementations of .csv file related sources.
 *
 * @version 0.1
 * @since 05.04.20
 */
public class CsvDataSource implements DataSource {

  protected static final Logger log = LoggerFactory.getLogger(CsvDataSource.class);

  // general fields
  protected final String csvSep;
  protected final CsvFileConnector connector;

  private final FileNamingStrategy fileNamingStrategy;

  public CsvDataSource(String csvSep, Path directoryPath, FileNamingStrategy fileNamingStrategy) {
    this.csvSep = csvSep;
    this.connector = new CsvFileConnector(directoryPath);
    this.fileNamingStrategy = fileNamingStrategy;
  }

  @Override
  public Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass)
      throws SourceException {
    return getSourceFields(getFilePath(entityClass).getOrThrow());
  }

  /**
   * @param filePath path of file starting from base folder, including file name but not file
   *     extension
   * @return The source field names as a set, if file exists
   * @throws SourceException on error while reading the source file
   */
  public Optional<Set<String>> getSourceFields(Path filePath) throws SourceException {
    try (BufferedReader reader = connector.initReader(filePath)) {
      return Optional.of(
          Arrays.stream(parseCsvRow(reader.readLine(), csvSep)).collect(Collectors.toSet()));
    } catch (FileNotFoundException e) {
      // A file not existing can be acceptable in many cases, and is handled elsewhere.
      log.debug("The source for the given entity couldn't be found! Cause: {}", e.getMessage());
      return Optional.empty();
    } catch (IOException e) {
      throw new SourceException("Error while trying to read source", e);
    }
  }

  @Override
  public Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass)
      throws SourceException {
    return buildStreamWithFieldsToAttributesMap(entityClass, true).getOrThrow();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /** Returns the set {@link FileNamingStrategy}. */
  public FileNamingStrategy getNamingStrategy() {
    return fileNamingStrategy;
  }

  /**
   * Receive the information for specific time series. They are given back filtered by the column
   * scheme in order to allow for accounting the different content types.
   *
   * @param columnSchemes the column schemes to initialize readers for. If no scheme is given, all
   *     possible readers will be initialized.
   * @return A mapping from column scheme to the individual time series meta information
   */
  public Map<UUID, CsvIndividualTimeSeriesMetaInformation>
      getCsvIndividualTimeSeriesMetaInformation(final ColumnScheme... columnSchemes) {
    return getIndividualTimeSeriesFilePaths().parallelStream()
        .map(
            filePath -> {
              /* Extract meta information from file path and enhance it with the file path itself */
              IndividualTimeSeriesMetaInformation metaInformation =
                  fileNamingStrategy.individualTimeSeriesMetaInformation(filePath.toString());
              return new CsvIndividualTimeSeriesMetaInformation(
                  metaInformation, FileNamingStrategy.removeFileNameEnding(filePath.getFileName()));
            })
        .filter(
            metaInformation ->
                columnSchemes == null
                    || columnSchemes.length == 0
                    || Stream.of(columnSchemes)
                        .anyMatch(scheme -> scheme.equals(metaInformation.getColumnScheme())))
        .collect(Collectors.toMap(TimeSeriesMetaInformation::getUuid, Function.identity()));
  }

  /**
   * Returns a set of relative paths strings to time series files, with respect to the base folder
   * path
   *
   * @return A set of relative paths to time series files, with respect to the base folder path
   */
  protected Set<Path> getIndividualTimeSeriesFilePaths() {
    Path baseDirectory = connector.getBaseDirectory();
    try (Stream<Path> pathStream = Files.walk(baseDirectory)) {
      return pathStream
          .map(baseDirectory::relativize)
          .filter(
              path -> {
                Path withoutEnding =
                    Path.of(FileNamingStrategy.removeFileNameEnding(path.toString()));
                return fileNamingStrategy
                    .getIndividualTimeSeriesPattern()
                    .matcher(withoutEnding.toString())
                    .matches();
              })
          .collect(Collectors.toSet());
    } catch (IOException e) {
      log.error("Unable to determine time series files readers for time series.", e);
      return Collections.emptySet();
    }
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Takes a row string of a .csv file and a string array of the csv file headline, tries to split
   * the csv row string based and zip it together with the headline. This method does not contain
   * any sanity checks. Order of the headline needs to be the same as the fields in the csv row. If
   * the zipping fails, an empty map is returned and the causing error is logged.
   *
   * @param csvRow the csv row string that contains the data
   * @param headline the headline fields of the csv file
   * @return a map containing the mapping of (fieldName to fieldValue) or an empty map if an error
   *     occurred
   */
  protected Map<String, String> buildFieldsToAttributes(
      final String csvRow, final String[] headline) throws SourceException {

    TreeMap<String, String> insensitiveFieldsToAttributes =
        new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    String[] fieldVals = parseCsvRow(csvRow, csvSep);
    insensitiveFieldsToAttributes.putAll(
        IntStream.range(0, Math.min(fieldVals.length, headline.length))
            .boxed()
            .collect(
                Collectors.toMap(
                    k -> StringUtils.snakeCaseToCamelCase(headline[k]), v -> fieldVals[v])));

    if (fieldVals.length != headline.length) {
      throw new SourceException(
          "The size of the headline ("
              + headline.length
              + ") does not fit to the size of the attribute fields ("
              + fieldVals.length
              + ").\nHeadline: "
              + String.join(", ", headline)
              + "\nRow: "
              + csvRow.trim()
              + ".\nPlease check:"
              + "\n - is the csv separator in the file matching the separator provided in the constructor ('"
              + csvSep
              + "')"
              + "\n - does the number of columns match the number of headline fields "
              + "\n - are you using a valid RFC 4180 formatted csv row?");
    }

    if (insensitiveFieldsToAttributes.size() != fieldVals.length) {
      throw new SourceException(
          "There might be duplicate headline elements.\nHeadline: "
              + String.join(", ", headline)
              + ".\nPlease keep in mind that headlines are case-insensitive and underscores from snake case are ignored.");
    }

    return insensitiveFieldsToAttributes;
  }

  /**
   * Parse a given row of a valid RFC 4180 formatted csv row
   *
   * @param csvRow the valid row
   * @param csvSep separator of the csv file
   * @return an array with the csv field values as strings
   */
  protected String[] parseCsvRow(String csvRow, String csvSep) {
    return Arrays.stream(csvRow.split(csvSep + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1))
        .map(
            maybeStartEndQuotedString ->
                StringUtils.unquoteStartEnd(maybeStartEndQuotedString.trim())
                    .replaceAll("\"{2}", "\"")
                    .trim())
        .toArray(String[]::new);
  }

  /**
   * Tries to open a file reader based on the provided entity class and hands it over for further
   * processing.
   *
   * @param entityClass the entity class that should be build and that is used to get the
   *     corresponding reader
   * @return a parallel stream of maps, where each map represents one row of the csv file with the
   *     mapping (fieldName to fieldValue)
   */
  protected Try<Stream<Map<String, String>>, SourceException> buildStreamWithFieldsToAttributesMap(
      Class<? extends Entity> entityClass, boolean allowFileNotExisting) {
    return getFilePath(entityClass)
        .flatMap(
            path -> buildStreamWithFieldsToAttributesMap(entityClass, path, allowFileNotExisting));
  }

  /**
   * Reads the first line (considered to be the headline with headline fields) and returns a stream
   * of (fieldName to fieldValue) mapping where each map represents one row of the .csv file. Since
   * the returning stream is a parallel stream, the order of the elements cannot be guaranteed.
   *
   * @param entityClass the entity class that should be build
   * @param filePath the path of the file to read
   * @return a try containing either a parallel stream of maps, where each map represents one row of
   *     the csv file with the mapping (fieldName to fieldValue) or an exception
   */
  protected <T extends Entity>
      Try<Stream<Map<String, String>>, SourceException> buildStreamWithFieldsToAttributesMap(
          Class<T> entityClass, Path filePath, boolean allowFileNotExisting) {
    try (BufferedReader reader = connector.initReader(filePath)) {
      final String[] headline = parseCsvRow(reader.readLine(), csvSep);

      // by default try-with-resources closes the reader directly when we leave this method (which
      // is wanted to avoid a lock on the file), but this causes a closing of the stream as well.
      // As we still want to consume the data at other places, we start a new stream instead of
      // returning the original one
      return csvRowFieldValueMapping(reader, headline);
    } catch (FileNotFoundException e) {
      if (allowFileNotExisting) {
        log.warn("Unable to find file '{}': {}", filePath, e.getMessage());
        return Success.of(Stream.empty());
      } else {
        return Failure.of(new SourceException("Unable to find file '" + filePath + "'.", e));
      }
    } catch (IOException e) {
      return Failure.of(
          new SourceException(
              "Cannot read file to build entity '" + entityClass.getSimpleName() + "'", e));
    }
  }

  private Try<Path, SourceException> getFilePath(Class<? extends Entity> entityClass) {
    return Try.from(
        fileNamingStrategy.getFilePath(entityClass),
        () ->
            new SourceException(
                "Cannot find a naming strategy for class '" + entityClass.getSimpleName() + "'."));
  }

  /**
   * Method to return a row to field value mapping from a csv file.
   *
   * @param reader for the file
   * @param headline of the file
   * @return a list of mapping
   */
  protected Try<Stream<Map<String, String>>, SourceException> csvRowFieldValueMapping(
      BufferedReader reader, String[] headline) {
    return Try.scanStream(
            reader
                .lines()
                .parallel()
                .map(
                    csvRow ->
                        Try.of(
                            () -> buildFieldsToAttributes(csvRow, headline),
                            SourceException.class)),
            "Map<String, String>")
        .transform(
            stream -> stream.filter(map -> !map.isEmpty()),
            e -> new SourceException("Parsing csv row failed.", e));
  }
}
