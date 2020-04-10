/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.integration

import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.io.source.csv.CsvTypeSource

import spock.lang.Shared
import spock.lang.Specification
import edu.ie3.test.common.SystemParticipantTestData as sptd


/**
 * Tests that contains several methods testing I/O capabilities of the sinks and sources
 */
class CsvIOTest extends Specification {

	@Shared
	String testBaseFolderPath = new File(getClass().getResource('/testGridFiles').toURI()).getAbsolutePath()
	String typeFolderPath = testBaseFolderPath.concat(File.separator).concat("types")

	def "A type source should read all provided type files as expected"() {
		given:
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		// bm types
		def bmTypes = typeSource.bmTypes
		bmTypes.size() == 1
		bmTypes.first() == sptd.bmTypeInput

		// todo tests for all types, grid assets, system participants etc. (= all entities)


	}
}
