/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.io.CsvFileDefinition;
import edu.ie3.util.io.FileIOUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides the connector (here: buffered writer) for specific files to be used by a {@link
 * edu.ie3.datamodel.io.sink.CsvFileSink}
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileConnector implements DataConnector {

  private static final Logger log = LogManager.getLogger(CsvFileConnector.class);

  private final boolean allowLaterRegistration;

  private final Map<CsvFileDefinition, Optional<BufferedWriter>> fileToWriter;
  private final String csvSeparator;
  private final String baseFolderName;

  private static final String FILE_ENDING = ".csv";

  /**
   * Creates a file connector.
   *
   * @param baseFolderName Path to where the file may be located
   * @param fileDefinitions Collection of file definitions.
   * @param csvSeparator Separator character to separate csv columns
   * @param initFiles true, if the files should be created during initialization (might create
   *     files, that only consist of a headline, because no data will be written into them), false
   *     otherwise
   * @param allowLaterRegistration Allow for registering destination later, when acquiring writers
   * @throws ConnectorException If the connector cannot be established
   */
  public CsvFileConnector(
      String baseFolderName,
      Collection<CsvFileDefinition> fileDefinitions,
      String csvSeparator,
      boolean initFiles,
      boolean allowLaterRegistration)
      throws ConnectorException {
    this.baseFolderName = baseFolderName;
    this.csvSeparator = csvSeparator;
    this.fileToWriter = mapFileDefinitionsToWriter(fileDefinitions, initFiles);
    this.allowLaterRegistration = allowLaterRegistration;
  }

  /**
   * Builds the mapping from file definition to option to an equivalent writer.
   *
   * @param fileDefinitions Collections of file definitions
   * @param initWriter true, if the writers may be instantiated
   * @return An unmodifiable map of file definitions to writers
   * @throws ConnectorException If the initialization of any writer was unsuccessful
   */
  private Map<CsvFileDefinition, Optional<BufferedWriter>> mapFileDefinitionsToWriter(
      Collection<CsvFileDefinition> fileDefinitions, boolean initWriter) throws ConnectorException {
    Map<CsvFileDefinition, Optional<BufferedWriter>> map = new HashMap<>();
    for (CsvFileDefinition fileDefinition : fileDefinitions) {
      if (!initWriter) map.put(fileDefinition, Optional.empty());
      else {
        try {
          BufferedWriter writer =
              initWriter(fileDefinition.getFileName(), fileDefinition.getHeadLineElements());
          map.put(fileDefinition, Optional.of(writer));
        } catch (ConnectorException | IOException e) {
          throw new ConnectorException("Cannot build a writer for \"" + fileDefinition + "\".", e);
        }
      }
    }
    return map;
  }

  /** Closes all buffered writers */
  @Override
  public void shutdown() {
    fileToWriter
        .values()
        .forEach(
            bufferedWriter ->
                bufferedWriter.ifPresent(
                    writer -> {
                      try {
                        writer.close();
                      } catch (IOException e) {
                        log.error("Error during CsvFileConnector shutdown process.", e);
                      }
                    }));
  }

  /**
   * Returns a suitable writer that writes to the given file definition
   *
   * @param fileDefinition Queried file definition
   * @return An option to the writer
   */
  public BufferedWriter getWriter(CsvFileDefinition fileDefinition) throws ConnectorException {
    if (!fileToWriter.containsKey(fileDefinition))
      if (!allowLaterRegistration) {
        throw new ConnectorException(
            "There is no file writer associated with this definition: " + fileDefinition);
      } else {
        fileToWriter.put(fileDefinition, Optional.empty());
      }

    Optional<BufferedWriter> writerOption = fileToWriter.get(fileDefinition);
    if (writerOption.isPresent()) return writerOption.get();
    else {
      try {
        BufferedWriter writer =
            initWriter(fileDefinition.getFileName(), fileDefinition.getHeadLineElements());
        fileToWriter.put(fileDefinition, Optional.of(writer));
        return writer;
      } catch (ConnectorException | IOException e) {
        throw new ConnectorException("Cannot build a writer for \"" + fileDefinition + "\".", e);
      }
    }
  }

  /**
   * Initialises a writer for the given file definition
   *
   * @param fileName File name
   * @param headLineElements Array of head line elements for the implicitly created file
   * @return A writer denoted to that file name
   * @throws ConnectorException If the base folder path is already occupied
   * @throws IOException In any case writing to the file fails.
   */
  private BufferedWriter initWriter(String fileName, String[] headLineElements)
      throws ConnectorException, IOException {
    File basePathDir = new File(baseFolderName);
    if (basePathDir.isFile())
      throw new ConnectorException(
          "Base path dir '" + baseFolderName + "' already exists and is a file!");
    if (!basePathDir.exists()) basePathDir.mkdirs();
    String fullPath = baseFolderName + File.separator + fileName + CsvFileConnector.FILE_ENDING;

    BufferedWriter writer = FileIOUtils.getBufferedWriterUTF8(fullPath);

    // write header
    writeFileHeader(writer, headLineElements, csvSeparator);

    return writer;
  }

  /**
   * Writes the headline to the file implicitly provided by the writer. All entries are quoted
   * ("bla","foo",...).
   *
   * @param writer Buffered writer to use
   * @param headerElements Head line elements
   * @param csvSep Separator character to separate csv columns
   * @throws IOException when the head line appending does not work.
   */
  private void writeFileHeader(BufferedWriter writer, final String[] headerElements, String csvSep)
      throws IOException {
    for (int i = 0; i < headerElements.length; i++) {
      String attribute = headerElements[i];
      writer.append("\"").append(attribute).append("\""); // adds " to headline
      if (i + 1 < headerElements.length) {
        writer.append(csvSep);
      } else {
        writer.append("\n");
      }
    }
    writer.flush();
  }
}
