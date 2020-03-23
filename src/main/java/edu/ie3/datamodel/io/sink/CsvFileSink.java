/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

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

  private final String csvSep;

  public CsvFileSink(String baseFolderPath) {
    this(baseFolderPath, new ProcessorProvider(), new FileNamingStrategy(), false, ",");
  }

  public CsvFileSink(
      String baseFolderPath,
      ProcessorProvider processorProvider,
      FileNamingStrategy fileNamingStrategy,
      boolean initFiles,
      String csvSep) {
    this.csvSep = csvSep;
    this.processorProvider = processorProvider;
    this.connector = new CsvFileConnector(baseFolderPath, fileNamingStrategy);

    if (initFiles) initFiles(processorProvider, connector);
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

    LinkedHashMap<String, String> entityFieldData =
        processorProvider
            .processEntity(entity)
            .orElseThrow(
                () ->
                    new SinkException(
                        "Cannot persist entity of type '"
                            + entity.getClass().getSimpleName()
                            + "'. Is this sink properly initialized?"));

    String[] headerElements =
        processorProvider.getHeaderElements(entity.getClass()).orElse(new String[0]);
    BufferedWriter writer = connector.getOrInitWriter(entity.getClass(), headerElements, csvSep);
    write(entityFieldData, headerElements, writer);
  }

  private void initFiles(
      final ProcessorProvider processorProvider, final CsvFileConnector connector) {

    processorProvider
        .getRegisteredClasses()
        .forEach(
            clz ->
                processorProvider
                    .getHeaderElements(clz)
                    .ifPresent(
                        headerElements -> connector.getOrInitWriter(clz, headerElements, csvSep)));
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
