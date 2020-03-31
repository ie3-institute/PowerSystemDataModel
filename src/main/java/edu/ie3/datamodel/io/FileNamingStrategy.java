/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.LoadProfileInput;
import edu.ie3.datamodel.models.input.RandomLoadParameters;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.characteristic.AssetCharacteristicInput;
import edu.ie3.datamodel.models.result.ResultEntity;
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
  private static final String TIME_SERIES_SUFFIX = "_timeseries";

  private static final String INPUT_CLASS_STRING = "Input";

  private final String prefix;
  private final String suffix;

  public FileNamingStrategy() {
    this.prefix = "";
    this.suffix = "";
  }

  /**
   * Constructor for building the file names
   *
   * @param prefix Prefix of the files
   * @param suffix Suffixes of the files
   */
  public FileNamingStrategy(String prefix, String suffix) {
    this.prefix = prefix.endsWith("_") || prefix.isEmpty() ? prefix : prefix.concat("_");
    this.suffix = suffix.startsWith("_") || prefix.isEmpty() ? suffix : "_".concat(suffix);
  }

  /**
   * Constructor for building the file names
   *
   * @param prefix Prefix of the files
   */
  public FileNamingStrategy(String prefix) {
    this.suffix = "";
    this.prefix = prefix.endsWith("_") ? prefix : prefix.concat("_");
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

  public Optional<String> getIndividualTimeSeriesFileName(UUID uuid) {
    return Optional.of(
        prefix
            .concat("individual")
            .concat(TIME_SERIES_SUFFIX)
            .concat("_")
            .concat(uuid.toString())
            .concat(suffix));
  }

  public Optional<String> getLoadProfileInputFileName(UUID uuid) {
    String identifier =
        LoadProfileInput.class.getSimpleName().replace(INPUT_CLASS_STRING, "").toLowerCase();
    return Optional.of(
        prefix
            .concat(identifier)
            .concat(TIME_SERIES_SUFFIX)
            .concat("_")
            .concat(uuid.toString())
            .concat(suffix));
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
