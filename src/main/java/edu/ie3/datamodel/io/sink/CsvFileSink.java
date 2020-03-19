/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.exceptions.SinkException;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.connectors.DataConnector;
import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.models.UniqueEntity;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileSink implements FileSink {

  private static final Logger log = LogManager.getLogger(CsvFileSink.class);

  private final CsvFileConnector connector;
  private final EntityProcessor<UniqueEntity> processor;

  public CsvFileSink(CsvFileConnector connector, EntityProcessor<UniqueEntity> entityProcessor) {
    this.connector = connector;
    this.processor = entityProcessor;
    writeHeaders();
  }

  @Override
  public void writeHeaders() {
    final String[] columns = processor.getHeaderElements();
    final BufferedWriter writer = connector.getWriter();

    try {
      for (int i = 0; i < columns.length; i++) {
        String attribute = columns[i];
        writer.append("\"" + attribute + "\""); // adds " to headline
        if (i + 1 < columns.length) {
          writer.append(",");
        } else {
          writer.append("\n");
        }
      }
      writer.flush();
    } catch (IOException e) {
      throw new SinkException(e); // todo JH
    }
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
  }

  @Override
  public void persist(UniqueEntity entity) {
    if (processor.getRegisteredClass() == entity.getClass()) {
      write(entity);
    }
  }

  private void write(UniqueEntity entity) {
    final BufferedWriter writer = connector.getWriter();
    processor
        .handleEntity(entity)
        .ifPresent(
            attributeToValue -> {
              try {
                String[] columns = processor.getHeaderElements();
                for (int i = 0; i < columns.length; i++) {
                  String attribute = columns[i];
                  writer.append(attributeToValue.get(attribute));

                  if (i + 1 < columns.length) {
                    writer.append(",");
                  } else {
                    writer.append("\n");
                  }
                }
              } catch (IOException e) {
                log.error("{}", e); // todo JH
              }
            });
  }

  @Override
  public void persistAll(Collection<? extends UniqueEntity> entities) {
    for (UniqueEntity entity : entities) {
      write(entity);
    }
  }
}
