/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.naming.FileNamingStrategy

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Holds meta data for csv tests e.g. file and folder paths
 */
trait CsvTestDataMeta {

  static Path timeSeriesFolderPath = CsvTestDataMeta.getResourceAbs("_timeseries")
  static Path graphicsFolderPath = CsvTestDataMeta.getResourceAbs("_graphics")
  static Path typeFolderPath = CsvTestDataMeta.getResourceAbs("_types")
  static Path participantsFolderPath =  CsvTestDataMeta.getResourceAbs("_participants")
  static Path resultEntitiesFolderPath = CsvTestDataMeta.getResourceAbs("_results")
  static Path thermalFolderPath = CsvTestDataMeta.getResourceAbs("_thermal")
  static Path coordinatesIconFolderPath = CsvTestDataMeta.getResourceAbs("_coordinates/icon")
  static Path coordinatesCosmoFolderPath = CsvTestDataMeta.getResourceAbs("_coordinates/cosmo")
  static Path weatherCosmoFolderPath = CsvTestDataMeta.getResourceAbs("_weather/cosmo")
  static Path weatherIconFolderPath = CsvTestDataMeta.getResourceAbs("_weather/icon")
  static Path jointGridFolderPath = CsvTestDataMeta.getResourceAbs("_joint_grid")

  static Path gridDefaultFolderPath = CsvTestDataMeta.getResourceAbs("_grid/default")
  static Path gridMalformedFolderPath = CsvTestDataMeta.getResourceAbs("_grid/malformed")
  static Path gridEmptyFolderPath = CsvTestDataMeta.getResourceAbs("_grid/empty")

  static String csvSep = ","
  static FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()

  static Path getResourceAbs(String directory) {
    return Path.of(CsvTestDataMeta.getResource(directory).toURI())
  }
}