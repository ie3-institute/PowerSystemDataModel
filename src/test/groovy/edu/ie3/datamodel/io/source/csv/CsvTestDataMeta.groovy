/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.naming.FileNamingStrategy
import spock.lang.Shared

import java.nio.file.Path

/**
 * Holds meta data for csv tests e.g. file and folder paths
 */
trait CsvTestDataMeta {

  @Shared
  Path timeSeriesFolderPath = getResourceAbs("_timeseries")
  @Shared
  Path graphicsFolderPath = getResourceAbs("_graphics")
  @Shared
  Path typeFolderPath = getResourceAbs("_types")
  @Shared
  Path participantsFolderPath =  getResourceAbs("_participants")
  @Shared
  Path resultEntitiesFolderPath = getResourceAbs("_results")
  @Shared
  Path thermalFolderPath = getResourceAbs("_thermal")
  @Shared
  Path coordinatesIconFolderPath = getResourceAbs("_coordinates/icon")
  @Shared
  Path coordinatesCosmoFolderPath = getResourceAbs("_coordinates/cosmo")
  @Shared
  Path weatherCosmoFolderPath = getResourceAbs("_weather/cosmo")
  @Shared
  Path weatherIconFolderPath = getResourceAbs("_weather/icon")
  @Shared
  Path jointGridFolderPath = getResourceAbs("_joint_grid")

  @Shared
  Path gridDefaultFolderPath = getResourceAbs("_grid/default")
  @Shared
  Path gridMalformedFolderPath = getResourceAbs("_grid/malformed")
  @Shared
  Path gridEmptyFolderPath = getResourceAbs("_grid/empty")

  @Shared
  String csvSep = ","
  @Shared
  FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()

  Path getResourceAbs(String directory) {
    return Path.of(getClass().getResource(directory).toURI())
  }
}