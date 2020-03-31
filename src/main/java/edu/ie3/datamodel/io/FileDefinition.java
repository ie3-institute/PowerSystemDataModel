/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to contain all relevant information to prepare a file for reading or writing objects to /
 * from
 */
public class FileDefinition implements Destination {
  private static final Pattern fileNamePattern = Pattern.compile("[\\w\\\\/]+");
  private static final Pattern extensionPattern = Pattern.compile("\\.?([\\w\\.]+)$");
  private static final Pattern fullPathPattern =
      Pattern.compile("(" + fileNamePattern.pattern() + ")\\.+(\\w+)");

  protected final String fileName;
  protected final String fileExtension;
  protected final String[] headLineElements;

  public FileDefinition(String fileName, String fileExtension, String[] headLineElements) {
    if (fileName.matches(fullPathPattern.pattern())) {
      Matcher matcher = extensionPattern.matcher(fileExtension);
      matcher.matches();
      this.fileName = matcher.group(0).replaceAll("\\\\/", File.separator);
    } else if (fileName.matches(fileNamePattern.pattern())) {
      this.fileName = fileName.replaceAll("\\\\/", File.separator);
    } else {
      throw new IllegalArgumentException(
          "The file name \"" + fileName + "\" is no valid file name.");
    }

    if (fileExtension.matches(fullPathPattern.pattern())) {
      Matcher matcher = extensionPattern.matcher(fileExtension);
      matcher.matches();
      this.fileExtension = matcher.group(2).replaceAll("\\.", "");
    } else if (fileName.matches(extensionPattern.pattern())) {
      Matcher matcher = extensionPattern.matcher(fileExtension);
      matcher.matches();
      this.fileExtension = matcher.group(0).replaceAll("\\.", "");
    } else {
      throw new IllegalArgumentException(
          "The extension \"" + fileExtension + "\" is no valid file extension.");
    }

    this.headLineElements = headLineElements;
  }

  public String getFileName() {
    return fileName;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public String[] getHeadLineElements() {
    return headLineElements;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FileDefinition that = (FileDefinition) o;
    return fileName.equals(that.fileName)
        && fileExtension.equals(that.fileExtension)
        && Arrays.equals(headLineElements, that.headLineElements);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(fileName, fileExtension);
    result = 31 * result + Arrays.hashCode(headLineElements);
    return result;
  }

  @Override
  public String toString() {
    return "FileDefinition{"
        + "fileName='"
        + fileName
        + '\''
        + ", fileExtension='"
        + fileExtension
        + '\''
        + ", headLineElements="
        + Arrays.toString(headLineElements)
        + '}';
  }
}
