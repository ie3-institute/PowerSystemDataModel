/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.csv.FileNamingStrategy
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping
import spock.lang.Specification

class CsvTimeSeriesSourceTest extends Specification implements CsvTestDataMeta {
	def "The csv time series source is able to provide a valid time series mapping from files"() {
		given:
		def source = new CsvTimeSeriesSource(";", participantsFolderPath, new FileNamingStrategy())
		def expectedMapping = [
			new TimeSeriesMapping.Entry(UUID.fromString("58167015-d760-4f90-8109-f2ebd94cda91"), UUID.fromString("b86e95b0-e579-4a80-a534-37c7a470a409"), UUID.fromString("67600124-2475-4a62-a410-0dd6eabb9441")),
			new TimeSeriesMapping.Entry(UUID.fromString("9a9ebfda-dc26-4a40-b9ca-25cd42f6cc3f"), UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8"), UUID.fromString("05a25fe7-11e5-4732-92b0-490cec171c78")),
			new TimeSeriesMapping.Entry(UUID.fromString("9c1c53ea-e575-41a2-a373-a8b2d3ed2c39"), UUID.fromString("90a96daa-012b-4fea-82dc-24ba7a7ab81c"), UUID.fromString("05a25fe7-11e5-4732-92b0-490cec171c78"))
		]

		when:
		def mappingEntries = source.mapping

		then:
		mappingEntries.size() == expectedMapping.size()

		expectedMapping.stream().allMatch { mappingEntries.contains(it) }
	}
}
