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

	static String timeSeriesFolderPath = getResourceAbs("timeseries")
	static String graphicsFolderPath = getResourceAbs("graphics")
	static String typeFolderPath = getResourceAbs("types")
	static String gridFolderPath = getResourceAbs("grid")
	static String participantsFolderPath =  getResourceAbs("participants")
	static String resultEntitiesFolderPath = getResourceAbs("results")
	static String thermalFolderPath = getResourceAbs("thermal")
	static String coordinatesIconFolderPath = getResourceAbs("coordinates_icon")
	static String coordinatesCosmoFolderPath = getResourceAbs("coordinates_cosmo")

	static String csvSep = ","
	static FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()

	static String getResourceAbs(String directory) {
		return Paths.get(CsvTestDataMeta.getResource(directory).toURI()).toString()
	}
}