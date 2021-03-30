/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.io.csv.FileNameMetaInformation;
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.csv.timeseries.LoadProfileTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicInput;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput;
import edu.ie3.datamodel.models.value.*;
import edu.ie3.util.StringUtils;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides an easy to use standard way to name files, tables or any other persistent representation
 * of models. Normal use cases are e.g., I/O operations with .csv files or databases. If a folder
 * structure is required for file based I/O operations, one might consider using {@link
 * HierarchicFileNamingStrategy}
 *
 * @version 0.1
 * @since 03.02.20
 */
public class EntityPersistenceNamingStrategy {

  protected static final Logger logger =
      LogManager.getLogger(EntityPersistenceNamingStrategy.class);

  private static final String UUID_STRING =
      "[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}";
  /**
   * Regex to match the naming convention of a file for an individual time series. The column scheme
   * is accessible via the named capturing group "columnScheme". The time series' UUID is accessible
   * by the named capturing group "uuid"
   */
  private static final Pattern INDIVIDUAL_TIME_SERIES_PATTERN =
      Pattern.compile("its_(?<columnScheme>[a-zA-Z]{1,11})_(?<uuid>" + UUID_STRING + ")");

  /**
   * Pattern to identify individual time series in this instance of the naming strategy (takes care
   * of prefix and suffix)
   */
  protected final Pattern individualTimeSeriesPattern;

  /**
   * Regex to match the naming convention of a file for a repetitive load profile time series. The
   * profile is accessible via the named capturing group "profile", the uuid by the group "uuid"
   */
  private static final Pattern LOAD_PROFILE_TIME_SERIES =
      Pattern.compile("lpts_(?<profile>[a-zA-Z][0-9])_(?<uuid>" + UUID_STRING + ")");

  /**
   * Pattern to identify load profile time series in this instance of the naming strategy (takes
   * care of prefix and suffix)
   */
  protected final Pattern loadProfileTimeSeriesPattern;

  private static final String RES_ENTITY_SUFFIX = "_res";

  private final String prefix;
  private final String suffix;

  /**
   * Constructor for building the names of the data sinks without provided entities with prefix and
   * suffix
   */
  public EntityPersistenceNamingStrategy() {
    this("", "");
  }

  /**
   * Constructor for building the names of the data sinks
   *
   * @param prefix Prefix of the data sinks
   */
  public EntityPersistenceNamingStrategy(String prefix) {
    this(prefix, "");
  }

  /**
   * Constructor for building the names of the data sinks
   *
   * @param prefix Prefix of the data sinks
   * @param suffix Suffixes of the data sinks
   */
  public EntityPersistenceNamingStrategy(String prefix, String suffix) {
    this.prefix = preparePrefix(prefix);
    this.suffix = prepareSuffix(suffix);

    this.individualTimeSeriesPattern =
        Pattern.compile(
            prefix
                + (prefix.isEmpty() ? "" : "_")
                + INDIVIDUAL_TIME_SERIES_PATTERN.pattern()
                + (suffix.isEmpty() ? "" : "_")
                + suffix);
    this.loadProfileTimeSeriesPattern =
        Pattern.compile(
            prefix
                + (prefix.isEmpty() ? "" : "_")
                + LOAD_PROFILE_TIME_SERIES.pattern()
                + (suffix.isEmpty() ? "" : "_")
                + suffix);
  }

  public Pattern getLoadProfileTimeSeriesPattern() {
    return loadProfileTimeSeriesPattern;
  }

  public Pattern getIndividualTimeSeriesPattern() {
    return individualTimeSeriesPattern;
  }

  /**
   * Prepares the prefix by appending an underscore and bringing it to lower case
   *
   * @param prefix Intended prefix
   * @return Prefix with trailing underscore
   */
  private static String preparePrefix(String prefix) {
    return StringUtils.cleanString(prefix).replaceAll("([^_])$", "$1_").toLowerCase();
  }

  /**
   * Prepares the suffix by prepending an underscore and bringing it to lower case
   *
   * @param suffix Intended suffix
   * @return Suffix with trailing leading
   */
  private static String prepareSuffix(String suffix) {
    return StringUtils.cleanString(suffix).replaceAll("^([^_])", "_$1").toLowerCase();
  }

  /**
   * Get the full path to the file with regard to some (not explicitly specified) base directory.
   * The path does NOT start or end with any of the known file separators or file extension.
   *
   * @param cls Targeted class of the given file
   * @return An optional sub path to the actual file
   */
  public Optional<String> getFilePath(Class<? extends UniqueEntity> cls) {
    // do not adapt orElseGet, see https://www.baeldung.com/java-optional-or-else-vs-or-else-get for
    // details
    return getFilePath(
        getFileName(cls).orElseGet(() -> ""), getDirectoryPath(cls).orElseGet(() -> ""));
  }

  private Optional<String> getFilePath(String fileName, String subDirectories) {
    if (fileName.isEmpty()) return Optional.empty();
    if (!subDirectories.isEmpty())
      return Optional.of(FilenameUtils.concat(subDirectories, fileName));
    else return Optional.of(fileName);
  }

  /**
   * Returns the file name (and only the file name without any directories and extension).
   *
   * @param cls Targeted class of the given file
   * @return The file name
   */
  public Optional<String> getFileName(Class<? extends UniqueEntity> cls) {
    Optional<String> inputEntityFileName = getInputEntityName(cls);
    if (inputEntityFileName.isPresent()) return inputEntityFileName;
    if (ResultEntity.class.isAssignableFrom(cls))
      return getResultEntityName(cls.asSubclass(ResultEntity.class));
    logger.error("There is no naming strategy defined for {}", cls.getSimpleName());
    return Optional.empty();
  }

  /**
   * Get the name for all {@link InputEntity}s
   *
   * @param cls Targeted class of the given entity
   * @return The entity name
   */
  public Optional<String> getInputEntityName(Class<? extends UniqueEntity> cls) {
    if (AssetTypeInput.class.isAssignableFrom(cls))
      return getTypeEntityName(cls.asSubclass(AssetTypeInput.class));
    if (AssetInput.class.isAssignableFrom(cls))
      return getAssetInputEntityName(cls.asSubclass(AssetInput.class));
    if (CharacteristicInput.class.isAssignableFrom(cls))
      return getAssetCharacteristicsEntityName(cls.asSubclass(CharacteristicInput.class));
    if (cls.equals(RandomLoadParameters.class)) {
      String loadParamString = camelCaseToSnakeCase(cls.getSimpleName());
      return Optional.of(addPrefixAndSuffix(loadParamString.concat("_input")));
    }
    if (GraphicInput.class.isAssignableFrom(cls))
      return getGraphicsInputEntityName(cls.asSubclass(GraphicInput.class));
    if (OperatorInput.class.isAssignableFrom(cls))
      return getOperatorInputEntityName(cls.asSubclass(OperatorInput.class));
    if (TimeSeriesMappingSource.MappingEntry.class.isAssignableFrom(cls))
      return getTimeSeriesMappingEntityName();
    return Optional.empty();
  }

  /**
   * Get the entity name for all {@link ResultEntity}s
   *
   * @param resultEntityClass the result entity class an entity name string should be generated from
   * @return the entity name string
   */
  public Optional<String> getResultEntityName(Class<? extends ResultEntity> resultEntityClass) {
    String resultEntityString =
        camelCaseToSnakeCase(resultEntityClass.getSimpleName().replace("Result", ""));
    return Optional.of(addPrefixAndSuffix(resultEntityString.concat(RES_ENTITY_SUFFIX)));
  }

  /**
   * Get the entity name for all {@link AssetTypeInput}s
   *
   * @param typeClass the asset type class an entity name string should be generated from
   * @return the entity name string
   */
  public Optional<String> getTypeEntityName(Class<? extends AssetTypeInput> typeClass) {
    String assetTypeString = camelCaseToSnakeCase(typeClass.getSimpleName());
    return Optional.of(addPrefixAndSuffix(assetTypeString));
  }

  /**
   * Get the entity name for all {@link AssetInput}s
   *
   * @param assetInputClass the asset input class an entity name string should be generated from
   * @return the entity name string
   */
  public Optional<String> getAssetInputEntityName(Class<? extends AssetInput> assetInputClass) {
    String assetInputString = camelCaseToSnakeCase(assetInputClass.getSimpleName());
    return Optional.of(addPrefixAndSuffix(assetInputString));
  }

  /**
   * Get the entity name for all {@link CharacteristicInput}s
   *
   * @param assetCharClass the asset characteristics class an entity name string should be generated
   *     from
   * @return the entity name string
   */
  public Optional<String> getAssetCharacteristicsEntityName(
      Class<? extends CharacteristicInput> assetCharClass) {
    String assetCharString = camelCaseToSnakeCase(assetCharClass.getSimpleName());
    return Optional.of(addPrefixAndSuffix(assetCharString));
  }

  /**
   * Converts a given camel case string to its snake case representation
   *
   * @param camelCaseString the camel case string
   * @return the resulting snake case representation
   */
  private String camelCaseToSnakeCase(String camelCaseString) {
    String snakeCaseReplacement = "$1_$2";
    /* Separate all lower case letters, that are followed by a capital or a digit by underscore */
    String regularCamelCaseRegex = "([a-z])([A-Z0-9]+)";
    /* Separate all digits, that are followed by a letter by underscore */
    String numberLetterCamelCaseRegex = "([0-9])([a-zA-Z]+)";
    /* Separate two or more capitals, that are not at the beginning of the string by underscore */
    String specialCamelCaseRegex = "((?<!^)[A-Z])([A-Z]+)";
    return camelCaseString
        .replaceAll(regularCamelCaseRegex, snakeCaseReplacement)
        .replaceAll(numberLetterCamelCaseRegex, snakeCaseReplacement)
        .replaceAll(specialCamelCaseRegex, snakeCaseReplacement)
        .toLowerCase();
  }

  /**
   * Adds prefix and suffix to the provided String
   *
   * @param s the string that should be pre-/suffixed
   * @return the original string with prefixes/suffixes
   */
  private String addPrefixAndSuffix(String s) {
    return prefix.concat(s).concat(suffix);
  }

  /**
   * Get the entity name for all {@link GraphicInput}s
   *
   * @param graphicClass the graphic input class an entity name string should be generated from
   * @return the entity name string
   */
  public Optional<String> getGraphicsInputEntityName(Class<? extends GraphicInput> graphicClass) {
    String assetInputString = camelCaseToSnakeCase(graphicClass.getSimpleName());
    return Optional.of(addPrefixAndSuffix(assetInputString));
  }

  /**
   * Get the entity name for all {@link OperatorInput}s
   *
   * @param operatorClass the asset input class an entity name string should be generated from
   * @return the entity name string
   */
  public Optional<String> getOperatorInputEntityName(Class<? extends OperatorInput> operatorClass) {
    String assetInputString = camelCaseToSnakeCase(operatorClass.getSimpleName());
    return Optional.of(addPrefixAndSuffix(assetInputString));
  }

  /**
   * Get the entity name for time series mapping
   *
   * @return The entity name string
   */
  public Optional<String> getTimeSeriesMappingEntityName() {
    return Optional.of(addPrefixAndSuffix("time_series_mapping"));
  }

  /**
   * Returns the sub directory structure with regard to some (not explicitly specified) base
   * directory. The path does NOT start or end with any of the known file separators.
   *
   * @param cls Targeted class of the given file
   * @return An optional sub directory path
   */
  public Optional<String> getDirectoryPath(Class<? extends UniqueEntity> cls) {
    return Optional.empty();
  }

  /**
   * Get the full path to the file with regard to some (not explicitly specified) base directory.
   * The path does NOT start or end with any of the known file separators or file extension.
   *
   * @param <T> Type of the time series
   * @param <E> Type of the entry in the time series
   * @param <V> Type of the value, that is carried by the time series entry
   * @param timeSeries Time series to derive naming information from
   * @return An optional sub path to the actual file
   */
  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      Optional<String> getFilePath(T timeSeries) {
    // do not adapt orElseGet, see https://www.baeldung.com/java-optional-or-else-vs-or-else-get for
    // details
    return getFilePath(
        getFileName(timeSeries).orElseGet(() -> ""),
        getDirectoryPath(timeSeries).orElseGet(() -> ""));
  }

  /**
   * Builds a file name (and only the file name without any directories and extension) of the given
   * information.
   *
   * @param <T> Type of the time series
   * @param <E> Type of the entry in the time series
   * @param <V> Type of the value, that is carried by the time series entry
   * @param timeSeries Time series to derive naming information from
   * @return A file name for this particular time series
   */
  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      Optional<String> getFileName(T timeSeries) {
    if (timeSeries instanceof IndividualTimeSeries) {
      Optional<E> maybeFirstElement = timeSeries.getEntries().stream().findFirst();
      if (maybeFirstElement.isPresent()) {
        Class<? extends Value> valueClass = maybeFirstElement.get().getValue().getClass();
        Optional<ColumnScheme> mayBeColumnScheme = ColumnScheme.parse(valueClass);
        if (mayBeColumnScheme.isPresent()) {
          return Optional.of(
              prefix
                  .concat("its")
                  .concat("_")
                  .concat(mayBeColumnScheme.get().getScheme())
                  .concat("_")
                  .concat(timeSeries.getUuid().toString())
                  .concat(suffix));
        } else {
          logger.error("Unsupported content of time series {}", timeSeries);
          return Optional.empty();
        }
      } else {
        logger.error("Unable to determine content of time series {}", timeSeries);
        return Optional.empty();
      }
    } else if (timeSeries instanceof LoadProfileInput) {
      LoadProfileInput loadProfileInput = (LoadProfileInput) timeSeries;
      return Optional.of(
          prefix
              .concat("lpts")
              .concat("_")
              .concat(loadProfileInput.getType().getKey())
              .concat("_")
              .concat(loadProfileInput.getUuid().toString())
              .concat(suffix));
    } else {
      logger.error("There is no naming strategy defined for {}", timeSeries);
      return Optional.empty();
    }
  }

  /**
   * Returns the sub directory structure with regard to some (not explicitly specified) base
   * directory. The path does NOT start or end with any of the known file separators.
   *
   * @param <T> Type of the time series
   * @param <E> Type of the entry in the time series
   * @param <V> Type of the value, that is carried by the time series entry
   * @param timeSeries Time series to derive naming information from
   * @return An optional sub directory path
   */
  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      Optional<String> getDirectoryPath(T timeSeries) {
    return Optional.empty();
  }

  /**
   * Extracts meta information from a file name, of a time series.
   *
   * @param path Path to the file
   * @return The meeting meta information
   */
  public FileNameMetaInformation extractTimeSeriesMetaInformation(Path path) {
    /* Extract file name from possibly fully qualified path */
    Path fileName = path.getFileName();
    if (fileName == null)
      throw new IllegalArgumentException("Unable to extract file name from path '" + path + "'.");
    return extractTimeSeriesMetaInformation(fileName.toString());
  }

  /**
   * Extracts meta information from a file name, of a time series. Here, a file name <u>without</u>
   * leading path has to be provided
   *
   * @param fileName File name
   * @return The meeting meta information
   */
  public FileNameMetaInformation extractTimeSeriesMetaInformation(String fileName) {
    /* Remove the file ending (ending limited to 255 chars, which is the max file name allowed in NTFS and ext4) */
    String withoutEnding = fileName.replaceAll("(?:\\.[^\\\\/\\s]{1,255}){1,2}$", "");

    if (individualTimeSeriesPattern.matcher(withoutEnding).matches())
      return extractIndividualTimesSeriesMetaInformation(withoutEnding);
    else if (loadProfileTimeSeriesPattern.matcher(withoutEnding).matches())
      return extractLoadProfileTimesSeriesMetaInformation(withoutEnding);
    else
      throw new IllegalArgumentException(
          "Unknown format of '" + fileName + "'. Cannot extract meta information.");
  }

  /**
   * Extracts meta information from a valid file name for a individual time series
   *
   * @param fileName File name to extract information from
   * @return Meta information form individual time series file name
   */
  private IndividualTimeSeriesMetaInformation extractIndividualTimesSeriesMetaInformation(
      String fileName) {
    Matcher matcher = individualTimeSeriesPattern.matcher(fileName);
    if (!matcher.matches())
      throw new IllegalArgumentException(
          "Cannot extract meta information on individual time series from '" + fileName + "'.");

    String columnSchemeKey = matcher.group("columnScheme");
    ColumnScheme columnScheme =
        ColumnScheme.parse(columnSchemeKey)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Cannot parse '" + columnSchemeKey + "' to valid column scheme."));

    return new IndividualTimeSeriesMetaInformation(
        UUID.fromString(matcher.group("uuid")), columnScheme);
  }

  /**
   * Extracts meta information from a valid file name for a load profile time series
   *
   * @param fileName File name to extract information from
   * @return Meta information form load profile time series file name
   */
  private LoadProfileTimeSeriesMetaInformation extractLoadProfileTimesSeriesMetaInformation(
      String fileName) {
    Matcher matcher = loadProfileTimeSeriesPattern.matcher(fileName);
    if (!matcher.matches())
      throw new IllegalArgumentException(
          "Cannot extract meta information on load profile time series from '" + fileName + "'.");

    return new LoadProfileTimeSeriesMetaInformation(
        UUID.fromString(matcher.group("uuid")), matcher.group("profile"));
  }

  /**
   * Get the entity name for coordinates
   *
   * @return the entity name string
   */
  public String getIdCoordinateEntityName() {
    return addPrefixAndSuffix("coordinates");
  }
}
