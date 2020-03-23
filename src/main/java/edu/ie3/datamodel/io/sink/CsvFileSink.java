/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.exceptions.ProcessorProviderException;
import edu.ie3.datamodel.exceptions.SinkException;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.connectors.DataConnector;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.models.UniqueEntity;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * //ToDo: Class Description // todo this needs to be adapted to be able to process several
 * different entity types -> passing over a collection of entity processors needs to be possible and
 * select the fitting processor based on the provided entity -> this actually IS the old CsvWriter,
 * no need for another wrapper class
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileSink implements DataSink {

  private static final Logger log = LogManager.getLogger(CsvFileSink.class);

  private final CsvFileConnector connector;
  private final ProcessorProvider processorProvider;
  // todo check if this needs to be a globale value or if we can remove it

  private final String csvSep;

  public CsvFileSink(String baseFolderPath) {
    this(baseFolderPath, new ProcessorProvider(), new FileNamingStrategy(), true, ",");
  }

  public CsvFileSink(
      String baseFolderPath,
      ProcessorProvider processorProvider,
      FileNamingStrategy fileNamingStrategy,
      boolean writeFileHeaders,
      String csvSep) {
    this.csvSep = csvSep;
    this.processorProvider = processorProvider;
    this.connector = initConnector(baseFolderPath, processorProvider, fileNamingStrategy);

    // write headers
    if (writeFileHeaders) writeFileHeaders();

    // todo check if connector can become null
  }

  private CsvFileConnector initConnector(
      String baseFolderPath,
      ProcessorProvider processorProvider,
      FileNamingStrategy fileNamingStrategy) {
    CsvFileConnector csvFileConnector = null;
    try {
      List<Class<? extends UniqueEntity>> registeredEntities =
          processorProvider.getRegisteredClasses();
      csvFileConnector =
          new CsvFileConnector(baseFolderPath, registeredEntities, fileNamingStrategy);
    } catch (IOException e) {
      log.error(
          "Exception occurred while initializing the writers of CsvFileConnector writers.", e);
    } catch (ConnectorException e) {
      log.error("Exception occurred while initializing of CsvFileConnector writers.", e);
    }

    return csvFileConnector;
  }

  private void writeFileHeaders() {

    for (final Class<? extends UniqueEntity> registeredClass :
        processorProvider.getRegisteredClasses()) {

      connector
          .getWriter(registeredClass)
          .ifPresent(
              writer -> {
                final String[] columns = processorProvider.getHeaderElements(registeredClass);
                ;
                try {
                  for (int i = 0; i < columns.length; i++) {
                    String attribute = columns[i];
                    writer.append("\"").append(attribute).append("\""); // adds " to headline
                    if (i + 1 < columns.length) {
                      writer.append(csvSep);
                    } else {
                      writer.append("\n");
                    }
                  }
                  writer.flush();
                } catch (IOException e) {
                  throw new SinkException(e); // todo JH
                }
              });
    }
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
  }

  @Override
  public <T extends UniqueEntity> void persistAll(Collection<T> entities) {
    for (T entity : entities) {
      persist(entity);
    }
  }

  @Override
  public <T extends UniqueEntity> void persist(T entity) {

    try {
      processorProvider
          .processEntity(entity)
          .ifPresent(
              entityFieldData ->
                  connector
                      .getWriter(entity.getClass())
                      .ifPresent(
                          writer -> {
                            String[] headerElements =
                                processorProvider.getHeaderElements(entity.getClass());
                            write(entityFieldData, headerElements, writer);
                          }));

    } catch (ProcessorProviderException e) {
      e.printStackTrace(); // todo JH
    }
  }

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
