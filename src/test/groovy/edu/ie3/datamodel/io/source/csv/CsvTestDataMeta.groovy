/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.FileNamingStrategy


trait CsvTestDataMeta {

	String testBaseFolderPath = new File(getClass().getResource('/testGridFiles').toURI()).absolutePath
	String graphicsFolderPath = testBaseFolderPath.concat(File.separator).concat("graphics")
	String typeFolderPath = testBaseFolderPath.concat(File.separator).concat("types")
	String gridFolderPath = testBaseFolderPath.concat(File.separator).concat("grid")
	String participantsFolderPath =  testBaseFolderPath.concat(File.separator).concat("participants")
	String thermalFolderPath = testBaseFolderPath.concat(File.separator).concat("thermal")

	String csvSep = ","
	FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()
}