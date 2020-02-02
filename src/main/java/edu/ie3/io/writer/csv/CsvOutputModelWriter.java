/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.io.writer.csv;

import edu.ie3.io.processor.result.ResultEntityProcessor;
import edu.ie3.io.writer.ModelWriter;
import edu.ie3.models.UniqueEntity;
import edu.ie3.util.TimeTools;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Locale;


public class CsvOutputModelWriter extends ModelWriter {

    private final LinkedHashMap<String, Method> attributeToMethod = new LinkedHashMap<>();

    public CsvOutputModelWriter(String outFileName, ResultEntityProcessor resultEntityProcessor) {
        super(outFileName, resultEntityProcessor);
        writeHeaders();

        // set time tools according to database export
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss");
    }

  @Override
  public void handleUniqueEntity(UniqueEntity uniqueEntity) {
    if(entityProcessor.getRegisteredClass() == uniqueEntity.getClass()) {
      writeUniqueEntity(uniqueEntity);
    }

  }

  @Override
  protected void writeUniqueEntity(UniqueEntity uniqueEntity) {
    entityProcessor.handleEntity(uniqueEntity).ifPresent(attributeToValue -> {
      try {
        String[] columns = entityProcessor.getHeaderElements();
        for(int i = 0; i < columns.length; i++) {
          String attribute = columns[i];
          this.outWriter.append(attributeToValue.get(attribute));

          if(i + 1 < columns.length) {
            this.outWriter.append(",");
          } else {
            this.outWriter.append("\n");
          }
        }
      } catch(IOException e) {
        logger.error("{}", e);
      }
    });
  }

  @Override
    public void writeHeaders() {
        String[] columns = entityProcessor.getHeaderElements();

        try {
            for(int i = 0; i < columns.length; i++) {
                String attribute = columns[i];
                this.outWriter.append("\"" + attribute + "\""); // adds " to headline
                if(i + 1 < columns.length) {
                    this.outWriter.append(",");
                } else {
                    this.outWriter.append("\n");
                }
            }
            outWriter.flush();
        } catch(IOException e) {
            throw new FileIOException(e);
        }
    }

    @Override
    public void closeFile() {
        try {
            this.outWriter.close();
            System.out.println("Closing file ...");
        } catch(IOException e) {
            throw new FileIOException(e);
        }
    }
}
