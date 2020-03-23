/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.util.io.FileIOUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileConnector implements DataConnector {

  private final Map<Class<? extends UniqueEntity>, BufferedWriter> writers;

  private final String fileEnding = ".csv";

  public CsvFileConnector(
      String baseFolderName,
      Collection<Class<? extends UniqueEntity>> classesToBeWritten,
      FileNamingStrategy fileNamingStrategy)
      throws ConnectorException, IOException {
    this.writers = initWriters(baseFolderName, classesToBeWritten, fileNamingStrategy);
  }

  private Map<Class<? extends UniqueEntity>, BufferedWriter> initWriters(
      String baseFolderName,
      Collection<Class<? extends UniqueEntity>> classesToBeWritten,
      FileNamingStrategy fileNamingStrategy)
      throws ConnectorException, IOException {

    // create the base path folder first
    // todo check if dir exists and throw an exception then!
    File basePathDir = new File(baseFolderName);
    basePathDir.mkdirs();

    Map<Class<? extends UniqueEntity>, BufferedWriter> writersMap = new HashMap<>();
    for (Class<? extends UniqueEntity> clz : classesToBeWritten) {
      String fileName =
          fileNamingStrategy
              .getFileName(clz)
              .orElseThrow(
                  () ->
                      new ConnectorException(
                          "Cannot determine the file name for provided class '"
                              + clz.getSimpleName()
                              + "'."));
      String fullPath = baseFolderName + File.separator + fileName + fileEnding;

      BufferedWriter writer = FileIOUtils.getBufferedWriterUTF8(fullPath);
      writersMap.put(clz, writer);
    }

    return writersMap;
  }

  // todo init nur dann, wenn erster schreibversuch auf file, ansonsten nicht initialisieren!

  @Override
  public void shutdown() {

    writers
        .values()
        .forEach(
            bufferedWriter -> {
              try {
                bufferedWriter.close();
              } catch (IOException e) {
                e.printStackTrace(); // todo JH
              }
            });
  }

  public Optional<BufferedWriter> getWriter(Class<? extends UniqueEntity> clz) {
    return Optional.ofNullable(writers.get(clz));
  }

  //  public String getFilePath() {
  //    return filePath;
  //  }

  //  public BufferedWriter getWriters() {
  //    return writers;
  //  }
}
