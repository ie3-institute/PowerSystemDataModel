/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.naming.FileNamingStrategy

import java.nio.file.Path

/**
 * Holds meta data for csv tests e.g. file and folder paths
 */
trait CsvTestDataMeta {

  Path timeSeriesFolderPath = getResourceAbs("_timeseries")
  Path graphicsFolderPath = getResourceAbs("_graphics")
  Path typeFolderPath = getResourceAbs("_types")
  Path participantsFolderPath =  getResourceAbs("_participants")
  Path resultEntitiesFolderPath = getResourceAbs("_results")
  Path thermalFolderPath = getResourceAbs("_thermal")
  Path coordinatesIconFolderPath = getResourceAbs("_coordinates/icon")
  Path coordinatesCosmoFolderPath = getResourceAbs("_coordinates/cosmo")
  Path weatherCosmoFolderPath = getResourceAbs("_weather/cosmo")
  Path weatherIconFolderPath = getResourceAbs("_weather/icon")
  Path jointGridFolderPath = getResourceAbs("_joint_grid")

  Path gridDefaultFolderPath = getResourceAbs("_grid/default")
  Path gridMalformedFolderPath = getResourceAbs("_grid/malformed")
  Path gridEmptyFolderPath = getResourceAbs("_grid/empty")

  String csvSep = ","
  FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()

  Path getResourceAbs(String directory) {
    return Path.of(getClass().getResource(directory).toURI())
  }
}