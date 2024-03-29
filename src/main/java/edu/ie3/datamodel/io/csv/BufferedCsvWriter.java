/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.exceptions.SinkException;
import edu.ie3.util.StringUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
   * Build a new CsvBufferedWriter. The order of headline elements given in this constructor defines
   * the order of columns in file
   *
   * @param filePath String representation of the full path to the target file
   * @param headLineElements Elements of the csv headline
   * @param csvSep csv separator char
   * @param append true to append to an existing file, false to overwrite an existing file (if any),
   *     if no file exists, a new one will be created in both cases
   * @throws IOException If the FileOutputStream cannot be established.
   */
  public BufferedCsvWriter(Path filePath, String[] headLineElements, String csvSep, boolean append)
      throws IOException {
    super(
        new OutputStreamWriter(
            new FileOutputStream(filePath.toFile(), append), StandardCharsets.UTF_8));
    this.headLineElements = headLineElements;
    this.csvSep = csvSep;
  }

  /**
   * Build a new CsvBufferedWriter. This is a "convenience" Constructor. The absolute file path is
   * assembled by concatenation of {@code baseFolder} and {@code fileDefinition}'s file path
   * information. The order of headline elements in {@code fileDefinition} defines the order of
   * columns in file
   *
   * @param baseFolder Base folder, from where the file hierarchy should start
   * @param fileDefinition The foreseen shape of the file
   * @param append true to append to an existing file, false to overwrite an existing file (if any),
   *     if no file exists, a new one will be created in both cases
   * @throws IOException If the FileOutputStream cannot be established.
   */
  public BufferedCsvWriter(Path baseFolder, CsvFileDefinition fileDefinition, boolean append)
      throws IOException {
    this(
        baseFolder.resolve(fileDefinition.getFilePath()),
        fileDefinition.headLineElements(),
        fileDefinition.csvSep(),
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
    /* Check against eligible headline elements */
    if (entityFieldData.size() != headLineElements.length
        || !entityFieldData.keySet().containsAll(Arrays.asList(headLineElements)))
      throw new SinkException(
          "The provided data does not meet the pre-defined head line elements '"
              + String.join(",", headLineElements)
              + "'.");

    writeOneLine(Arrays.stream(headLineElements).map(entityFieldData::get));
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
    writeOneLine(Arrays.stream(entries));
  }

  /**
   * Write one line to the csv file
   *
   * @param entries Stream of entries to write
   * @throws IOException If writing is not possible
   */
  private void writeOneLine(Stream<String> entries) throws IOException {
    super.append(entries.collect(Collectors.joining(csvSep)));
    super.append("\n");
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
    if (!(o instanceof BufferedCsvWriter that)) return false;
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
