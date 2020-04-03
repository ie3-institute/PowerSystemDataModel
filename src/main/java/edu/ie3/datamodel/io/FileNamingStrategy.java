/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.RandomLoadParameters;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.characteristic.AssetCharacteristicInput;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides an easy to use standard way to name files based on the class that should be processed
 * e.g. when writing .csv or .xml files
 *
 * @version 0.1
 * @since 03.02.20
 */
public class FileNamingStrategy {

  private static final Logger logger = LogManager.getLogger(FileNamingStrategy.class);

  private static final String RES_ENTITY_SUFFIX = "_res";
  private static final String INPUT_ENTITY_SUFFIX = "_input";
  private static final String TYPE_INPUT = "_type_input";
  private static final String GRAPHIC_INPUT_SUFFIX = "_graphic";
  private static final String TIME_SERIES_SUFFIX = "_time_series";

  private static final String INPUT_CLASS_STRING = "Input";

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
    return cleanString(prefix).replaceAll("([^_])$", "$1_").toLowerCase();
  }

  /**
   * Prepares the suffix by prepending an underscore and bringing it to lower case
   *
   * @param suffix Intended suffix
   * @return Suffix with trailing leading
   */
  private static String prepareSuffix(String suffix) {
    return cleanString(suffix).replaceAll("^([^_])", "_$1").toLowerCase();
  }

  /**
   * Replaces all non word-characters with an underscore
   *
   * @param input String to clean
   * @return the cleaned string
   */
  public static String cleanString(String input) {
    return input.replaceAll("[^\\w]", "_");
  }

  public Optional<String> getFileName(Class<? extends UniqueEntity> cls) {
    if (AssetTypeInput.class.isAssignableFrom(cls))
      return getTypeFileName(cls.asSubclass(AssetTypeInput.class));
    if (AssetInput.class.isAssignableFrom(cls))
      return getAssetInputFileName(cls.asSubclass(AssetInput.class));
    if (ResultEntity.class.isAssignableFrom(cls))
      return getResultEntityFileName(cls.asSubclass(ResultEntity.class));
    if (AssetCharacteristicInput.class.isAssignableFrom(cls))
      return getAssetCharacteristicsFileName(cls.asSubclass(AssetCharacteristicInput.class));
    if (cls.equals(RandomLoadParameters.class)) {
      String loadParamString = cls.getSimpleName().toLowerCase();
      return Optional.of(prefix.concat(loadParamString).concat(INPUT_ENTITY_SUFFIX).concat(suffix));
    }
    if (GraphicInput.class.isAssignableFrom(cls))
      return getGraphicsInputFileName(cls.asSubclass(GraphicInput.class));
    logger.error("There is no naming strategy defined for {}", cls.getSimpleName());
    return Optional.empty();
  }

  /**
   * Builds a file name of the given information.
   *
   * @param timeSeriesProcessorKey Key to identify the combination of time series elements
   * @param timeSeriesUuid UUID of the time series
   * @return A file name for this particular time series
   */
  public Optional<String> getFileName(
      TimeSeriesProcessorKey timeSeriesProcessorKey, UUID timeSeriesUuid) {
    if (timeSeriesProcessorKey.getTimeSeriesClass().equals(IndividualTimeSeries.class)) {
      return Optional.of(
          prefix
              .concat("individual")
              .concat(TIME_SERIES_SUFFIX)
              .concat("_")
              .concat(timeSeriesUuid.toString())
              .concat(suffix));
    } else if (timeSeriesProcessorKey.getTimeSeriesClass().equals(LoadProfileInput.class)) {
      return Optional.of(
          prefix
              .concat("load_profile")
              .concat(TIME_SERIES_SUFFIX)
              .concat("_")
              .concat(timeSeriesUuid.toString())
              .concat(suffix));
    } else {
      logger.error("There is no naming strategy defined for {}", timeSeriesProcessorKey);
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
    String assetInputString =
        graphicClass
            .getSimpleName()
            .replace(INPUT_CLASS_STRING, "")
            .replace("Graphic", "")
            .toLowerCase();
    return Optional.of(
        prefix
            .concat(assetInputString)
            .concat(GRAPHIC_INPUT_SUFFIX)
            .concat(INPUT_ENTITY_SUFFIX)
            .concat(suffix));
  }

  /**
   * Get the the file name for all {@link AssetCharacteristicInput}s
   *
   * @param assetCharClass the asset characteristics class a filename string should be generated
   *     from
   * @return the filename string
   */
  public Optional<String> getAssetCharacteristicsFileName(
      Class<? extends AssetCharacteristicInput> assetCharClass) {
    String assetCharString =
        assetCharClass.getSimpleName().replace(INPUT_CLASS_STRING, "").toLowerCase();
    return Optional.of(prefix.concat(assetCharString).concat(INPUT_ENTITY_SUFFIX).concat(suffix));
  }

  /**
   * Get the the file name for all {@link AssetTypeInput}s
   *
   * @param typeClass the asset type class a filename string should be generated from
   * @return the filename string
   */
  public Optional<String> getTypeFileName(Class<? extends AssetTypeInput> typeClass) {
    String assetTypeString =
        typeClass.getSimpleName().replace(INPUT_CLASS_STRING, "").replace("Type", "").toLowerCase();
    return Optional.of(prefix.concat(assetTypeString).concat(TYPE_INPUT).concat(suffix));
  }

  /**
   * Get the the file name for all {@link AssetInput}s
   *
   * @param assetInputClass the asset input class a filename string should be generated from
   * @return the filename string
   */
  public Optional<String> getAssetInputFileName(Class<? extends AssetInput> assetInputClass) {
    String assetInputString =
        assetInputClass.getSimpleName().replace(INPUT_CLASS_STRING, "").toLowerCase();
    return Optional.of(prefix.concat(assetInputString).concat(INPUT_ENTITY_SUFFIX).concat(suffix));
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

  private String buildResultEntityString(Class<? extends ResultEntity> resultEntityClass) {
    String resultEntityString =
        resultEntityClass.getSimpleName().replace("Result", "").toLowerCase();
    return prefix.concat(resultEntityString).concat(RES_ENTITY_SUFFIX).concat(suffix);
  }
}
