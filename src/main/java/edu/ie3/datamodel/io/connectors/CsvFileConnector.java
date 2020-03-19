/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.util.io.FileIOUtils;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileConnector implements DataConnector {

  private final String filePath;
  private final BufferedWriter writer;

  public CsvFileConnector(String fileName) throws IOException {
    this.filePath = fileName;
    this.writer = FileIOUtils.getBufferedWriterUTF8(fileName);
  }

  @Override
  public void shutdown() {
    try {
      writer.close();
    } catch (IOException e) {
      e.printStackTrace(); // todo JH
    }
  }

  public String getFilePath() {
    return filePath;
  }

  public BufferedWriter getWriter() {
    return writer;
  }
}
