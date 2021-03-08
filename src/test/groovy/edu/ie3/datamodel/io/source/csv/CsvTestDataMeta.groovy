/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy

/**
 * Holds meta data for csv tests e.g. file and folder paths
 */
trait CsvTestDataMeta {

	String testParticipantsBaseFolderPath = new File(getClass().getResource('/testGridFiles').toURI()).absolutePath
	String testTimeSeriesBaseFolderPath = new File(getClass().getResource('/testTimeSeriesFiles').toURI()).absolutePath
	String graphicsFolderPath = testParticipantsBaseFolderPath.concat(File.separator).concat("graphics")
	String typeFolderPath = testParticipantsBaseFolderPath.concat(File.separator).concat("types")
	String gridFolderPath = testParticipantsBaseFolderPath.concat(File.separator).concat("grid")
	String participantsFolderPath =  testParticipantsBaseFolderPath.concat(File.separator).concat("participants")
	String timeSeriesFolderPath =  testTimeSeriesBaseFolderPath
	String thermalFolderPath = testParticipantsBaseFolderPath.concat(File.separator).concat("thermal")
	String coordinatesFolderPath = testParticipantsBaseFolderPath.concat(File.separator).concat("coordinates")

	String csvSep = ","
	EntityPersistenceNamingStrategy entityPersistenceNamingStrategy = new EntityPersistenceNamingStrategy()
}