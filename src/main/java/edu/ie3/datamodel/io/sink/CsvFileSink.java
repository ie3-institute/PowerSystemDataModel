/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.exceptions.SinkException;
import edu.ie3.datamodel.io.CsvFileDefinition;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.connectors.DataConnector;
import edu.ie3.datamodel.models.UniqueEntity;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sink that provides all capabilities to write {@link UniqueEntity}s to .csv-files
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileSink implements DataSink<CsvFileDefinition, LinkedHashMap<String, String>> {

  private static final Logger log = LogManager.getLogger(CsvFileSink.class);

  private final CsvFileConnector connector;

  private final String csvSep;

  /**
   * @param baseFolderPath the base folder path where the files should be put into
   * @param fileDefinitions Collection of file definitions
   * @param csvSep csv file separator that should be use
   * @param initFiles true, if the files should be created during initialization (might create
   *     files, that only consist of a headline, because no data will be written into them), false
   *     otherwise
   * @param allowLaterRegistration Allows for later registration of destinations upon acquiring
   *     writer
   */
  public CsvFileSink(
      String baseFolderPath,
      Collection<CsvFileDefinition> fileDefinitions,
      String csvSep,
      boolean initFiles,
      boolean allowLaterRegistration) {
    this.csvSep = csvSep;
    try {
      this.connector =
          new CsvFileConnector(
              baseFolderPath, fileDefinitions, csvSep, initFiles, allowLaterRegistration);
    } catch (ConnectorException e) {
      throw new SinkException("Error during initialization of the file sink.", e);
    }
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
  }

  /**
   * Persists the given data to the specified location.
   *
   * @param destination Specific location of the data
   * @param data Data to persist
   */
  @Override
  public void persist(CsvFileDefinition destination, LinkedHashMap<String, String> data) {
    BufferedWriter writer;
    try {
      writer = connector.getWriter(destination);
    } catch (ConnectorException e) {
      throw new SinkException(
          "Cannot find a matching writer for file definition: \"" + destination + "\".", e);
    }

    if (data.keySet().size() != destination.getHeadLineElements().length
        || !data.keySet().containsAll(Arrays.asList(destination.getHeadLineElements()))) {
      throw new SinkException("The provided data does not match the head line definition!");
    }

    write(data, destination.getHeadLineElements(), writer);
  }

  /**
   * Persists the given amount of data to the specified location.
   *
   * @param destination Specific location of the data
   * @param data Data to persist
   */
  @Override
  public void persistAll(
      CsvFileDefinition destination, Collection<LinkedHashMap<String, String>> data) {
    BufferedWriter writer;
    try {
      writer = connector.getWriter(destination);
    } catch (ConnectorException e) {
      throw new SinkException(
          "Cannot find a matching writer for file definition: \"" + destination + "\".", e);
    }

    data.forEach(
        entry -> {
          if (entry.keySet().size() != destination.getHeadLineElements().length
              || !entry.keySet().containsAll(Arrays.asList(destination.getHeadLineElements()))) {
            throw new SinkException("The provided data does not match the head line definition!");
          }

          write(entry, destination.getHeadLineElements(), writer);
        });
  }

  /**
   * Actually persisting the provided entity field data
   *
   * @param entityFieldData a mapping of an entity instance fields to their values
   * @param headerElements the header elements of the entity, normally all attributes of the entity
   *     class
   * @param writer the corresponding writer for that should be used
   */
  private void write(
      LinkedHashMap<String, String> entityFieldData,
      String[] headerElements,
      BufferedWriter writer) {

    try {
      for (int i = 0; i < headerElements.length; i++) {
        String attribute = headerElements[i];
        writer.append(entityFieldData.get(attribute));
        if (i + 1 < headerElements.length) {
          writer.append(csvSep);
        } else {
          writer.append("\n");
        }
      }
      writer.flush();
    } catch (IOException e) {
      log.error(
          "Error while writing entity with field data: "
              + Arrays.toString(entityFieldData.entrySet().toArray()),
          e);
    }
  }
}
