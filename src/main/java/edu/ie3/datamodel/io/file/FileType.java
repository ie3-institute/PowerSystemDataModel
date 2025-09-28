/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.file;

public enum FileType {
  CSV,
  JSON;

  public String extension() {
    return switch (this) {
      case CSV -> ".csv";
      case JSON -> ".json";
    };
  }
}
