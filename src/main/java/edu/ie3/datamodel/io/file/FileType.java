/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.file;

import edu.ie3.datamodel.exceptions.ParsingException;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum FileType {
  CSV(".csv"),
  JSON(".json");

  public final String fileEnding;

  FileType(String fileEnding) {
    this.fileEnding = fileEnding;
  }

  public static FileType getFileType(String fileName) throws ParsingException {
    FileType[] fileTypes = FileType.values();
    return Arrays.stream(fileTypes)
        .filter(f -> fileName.endsWith(f.fileEnding))
        .findFirst()
        .orElseThrow(
            () ->
                new ParsingException(
                    "No file ending found for file '"
                        + fileName
                        + "'. Only supports file types: "
                        + Arrays.stream(fileTypes)
                            .map(t -> t.fileEnding)
                            .collect(Collectors.joining(", "))));
  }
}
