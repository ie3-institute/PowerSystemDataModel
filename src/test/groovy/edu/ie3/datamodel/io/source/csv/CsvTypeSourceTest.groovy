/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.models.input.OperatorInput
import spock.lang.Shared
import spock.lang.Specification
import edu.ie3.test.common.SystemParticipantTestData as sptd


class CsvTypeSourceTest extends Specification implements CsvTestDataMeta {

	// todo tests for all types
	//  -> create files in test/resources/testGridFiles/types and create a test for each get method in CsvTypeSource

	def "A CsvTypeSource should read and handle valid 2W Transformer type file as expected"() {

	}

	def "A CsvTypeSource should read and handle valid bm type file as expected"() {
		given:
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def bmTypes = typeSource.bmTypes
		bmTypes.size() == 1
		bmTypes.first() == sptd.bmTypeInput

	}

	def "A CsvTypeSource should read and handle valid operator file as expected"() {
		given:
		def operator = new OperatorInput(
				UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator")
		def typeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())

		expect:
		def operators = typeSource.operators
		operators.size() == 1
		operators.first() == operator

	}


}
