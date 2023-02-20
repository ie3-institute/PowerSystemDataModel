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

  static Path timeSeriesFolderPath = getResourceAbs("_timeseries")
  static Path graphicsFolderPath = getResourceAbs("_graphics")
  static Path typeFolderPath = getResourceAbs("_types")
  static Path participantsFolderPath =  getResourceAbs("_participants")
  static Path resultEntitiesFolderPath = getResourceAbs("_results")
  static Path thermalFolderPath = getResourceAbs("_thermal")
  static Path coordinatesIconFolderPath = getResourceAbs("_coordinates/icon")
  static Path coordinatesCosmoFolderPath = getResourceAbs("_coordinates/cosmo")
  static Path weatherCosmoFolderPath = getResourceAbs("_weather/cosmo")
  static Path weatherIconFolderPath = getResourceAbs("_weather/icon")
  static Path jointGridFolderPath = getResourceAbs("_joint_grid")

  static Path gridDefaultFolderPath = getResourceAbs("_grid/default")
  static Path gridMalformedFolderPath = getResourceAbs("_grid/malformed")
  static Path gridEmptyFolderPath = getResourceAbs("_grid/empty")

  static String csvSep = ","
  static FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()

  static Path getResourceAbs(String directory) {
    return Paths.get(CsvTestDataMeta.getResource(directory).toURI())
  }
}