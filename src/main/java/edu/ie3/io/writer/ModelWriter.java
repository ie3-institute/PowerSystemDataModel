/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */

package edu.ie3.io.writer;

import java.io.BufferedWriter;

import edu.ie3.io.processor.EntityProcessor;
import edu.ie3.models.UniqueEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class ModelWriter {

  protected static final Logger logger = LogManager.getLogger(ModelWriter.class);

//  protected final BufferedWriter  outWriter; todo
  protected final EntityProcessor entityProcessor;

  public ModelWriter(final String outFileName, EntityProcessor entityProcessor) {
//    this.outWriter = FileIOUtils.getBufferedWriterUTF8(outFileName); // todo
    this.entityProcessor = entityProcessor;
  }

  public abstract void handleUniqueEntity(final UniqueEntity uniqueEntity);

  protected abstract void writeUniqueEntity(final UniqueEntity uniqueEntity);

  public abstract void writeHeaders();

  public abstract void closeFile();
}
