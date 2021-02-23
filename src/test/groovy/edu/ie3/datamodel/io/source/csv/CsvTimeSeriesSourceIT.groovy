/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.csv.FileNamingStrategy
import spock.lang.Shared
import spock.lang.Specification


class CsvTimeSeriesSourceIT extends Specification implements CsvTestDataMeta {

	@Shared
	CsvTimeSeriesSource source

	def setup() {
		source = new CsvTimeSeriesSource(";", timeSeriesFolderPath, new FileNamingStrategy())
	}

	def "Bli bla blubb"() {
		expect:
		1 == 1
	}
}
