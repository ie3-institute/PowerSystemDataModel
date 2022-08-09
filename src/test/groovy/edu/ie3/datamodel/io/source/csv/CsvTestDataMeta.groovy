/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.naming.FileNamingStrategy

import java.nio.file.Paths

/**
 * Holds meta data for csv tests e.g. file and folder paths
 */
trait CsvTestDataMeta {

	static String timeSeriesFolderPath = getResourceAbs("_timeseries")
	static String graphicsFolderPath = getResourceAbs("_graphics")
	static String typeFolderPath = getResourceAbs("_types")
	static String participantsFolderPath =  getResourceAbs("_participants")
	static String resultEntitiesFolderPath = getResourceAbs("_results")
	static String thermalFolderPath = getResourceAbs("_thermal")
	static String coordinatesIconFolderPath = getResourceAbs("_coordinates/icon")
	static String coordinatesCosmoFolderPath = getResourceAbs("_coordinates/cosmo")
	static String weatherCosmoFolderPath = getResourceAbs("_weather/cosmo")
	static String weatherIconFolderPath = getResourceAbs("_weather/icon")
	static String jointGridFolderPath = getResourceAbs("_joint_grid")

	static String gridDefaultFolderPath = getResourceAbs("_grid/default")
	static String gridMalformedFolderPath = getResourceAbs("_grid/malformed")
	static String gridEmptyFolderPath = getResourceAbs("_grid/empty")

	static String csvSep = ","
	static FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()

	static String getResourceAbs(String directory) {
		return Paths.get(CsvTestDataMeta.getResource(directory).toURI()).toString()
	}
}