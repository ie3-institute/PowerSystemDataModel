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
  private final CsvFileDefinition fileDefinition;
  /** True, if every entry should be quoted */
  private final boolean quoted;

  /**
   * Build a new CsvBufferedWriter
   *
   * @param baseFolder Base folder, from where the file hierarchy should start
   * @param fileDefinition The foreseen shape of the file
   * @param quoted True, if the entries may be quoted
   * @param append true to append to an existing file, false to overwrite an existing file (if any),
   *     if no file exists, a new one will be created in both cases
   * @throws IOException If the FileOutputStream cannot be established.
   */
  public BufferedCsvWriter(
      String baseFolder,
      CsvFileDefinition fileDefinition,
      boolean quoted,
      boolean writeHeader,
      boolean append)
      throws IOException {
    super(
        new OutputStreamWriter(
            new FileOutputStream(
                baseFolder + File.separator + fileDefinition.getFilePath(), append),
            StandardCharsets.UTF_8));
    this.fileDefinition = fileDefinition;
    this.quoted = quoted;
    if (writeHeader) writeFileHeader(fileDefinition.headLineElements);
  }

  /**
   * Build a new CsvBufferedWriter. All entries are quoted
   *
   * @param baseFolder Base folder, from where the file hierarchy should start
   * @param fileDefinition The foreseen shape of the file
   * @param append true to append to an existing file, false to overwrite an existing file (if any),
   *     if no file exists, a new one will be created in both cases
   * @throws IOException If the FileOutputStream cannot be established.
   */
  public BufferedCsvWriter(
      String baseFolder, CsvFileDefinition fileDefinition, boolean writeHeader, boolean append)
      throws IOException {
    this(baseFolder, fileDefinition, false, writeHeader, append);
  }

  /**
   * Actually persisting the provided entity field data
   *
   * @param entityFieldData a mapping of an entity instance fields to their values
   */
  public void write(Map<String, String> entityFieldData) throws IOException, SinkException {
    /* Check against eligible head line elements */
    String[] eligibleHeadLineElements = fileDefinition.getHeadLineElements();
    if (entityFieldData.size() != eligibleHeadLineElements.length
        || !entityFieldData.keySet().containsAll(Arrays.asList(eligibleHeadLineElements)))
      throw new SinkException(
          "The provided data does not meet the pre-defined head line elements '"
              + String.join(",", eligibleHeadLineElements)
              + "'.");

    String[] entries = entityFieldData.values().toArray(new String[0]);
    writeOneLine(quoted ? StringUtils.quote(entries) : entries);
  }

  /**
   * Writes the file header
   *
   * @throws IOException If something is messed up
   */
  private void writeFileHeader(String[] headLineElements) throws IOException {
    writeOneLine(StringUtils.quote(StringUtils.camelCaseToSnakeCase(headLineElements)));
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
        super.append(fileDefinition.csvSep);
      } else {
        super.append("\n");
      }
    }
    flush();
  }

  @Override
  public Writer append(CharSequence csq) {
    throw new UnsupportedOperationException("Direct appending is prohibited. Use write instead.");
  }

  @Override
  public Writer append(CharSequence csq, int start, int end) {
    throw new UnsupportedOperationException("Direct appending is prohibited. Use write instead.");
  }

  @Override
  public Writer append(char c) {
    throw new UnsupportedOperationException("Direct appending is prohibited. Use write instead.");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BufferedCsvWriter that = (BufferedCsvWriter) o;
    return fileDefinition.equals(that.fileDefinition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileDefinition);
  }

  @Override
  public String toString() {
    return "BufferedCsvWriter{" + "fileDefinition=" + fileDefinition + '}';
  }
}
