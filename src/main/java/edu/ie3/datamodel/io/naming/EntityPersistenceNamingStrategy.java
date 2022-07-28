/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation;
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
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an easy to use standard way to name files, tables or any other persistent representation
 * of models. Normal use cases are e.g., I/O operations with .csv files or databases.
 *
 * @version 0.1
 * @since 03.02.20
 */
public class EntityPersistenceNamingStrategy {

  protected static final Logger logger =
      LoggerFactory.getLogger(EntityPersistenceNamingStrategy.class);

  private static final String UUID_STRING =
      "[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}";
  /**
   * Regex to match the naming convention of a source for an individual time series. The column
   * scheme is accessible via the named capturing group "columnScheme". The time series' UUID is
   * accessible by the named capturing group "uuid"
   */
  private static final String INDIVIDUAL_TIME_SERIES_PATTERN =
      "its_(?<columnScheme>[a-zA-Z]{1,11})_(?<uuid>" + UUID_STRING + ")";

  /**
   * Pattern to identify individual time series in this instance of the naming strategy (takes care
   * of prefix and suffix)
   */
  protected final Pattern individualTimeSeriesPattern;

  /**
   * Regex to match the naming convention of a file for a repetitive load profile time series. The
   * profile is accessible via the named capturing group "profile", the uuid by the group "uuid"
   */
  private static final String LOAD_PROFILE_TIME_SERIES =
      "lpts_(?<profile>[a-zA-Z][0-9])_(?<uuid>" + UUID_STRING + ")";

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
                + INDIVIDUAL_TIME_SERIES_PATTERN
                + (suffix.isEmpty() ? "" : "_")
                + suffix);
    this.loadProfileTimeSeriesPattern =
        Pattern.compile(
            prefix
                + (prefix.isEmpty() ? "" : "_")
                + LOAD_PROFILE_TIME_SERIES
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
   * Extracts meta information from a valid source name for an individual time series
   *
   * @param sourceName Name of the source to extract information from, e.g. file name or SQL table
   *     name
   * @return Meta information form individual time series source name
   * @deprecated since 3.0. Use {@link #individualTimesSeriesMetaInformation(String)} instead
   */
  @Deprecated(since = "3.0", forRemoval = true)
  public edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation
      extractIndividualTimesSeriesMetaInformation(String sourceName) {
    Matcher matcher = getIndividualTimeSeriesPattern().matcher(sourceName);
    if (!matcher.matches())
      throw new IllegalArgumentException(
          "Cannot extract meta information on individual time series from '" + sourceName + "'.");

    String columnSchemeKey = matcher.group("columnScheme");
    edu.ie3.datamodel.io.csv.timeseries.ColumnScheme columnScheme =
        edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.parse(columnSchemeKey)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Cannot parse '" + columnSchemeKey + "' to valid column scheme."));

    return new edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation(
        UUID.fromString(matcher.group("uuid")), columnScheme);
  }

  /**
   * Extracts meta information from a valid source name for an individual time series
   *
   * @param sourceName Name of the source to extract information from, e.g. file name or SQL table
   *     name
   * @return Meta information form individual time series source name
   */
  public IndividualTimeSeriesMetaInformation individualTimesSeriesMetaInformation(
      String sourceName) {
    Matcher matcher = getIndividualTimeSeriesPattern().matcher(sourceName);
    if (!matcher.matches())
      throw new IllegalArgumentException(
          "Cannot extract meta information on individual time series from '" + sourceName + "'.");

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
   * @deprecated since 3.0. Use {@link #loadProfileTimesSeriesMetaInformation(String)} instead
   */
  @Deprecated(since = "3.0", forRemoval = true)
  public edu.ie3.datamodel.io.csv.timeseries.LoadProfileTimeSeriesMetaInformation
      extractLoadProfileTimesSeriesMetaInformation(String fileName) {
    Matcher matcher = getLoadProfileTimeSeriesPattern().matcher(fileName);
    if (!matcher.matches())
      throw new IllegalArgumentException(
          "Cannot extract meta information on load profile time series from '" + fileName + "'.");

    return new edu.ie3.datamodel.io.csv.timeseries.LoadProfileTimeSeriesMetaInformation(
        UUID.fromString(matcher.group("uuid")), matcher.group("profile"));
  }

  /**
   * Extracts meta information from a valid file name for a load profile time series
   *
   * @param fileName File name to extract information from
   * @return Meta information form load profile time series file name
   */
  public LoadProfileTimeSeriesMetaInformation loadProfileTimesSeriesMetaInformation(
      String fileName) {
    Matcher matcher = getLoadProfileTimeSeriesPattern().matcher(fileName);
    if (!matcher.matches())
      throw new IllegalArgumentException(
          "Cannot extract meta information on load profile time series from '" + fileName + "'.");

    return new LoadProfileTimeSeriesMetaInformation(
        UUID.fromString(matcher.group("uuid")), matcher.group("profile"));
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
   * Returns the name of the entity, that should be used for persistence.
   *
   * @param cls Targeted class of the given entity
   * @return The name of the entity
   */
  public Optional<String> getEntityName(Class<? extends UniqueEntity> cls) {
    if (InputEntity.class.isAssignableFrom(cls))
      return getInputEntityName(cls.asSubclass(InputEntity.class));
    if (ResultEntity.class.isAssignableFrom(cls))
      return getResultEntityName(cls.asSubclass(ResultEntity.class));
    if (CharacteristicInput.class.isAssignableFrom(cls))
      return getAssetCharacteristicsEntityName(cls.asSubclass(CharacteristicInput.class));
    logger.error("There is no naming strategy defined for {}", cls.getSimpleName());
    return Optional.empty();
  }

  /**
   * Get the name for all {@link InputEntity}s
   *
   * @param cls Targeted class of the given entity
   * @return The entity name
   */
  public Optional<String> getInputEntityName(Class<? extends InputEntity> cls) {
    if (AssetTypeInput.class.isAssignableFrom(cls))
      return getTypeEntityName(cls.asSubclass(AssetTypeInput.class));
    if (AssetInput.class.isAssignableFrom(cls))
      return getAssetInputEntityName(cls.asSubclass(AssetInput.class));
    if (RandomLoadParameters.class.isAssignableFrom(cls))
      return getRandomLoadParametersEntityName(cls.asSubclass(RandomLoadParameters.class));
    if (GraphicInput.class.isAssignableFrom(cls))
      return getGraphicsInputEntityName(cls.asSubclass(GraphicInput.class));
    if (OperatorInput.class.isAssignableFrom(cls))
      return getOperatorInputEntityName(cls.asSubclass(OperatorInput.class));
    if (TimeSeriesMappingSource.MappingEntry.class.isAssignableFrom(cls))
      return getTimeSeriesMappingEntityName();
    logger.error("The class '{}' is not covered for input entity naming.", cls);
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
   * Get the entity name for all {@link RandomLoadParameters}
   *
   * @param randomLoadParamClass the random load parameters class an entity name string should be
   *     generated from
   * @return the entity name string
   */
  public Optional<String> getRandomLoadParametersEntityName(
      Class<? extends RandomLoadParameters> randomLoadParamClass) {
    String loadParamString = camelCaseToSnakeCase(randomLoadParamClass.getSimpleName());
    return Optional.of(addPrefixAndSuffix(loadParamString.concat("_input")));
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
      Optional<String> getEntityName(T timeSeries) {
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
    } else if (timeSeries instanceof LoadProfileInput loadProfileInput) {
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
   * Get the entity name for coordinates
   *
   * @return the entity name string
   */
  public String getIdCoordinateEntityName() {
    return addPrefixAndSuffix("coordinates");
  }
}
