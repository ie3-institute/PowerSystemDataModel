/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.exceptions.FileException;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput;
import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.connector.SwitchResult;
import edu.ie3.datamodel.models.result.connector.Transformer2WResult;
import edu.ie3.datamodel.models.result.connector.Transformer3WResult;
import edu.ie3.datamodel.models.result.system.*;
import edu.ie3.datamodel.models.result.thermal.ThermalUnitResult;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Default directory hierarchy for input models */
public class DefaultDirectoryHierarchy implements FileHierarchy {
  private static final Logger logger = LoggerFactory.getLogger(DefaultDirectoryHierarchy.class);

  /** Base directory for this specific grid model. The base path should be a directory. */
  private final Path baseDirectory;

  /** The project's directory beneath the {@code baseDirectory} */
  private final Path projectDirectory;

  /** Mapping from sub directories to if they are mandatory or not */
  private final Map<Path, Boolean> subDirectories;

  /** Set of additional paths (sub directory trees), that are permissible to exist */
  private final Path inputTree;

  private final Path resultTree;

  public DefaultDirectoryHierarchy(Path baseDirectory, String gridName) {
    /* Prepare the base path */
    Path baseDirectoryNormalized =
        Path.of(FilenameUtils.normalizeNoEndSeparator(String.valueOf(baseDirectory), true));
    this.baseDirectory = baseDirectoryNormalized.toAbsolutePath();
    this.projectDirectory = baseDirectoryNormalized.resolve(gridName).toAbsolutePath();

    /* Prepare the sub directories by appending the relative path to base path and mapping to information about being mandatory */
    this.subDirectories =
        Arrays.stream(SubDirectories.values())
            .collect(
                Collectors.toMap(
                    subDirectory -> this.projectDirectory.resolve(subDirectory.getRelPath()),
                    SubDirectories::isMandatory));

    inputTree = projectDirectory.resolve(SubDirectories.Constants.INPUT_SUB_TREE);
    resultTree = projectDirectory.resolve(SubDirectories.Constants.RESULT_SUB_TREE);
  }

  /**
   * Checks, if the structure beneath {@link #projectDirectory} is okay.
   *
   * @throws FileException if not
   */
  public void validate() throws FileException {
    if (!Files.exists(projectDirectory))
      throw new FileException("The path '" + projectDirectory + "' does not exist.");
    if (!Files.isDirectory(projectDirectory))
      throw new FileException("The path '" + projectDirectory + "' has to be a directory.");

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
   * Checks the elements, that are further available underneath the {@link #projectDirectory}. If
   * there is a directory, that is neither mandatory, nor optional, raise an Exception.
   *
   * @throws FileException if there is an unexpected directory
   */
  private void checkFurtherDirectoryElements() throws FileException {
    try (Stream<Path> apparentElementsStream = Files.list(projectDirectory)) {
      for (Path apparentPath : apparentElementsStream.toList()) {
        if (Files.isDirectory(apparentPath)
            && !subDirectories.containsKey(apparentPath)
            && apparentPath.compareTo(inputTree) != 0
            && apparentPath.compareTo(resultTree) != 0)
          throw new FileException(
              "There is a directory '"
                  + apparentPath
                  + "' apparent, that is not supported by the default directory hierarchy.");
      }
    } catch (IOException e) {
      throw new FileException(
          "Cannot get the list of apparent elements in '" + projectDirectory + "'.", e);
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
    Files.createDirectories(projectDirectory);
    for (Map.Entry<Path, Boolean> entry : subDirectories.entrySet()) {
      Path directoryPath = entry.getKey();
      boolean isMandatory = entry.getValue();

      if (isMandatory || withOptionals) {
        Files.createDirectories(directoryPath);
      }
    }
  }

  /**
   * Gives the {@link #baseDirectory}).
   *
   * @return An Option to the base directory as a string
   * @deprecated Use {@link edu.ie3.datamodel.io.connectors.CsvFileConnector} instead
   */
  @Deprecated(since = "3.0", forRemoval = true)
  @Override
  public Optional<Path> getBaseDirectory() {
    return Optional.of(this.baseDirectory);
  }

  /**
   * Gives the correct sub directory (w.r.t. {@link #baseDirectory}) for the provided class.
   *
   * @param cls Class to define the sub directory for
   * @return An Option to the regarding sub directory as a string
   */
  @Override
  public Optional<Path> getSubDirectory(Class<? extends Entity> cls) {
    /* Go through all sub directories and check, if the given class belongs to one of the classes mapped to the sub directories. */
    Optional<SubDirectories> maybeSubDirectory =
        Arrays.stream(SubDirectories.values())
            .filter(
                subDirectory ->
                    subDirectory.getRelevantClasses().stream()
                        .anyMatch(definedClass -> definedClass.isAssignableFrom(cls)))
            .findFirst();

    if (maybeSubDirectory.isEmpty()) {
      logger.debug("Don't know a fitting sub directory for class '{}'.", cls.getSimpleName());
      return Optional.empty();
    } else {
      /* Build the full path and then refer it to the base directory */
      Path fullPath = this.projectDirectory.resolve(maybeSubDirectory.get().getRelPath());
      Path relPath = this.baseDirectory.relativize(fullPath);

      return Optional.of(relPath);
    }
  }

  private enum SubDirectories {
    GRID_INPUT(
        Constants.INPUT_SUB_TREE.resolve("grid"),
        true,
        Stream.of(
                LineInput.class,
                SwitchInput.class,
                Transformer2WInput.class,
                Transformer3WInput.class,
                MeasurementUnitInput.class,
                NodeInput.class)
            .collect(Collectors.toSet())),
    GRID_RESULT(
        Constants.RESULT_SUB_TREE.resolve("grid"),
        false,
        Stream.of(
                LineResult.class,
                SwitchResult.class,
                Transformer2WResult.class,
                Transformer3WResult.class,
                NodeResult.class)
            .collect(Collectors.toSet())),
    GLOBAL(
        Constants.INPUT_SUB_TREE.resolve("global"),
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
                WecCharacteristicInput.class)
            .collect(Collectors.toSet())),
    PARTICIPANTS_INPUT(
        Constants.INPUT_SUB_TREE.resolve("participants"),
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
    PARTICIPANTS_RESULTS(
        Constants.RESULT_SUB_TREE.resolve("participants"),
        false,
        Stream.of(
                BmResult.class,
                ChpResult.class,
                EvResult.class,
                EvcsResult.class,
                FixedFeedInResult.class,
                HpResult.class,
                LoadResult.class,
                PvResult.class,
                StorageResult.class,
                WecResult.class,
                EmResult.class,
                FlexOptionsResult.class)
            .collect(Collectors.toSet())),
    TIME_SERIES(
        PARTICIPANTS_INPUT.relPath.resolve("time_series"),
        false,
        Stream.of(TimeSeries.class, TimeSeriesMappingSource.MappingEntry.class)
            .collect(Collectors.toSet())),
    THERMAL_INPUT(
        Constants.INPUT_SUB_TREE.resolve("thermal"),
        false,
        Stream.of(ThermalUnitInput.class, ThermalBusInput.class).collect(Collectors.toSet())),
    THERMAL_RESULTS(
        Constants.RESULT_SUB_TREE.resolve("thermal"),
        false,
        Stream.of(ThermalUnitResult.class).collect(Collectors.toSet())),
    GRAPHICS(
        Constants.INPUT_SUB_TREE.resolve("graphics"),
        false,
        Stream.of(GraphicInput.class).collect(Collectors.toSet()));
    private final Path relPath;
    private final boolean mandatory;
    private final Set<Class<?>> relevantClasses;

    public Path getRelPath() {
      return relPath;
    }

    public boolean isMandatory() {
      return mandatory;
    }

    public Set<Class<?>> getRelevantClasses() {
      return relevantClasses;
    }

    SubDirectories(Path relPath, boolean mandatory, Set<Class<?>> relevantClasses) {
      this.relPath = relPath;
      this.mandatory = mandatory;
      this.relevantClasses = Collections.unmodifiableSet(relevantClasses);
    }

    private static class Constants {
      private static final Path INPUT_SUB_TREE = Paths.get("input");
      private static final Path RESULT_SUB_TREE = Paths.get("results");
    }
  }
}
