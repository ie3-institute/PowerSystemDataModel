/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.RandomLoadParameters;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicInput;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.StringUtils;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides an easy to use standard way to name files based on the class that should be processed
 * e.g. when writing .csv files. Represents a flat dir with all files inside. To use a hierarchic
 * directory structure one might consider using {@link HierarchicFileNamingStrategy}
 *
 * @version 0.1
 * @since 03.02.20
 */
public class FileNamingStrategy {

  protected static final Logger logger = LogManager.getLogger(FileNamingStrategy.class);

  private static final String RES_ENTITY_SUFFIX = "_res";

  private final String prefix;
  private final String suffix;

  /**
   * Constructor for building the file names
   *
   * @param prefix Prefix of the files
   * @param suffix Suffixes of the files
   */
  public FileNamingStrategy(String prefix, String suffix) {
    this.prefix = preparePrefix(prefix);
    this.suffix = prepareSuffix(suffix);
  }

  /** Constructor for building the file names without provided files with prefix and suffix */
  public FileNamingStrategy() {
    this("", "");
  }

  /**
   * Constructor for building the file names
   *
   * @param prefix Prefix of the files
   */
  public FileNamingStrategy(String prefix) {
    this(prefix, "");
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

  public Optional<String> getFileName(Class<? extends UniqueEntity> cls) {
    if (AssetTypeInput.class.isAssignableFrom(cls))
      return getTypeFileName(cls.asSubclass(AssetTypeInput.class));
    if (AssetInput.class.isAssignableFrom(cls))
      return getAssetInputFileName(cls.asSubclass(AssetInput.class));
    if (ResultEntity.class.isAssignableFrom(cls))
      return getResultEntityFileName(cls.asSubclass(ResultEntity.class));
    if (CharacteristicInput.class.isAssignableFrom(cls))
      return getAssetCharacteristicsFileName(cls.asSubclass(CharacteristicInput.class));
    if (cls.equals(RandomLoadParameters.class)) {
      String loadParamString = camelCaseToSnakeCase(cls.getSimpleName());
      return Optional.of(addPrefixAndSuffix(loadParamString.concat("_input")));
    }
    if (GraphicInput.class.isAssignableFrom(cls))
      return getGraphicsInputFileName(cls.asSubclass(GraphicInput.class));
    if (OperatorInput.class.isAssignableFrom(cls))
      return getOperatorInputFileName(cls.asSubclass(OperatorInput.class));
    logger.error("There is no naming strategy defined for {}", cls.getSimpleName());
    return Optional.empty();
  }

  /**
   * Builds a file name of the given information.
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
      return Optional.of(
          prefix
              .concat("individual")
              .concat("_time_series")
              .concat("_")
              .concat(timeSeries.getUuid().toString())
              .concat(suffix));
    } else if (timeSeries instanceof LoadProfileInput) {
      LoadProfileInput loadProfileInput = (LoadProfileInput) timeSeries;
      return Optional.of(
          prefix
              .concat("load_profile")
              .concat("_time_series")
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
   * Get the the file name for all {@link GraphicInput}s
   *
   * @param graphicClass the graphic input class a filename string should be generated from
   * @return the filename string
   */
  public Optional<String> getGraphicsInputFileName(Class<? extends GraphicInput> graphicClass) {
    String assetInputString = camelCaseToSnakeCase(graphicClass.getSimpleName());
    return Optional.of(addPrefixAndSuffix(assetInputString));
  }

  /**
   * Get the the file name for all {@link CharacteristicInput}s
   *
   * @param assetCharClass the asset characteristics class a filename string should be generated
   *     from
   * @return the filename string
   */
  public Optional<String> getAssetCharacteristicsFileName(
      Class<? extends CharacteristicInput> assetCharClass) {
    String assetCharString = camelCaseToSnakeCase(assetCharClass.getSimpleName());
    return Optional.of(addPrefixAndSuffix(assetCharString));
  }

  /**
   * Get the the file name for all {@link AssetTypeInput}s
   *
   * @param typeClass the asset type class a filename string should be generated from
   * @return the filename string
   */
  public Optional<String> getTypeFileName(Class<? extends AssetTypeInput> typeClass) {
    String assetTypeString = camelCaseToSnakeCase(typeClass.getSimpleName());
    return Optional.of(addPrefixAndSuffix(assetTypeString));
  }

  /**
   * Get the the file name for all {@link AssetInput}s
   *
   * @param assetInputClass the asset input class a filename string should be generated from
   * @return the filename string
   */
  public Optional<String> getAssetInputFileName(Class<? extends AssetInput> assetInputClass) {
    String assetInputString = camelCaseToSnakeCase(assetInputClass.getSimpleName());
    return Optional.of(addPrefixAndSuffix(assetInputString));
  }

  /**
   * Get the the file name for all {@link OperatorInput}s
   *
   * @param operatorClass the asset input class a filename string should be generated from
   * @return the filename string
   */
  public Optional<String> getOperatorInputFileName(Class<? extends OperatorInput> operatorClass) {
    String assetInputString = camelCaseToSnakeCase(operatorClass.getSimpleName());
    return Optional.of(addPrefixAndSuffix(assetInputString));
  }

  /**
   * Get the the file name for all {@link ResultEntity}s
   *
   * @param resultEntityClass the result entity class a filename string should be generated from
   * @return the filename string
   */
  public Optional<String> getResultEntityFileName(Class<? extends ResultEntity> resultEntityClass) {
    return Optional.of(buildResultEntityString(resultEntityClass));
  }

  /**
   * Get the the file name for coordinates
   *
   * @return the filename string
   */
  public String getIdCoordinateFileName() {
    return addPrefixAndSuffix("coordinates");
  }

  private String buildResultEntityString(Class<? extends ResultEntity> resultEntityClass) {
    String resultEntityString =
        resultEntityClass.getSimpleName().replace("Result", "").toLowerCase();
    return addPrefixAndSuffix(resultEntityString.concat(RES_ENTITY_SUFFIX));
  }

  /**
   * Converts a given camel case string to its snake case representation
   *
   * @param camelCaseString the camel case string
   * @return the resulting snake case representation
   */
  private String camelCaseToSnakeCase(String camelCaseString) {
    String regularCamelCaseRegex = "([a-z])([A-Z]+)";
    String regularSnakeCaseReplacement = "$1_$2";
    String specialCamelCaseRegex = "((?<!_)[A-Z]?)((?<!^)[A-Z]+)";
    String specialSnakeCaseReplacement = "$1_$2";
    return camelCaseString
        .replaceAll(regularCamelCaseRegex, regularSnakeCaseReplacement)
        .replaceAll(specialCamelCaseRegex, specialSnakeCaseReplacement)
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
}
