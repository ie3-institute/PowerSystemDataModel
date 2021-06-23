/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import spock.lang.Specification

import edu.ie3.test.common.ResultEntityTestData as retd

class CsvResultEntitySourceTest extends Specification implements CsvTestDataMeta {

	def "A CsvResultEntitySource should read a csv and extract entities correctly"() {
		given:
		def csvResultEntitySource = new CsvResultEntitySource(csvSep,
				resultEntitiesFolderPath, entityPersistenceNamingStrategy)

		when:
		def wecResults = csvResultEntitySource.getWecResults() // existent
		def pvResults = csvResultEntitySource.getPvResults() // existent
		def bmResults = csvResultEntitySource.getBmResults() // existent
		def chpResults = csvResultEntitySource.getChpResults() // non-existent

		then:
		wecResults.size() == retd.wecResultsSize
		pvResults.size() == retd.pvResultsSize
		bmResults.size() == retd.bmResultsSize
		chpResults.isEmpty()

		bmResults.first().getUuid() == retd.bmResultUuid
		bmResults.first().getInputModel() == retd.bmInputModelUuid
		bmResults.first().getP() == retd.bmActivePower
		bmResults.first().getQ() == retd.bmReactivePower
		bmResults.first().getTime() == retd.bmZonedDateTime
	}
}
