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
		def wecResults = csvResultEntitySource.wecResults // existent
		def pvResults = csvResultEntitySource.pvResults // existent
		def bmResults = csvResultEntitySource.bmResults // existent
		def chpResults = csvResultEntitySource.chpResults // non-existent

		then:
		wecResults.size() == retd.WEC_RESULT_SIZE
		pvResults.size() == retd.PV_RESULT_SIZE
		bmResults.size() == retd.BM_RESULT_SIZE
		chpResults.empty

		bmResults.first().uuid == retd.BM_UUID
		bmResults.first().inputModel == retd.BM_INPUT_MODEL
		bmResults.first().p == retd.BM_ACTIVE_POWER
		bmResults.first().q == retd.BM_REACTIVE_POWER
		bmResults.first().time == retd.BM_TIME
	}
}
