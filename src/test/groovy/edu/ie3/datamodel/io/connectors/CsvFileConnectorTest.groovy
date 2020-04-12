/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.connectors

import edu.ie3.datamodel.io.FileNamingStrategy
import spock.lang.Specification


class CsvFileConnectorTest extends Specification {

	def "A CsvFileConnector should transformer given headline string arrays as expected"() {
		given:
		String[] headline = [
			"inputModel",
			"iAMag",
			"timestamp",
			"p",
			"nodeC",
			"tapPos",
			"parallelDevices",
			"kWd",
			"mySa",
			"sRated",
			"xScA",
			"sRatedB"] as String[]
		String[] expectedHeader = [
			"\"input_model\"",
			"\"i_a_mag\"",
			"\"timestamp\"",
			"\"p\"",
			"\"node_c\"",
			"\"tap_pos\"",
			"\"parallel_devices\"",
			"\"k_wd\"",
			"\"my_sa\"",
			"\"s_rated\"",
			"\"x_sc_a\"",
			"\"s_rated_b\""] as String[]

		def fileNamingStrategy = Mock(FileNamingStrategy)
		String baseFolderName = "test"

		CsvFileConnector connector = new CsvFileConnector(baseFolderName, fileNamingStrategy)

		expect:
		connector.prepareHeader(headline) == expectedHeader
	}
}
