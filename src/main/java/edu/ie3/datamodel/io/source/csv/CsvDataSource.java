/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.DataSource;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
import edu.ie3.util.StringUtils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class of all .csv file related sources containing methods and fields consumed by allmost
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

  /**
   * @deprecated ensures downward compatibility with old csv data format. Can be removed when
   *     support for old csv format is removed. *
   */
  @Deprecated(since = "1.1.0", forRemoval = true)
  private boolean notYetLoggedWarning = true;

  public CsvDataSource(String csvSep, Path folderPath, FileNamingStrategy fileNamingStrategy) {
    this.csvSep = csvSep;
    this.connector = new CsvFileConnector(folderPath, fileNamingStrategy);
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
      final String csvRow, final String[] headline) {

    TreeMap<String, String> insensitiveFieldsToAttributes =
        new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    try {
      String[] finalFieldVals = parseCsvRow(csvRow, csvSep);
      insensitiveFieldsToAttributes.putAll(
          IntStream.range(0, finalFieldVals.length)
              .boxed()
              .collect(
                  Collectors.toMap(
                      k -> StringUtils.snakeCaseToCamelCase(headline[k]), v -> finalFieldVals[v])));

      if (insensitiveFieldsToAttributes.size() != headline.length) {
        Set<String> fieldsToAttributesKeySet = insensitiveFieldsToAttributes.keySet();
        insensitiveFieldsToAttributes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        throw new SourceException(
            "The size of the headline does not fit to the size of the resulting fields to attributes mapping.\nHeadline: "
                + String.join(", ", headline)
                + "\nResultingMap: "
                + String.join(", ", fieldsToAttributesKeySet)
                + "\nCsvRow: "
                + csvRow.trim()
                + ".\nIs the csv separator in the file matching the separator provided in the constructor ('"
                + csvSep
                + "') and does the number of columns match the number of headline fields?");
      }
    } catch (Exception e) {
      log.error(
          "Cannot build fields to attributes map for row '{}' with headline '{}'.\nException: {}",
          csvRow.trim(),
          String.join(",", headline),
          e);
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
   * Extracts all strings from the provided csvRow matching the provided regexString and returns a
   * list of strings in the order of their appearance in the csvRow string
   *
   * @param regexString regex string that should be searched for
   * @param csvRow csv row string that should be searched in for the regex string
   * @return a list of strings matching the provided regex in the order of their appearance in the
   *     provided csv row string
   */
  private List<String> extractMatchingStrings(String regexString, String csvRow) {
    Pattern pattern = Pattern.compile(regexString);
    Matcher matcher = pattern.matcher(csvRow);

    ArrayList<String> matchingList = new ArrayList<>();
    while (matcher.find()) {
      matchingList.add(matcher.group());
    }
    return matchingList;
  }

  public FileNamingStrategy getNamingStrategy() {
    return fileNamingStrategy;
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
      return Success.of(csvRowFieldValueMapping(reader, headline).parallelStream());
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
  protected List<Map<String, String>> csvRowFieldValueMapping(
      BufferedReader reader, String[] headline) {
    return reader
        .lines()
        .parallel()
        .map(csvRow -> buildFieldsToAttributes(csvRow, headline))
        .filter(map -> !map.isEmpty())
        .toList();
  }
}
