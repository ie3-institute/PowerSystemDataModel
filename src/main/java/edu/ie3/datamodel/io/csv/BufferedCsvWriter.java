/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.exceptions.SinkException;
import edu.ie3.util.StringUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * This class extends the {@link BufferedWriter} and adds information about the file shape of the
 * csv file
 */
public class BufferedCsvWriter extends BufferedWriter {
  /** Information on the shape of the file */
  private final String[] headLineElements;

  private final String csvSep;

  private static final String APPENDING_WARNING =
          "Direct appending is prohibited. Use write instead.";

  /**
   * Build a new CsvBufferedWriter
   *
   * @param filePath String representation of the full path to the target file
   * @param headLineElements Elements of the csv head line
   * @param csvSep csv separator char
   * @param append true to append to an existing file, false to overwrite an existing file (if any),
   *     if no file exists, a new one will be created in both cases
   * @throws IOException If the FileOutputStream cannot be established.
   */
  public BufferedCsvWriter(
      String filePath, String[] headLineElements, String csvSep, boolean append)
      throws IOException {
    super(new OutputStreamWriter(new FileOutputStream(filePath, append), StandardCharsets.UTF_8));
    this.headLineElements = headLineElements;
    this.csvSep = csvSep;
  }

  /**
   * Build a new CsvBufferedWriter. This is a "convenience" Constructor. The absolute file path is
   * assembled by concatenation of {@code baseFolder} and {@code fileDefinition}'s file path
   * information.
   *
   * @param baseFolder Base folder, from where the file hierarchy should start
   * @param fileDefinition The foreseen shape of the file
   * @param append true to append to an existing file, false to overwrite an existing file (if any),
   *     if no file exists, a new one will be created in both cases
   * @throws IOException If the FileOutputStream cannot be established.
   */
  public BufferedCsvWriter(String baseFolder, CsvFileDefinition fileDefinition, boolean append)
      throws IOException {
    this(
        baseFolder + File.separator + fileDefinition.getFilePath(),
        fileDefinition.getHeadLineElements(),
        fileDefinition.getCsvSep(),
        append);
  }

  /**
   * Actually persisting the provided entity field data
   *
   * @param entityFieldData a mapping of an entity instance fields to their values
   * @throws IOException If writing has failed
   * @throws SinkException If the data does not meet the pre-defined head line
   */
  public synchronized void write(Map<String, String> entityFieldData)
      throws IOException, SinkException {
    /* Check against eligible head line elements */
    if (entityFieldData.size() != headLineElements.length
        || !entityFieldData.keySet().containsAll(Arrays.asList(headLineElements)))
      throw new SinkException(
          "The provided data does not meet the pre-defined head line elements '"
              + String.join(",", headLineElements)
              + "'.");

    String[] entries = entityFieldData.values().toArray(new String[0]);
    writeOneLine(entries);
  }

  /**
   * Writes the file header.
   *
   * @throws IOException If something is messed up
   */
  public final synchronized void writeFileHeader() throws IOException {
    writeOneLine(StringUtils.camelCaseToSnakeCase(headLineElements));
  }

  /**
   * Writes one line to the csv file
   *
   * @param entries Entries to write to the line of the file
   * @throws IOException If writing is not possible
   */
  private void writeOneLine(String[] entries) throws IOException {
    for (int i = 0; i < entries.length; i++) {
      String attribute = entries[i];
      super.append(attribute);
      if (i + 1 < entries.length) {
        super.append(csvSep);
      } else {
        super.append("\n");
      }
    }
    flush();
  }

  @Override
  public Writer append(CharSequence csq) {
    throw new UnsupportedOperationException(APPENDING_WARNING);
  }

  @Override
  public Writer append(CharSequence csq, int start, int end) {
    throw new UnsupportedOperationException(APPENDING_WARNING);
  }

  @Override
  public Writer append(char c) {
    throw new UnsupportedOperationException(APPENDING_WARNING);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BufferedCsvWriter)) return false;
    BufferedCsvWriter that = (BufferedCsvWriter) o;
    return Arrays.equals(headLineElements, that.headLineElements) && csvSep.equals(that.csvSep);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(csvSep);
    result = 31 * result + Arrays.hashCode(headLineElements);
    return result;
  }

  @Override
  public String toString() {
    return "BufferedCsvWriter{"
        + "headLineElements="
        + Arrays.toString(headLineElements)
        + ", csvSep='"
        + csvSep
        + '\''
        + '}';
  }
}
