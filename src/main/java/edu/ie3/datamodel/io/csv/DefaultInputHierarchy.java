/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.exceptions.FileException;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.RandomLoadParameters;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.characteristic.EvCharacteristicInput;
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Default directory hierarchy for input models */
public class DefaultInputHierarchy implements FileHierarchy {
  private static final Logger logger = LoggerFactory.getLogger(DefaultInputHierarchy.class);

  /** Use the unix file separator here. */
  protected static final String FILE_SEPARATOR = File.separator;

  /** Base directory for this specific grid model. The base path should be a directory. */
  private final Path baseDirectory;

  /** Mapping from sub directories to if they are mandatory or not */
  private final Map<Path, Boolean> subDirectories;

  public DefaultInputHierarchy(String baseDirectory, String gridName) {
    /* Prepare the base path */
    String baseDirectoryNormalized =
        FilenameUtils.normalizeNoEndSeparator(baseDirectory, true) + FILE_SEPARATOR;
    this.baseDirectory =
        Paths.get(
                baseDirectoryNormalized
                    + FilenameUtils.normalizeNoEndSeparator(gridName, true)
                    + FILE_SEPARATOR)
            .toAbsolutePath();

    /* Prepare the sub directories by appending the relative path to base path and mapping to information about being mandatory */
    this.subDirectories =
        Arrays.stream(SubDirectories.values())
            .collect(
                Collectors.toMap(
                    subDirectory ->
                        Paths.get(
                            FilenameUtils.concat(
                                this.baseDirectory.toString(), subDirectory.getRelPath())),
                    SubDirectories::isMandatory));
  }

  /**
   * Checks, if the structure beneath {@link #baseDirectory} is okay.
   *
   * @throws FileException if not
   */
  public void validate() throws FileException {
    if (!Files.exists(baseDirectory))
      throw new FileException("The path '" + baseDirectory + "' does not exist.");
    if (!Files.isDirectory(baseDirectory))
      throw new FileException("The path '" + baseDirectory + "' has to be a directory.");

    checkExpectedDirectories();
    checkFurtherDirectoryElements();
  }

  /**
   * Validates, that all mandatory directories are apparent and are actually directories. Optional
   * directories do not need to be there, but need to be directories.
   *
   * @throws FileException if either the directory is not there or an optional directory is not a
   *     directory.
   */
  private void checkExpectedDirectories() throws FileException {
    for (Map.Entry<Path, Boolean> entry : subDirectories.entrySet()) {
      Path subDirectory = entry.getKey();
      boolean mandatory = entry.getValue();

      if (mandatory) {
        if (!Files.exists(subDirectory))
          throw new FileException("The mandatory directory '" + subDirectory + "' does not exist.");
        if (!Files.isDirectory(subDirectory))
          throw new FileException(
              "The mandatory directory '" + subDirectory + "' is not a directory.");
      } else {
        if (Files.exists(subDirectory) && !Files.isDirectory(subDirectory))
          throw new FileException(
              "The optional directory '" + subDirectory + "' is not a directory.");
        logger.debug("The optional directory '{}' exists.", subDirectory);
      }
    }
  }

  /**
   * Checks the elements, that are further available underneath the {@link this#baseDirectory}. If
   * there is a directory, that is neither mandatory, nor optional, raise an Exception.
   *
   * @throws FileException if there is an unexpected directory
   */
  private void checkFurtherDirectoryElements() throws FileException {
    try (Stream<Path> apparentElementsStream = Files.list(baseDirectory)) {
      for (Path apparentPath : apparentElementsStream.collect(Collectors.toList())) {
        if (Files.isDirectory(apparentPath) && !subDirectories.containsKey(apparentPath))
          throw new FileException(
              "There is a directory '"
                  + apparentPath
                  + "' apparent, that is not supported by the default directory hierarchy.");
      }
    } catch (IOException e) {
      throw new FileException(
          "Cannot get the list of apparent elements in '" + baseDirectory + "'.", e);
    }
  }

  /**
   * Creates all mandatory subdirectories of this default directory hierarchy
   *
   * @throws IOException If the creation of sub directories is not possible
   */
  public void createDirs() throws IOException {
    createDirs(false);
  }

  /**
   * Creates all subdirectories of this default directory hierarchy. Upon request, also the optional
   * directories are created.
   *
   * @param withOptionals if true, also optional directories get created.
   * @throws IOException If the creation of sub directories is not possible
   */
  public void createDirs(boolean withOptionals) throws IOException {
    Files.createDirectories(baseDirectory);
    for (Map.Entry<Path, Boolean> entry : subDirectories.entrySet()) {
      Path directoryPath = entry.getKey();
      boolean isMandatory = entry.getValue();

      if (isMandatory || withOptionals) {
        Files.createDirectories(directoryPath);
      }
    }
  }

  /**
   * Gives the correct sub directory (w.r.t. {@link #baseDirectory}) for the provided class.
   *
   * @param cls Class to define the sub directory for
   * @param fileSeparator The file separator to use
   * @return An Option to the regarding sub directory as a string
   */
  @Override
  public Optional<String> getSubDirectory(Class<? extends UniqueEntity> cls, String fileSeparator) {
    /* Go through all sub directories and check, if the given class belongs to one of the classes mapped to the sub directories. */
    Optional<SubDirectories> maybeSubDirectory =
        Arrays.stream(SubDirectories.values())
            .filter(
                subDirectory ->
                    subDirectory.getRelevantClasses().stream()
                        .anyMatch(definedClass -> definedClass.isAssignableFrom(cls)))
            .findFirst();

    if (!maybeSubDirectory.isPresent()) {
      logger.debug("Don't know a fitting sub directory for class '{}'.", cls.getSimpleName());
      return Optional.empty();
    } else {
      return Optional.of(maybeSubDirectory.get().getRelPath());
    }
  }

  private enum SubDirectories {
    GRID(
        "grid" + FILE_SEPARATOR,
        true,
        Stream.of(
                LineInput.class,
                SwitchInput.class,
                Transformer2WInput.class,
                Transformer3WInput.class,
                MeasurementUnitInput.class,
                NodeInput.class)
            .collect(Collectors.toSet())),
    GLOBAL(
        "global" + FILE_SEPARATOR,
        true,
        Stream.of(
                LineTypeInput.class,
                Transformer2WTypeInput.class,
                Transformer3WTypeInput.class,
                BmTypeInput.class,
                ChpTypeInput.class,
                EvTypeInput.class,
                HpTypeInput.class,
                StorageTypeInput.class,
                WecTypeInput.class,
                OperatorInput.class,
                WecCharacteristicInput.class,
                EvCharacteristicInput.class,
                RandomLoadParameters.class,
                LoadProfileInput.class)
            .collect(Collectors.toSet())),
    PARTICIPANTS(
        "participants" + FILE_SEPARATOR,
        true,
        Stream.of(
                BmInput.class,
                ChpInput.class,
                EvInput.class,
                EvcsInput.class,
                FixedFeedInInput.class,
                HpInput.class,
                LoadInput.class,
                PvInput.class,
                StorageInput.class,
                WecInput.class)
            .collect(Collectors.toSet())),
    TIME_SERIES(
        PARTICIPANTS.relPath + "time_series" + FILE_SEPARATOR,
        false,
        Stream.of(TimeSeries.class, TimeSeriesMapping.Entry.class).collect(Collectors.toSet())),
    THERMAL(
        "thermal" + FILE_SEPARATOR,
        false,
        Stream.of(ThermalUnitInput.class).collect(Collectors.toSet())),
    GRAPHICS(
        "graphics" + FILE_SEPARATOR,
        false,
        Stream.of(GraphicInput.class).collect(Collectors.toSet()));
    private final String relPath;
    private final boolean mandatory;
    private final Set<Class<?>> relevantClasses;

    public String getRelPath() {
      return relPath;
    }

    public boolean isMandatory() {
      return mandatory;
    }

    public Set<Class<?>> getRelevantClasses() {
      return relevantClasses;
    }

    SubDirectories(String relPath, boolean mandatory, Set<Class<?>> relevantClasses) {
      this.relPath = relPath;
      this.mandatory = mandatory;
      this.relevantClasses = relevantClasses;
    }
  }
}
