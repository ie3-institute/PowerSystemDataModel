/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.connectors.CsvFileConnector
import edu.ie3.util.geo.GeoUtils
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

class CsvCoordinateSourceTest extends Specification implements CsvTestDataMeta {

	@Shared
	CsvCoordinateSource source

	def setupSpec() {
		source = new CsvCoordinateSource(csvSep, coordinatesFolderPath, fileNamingStrategy)
	}

	def "The CsvCoordinateSource is able to create a valid stream from a coordinate file"() {
		def expectedStream = Stream.of(
				["id": "193186", "lat": "48.038719", "lon": "14.39335"],
				["id": "193187", "lat": "48.035011", "lon": "14.48661"],
				["id": "193188", "lat": "48.031231", "lon": "14.57985"])
		def connector = new CsvFileConnector(coordinatesFolderPath, fileNamingStrategy)
		when:
		def actualStream = source.buildStreamWithFieldsToAttributesMap(fileNamingStrategy.coordinateFileName, connector)
		then:
		actualStream.collect(Collectors.toList()).containsAll(expectedStream.collect(Collectors.toList()))
	}

	def "The CsvCoordinateSource is able to convert a (fieldname -> fieldValue) stream to an (id -> Point) map"() {
		def validStream = Stream.of([
			"id"			: "42",
			"lat"			: "3.07",
			"lon"		    : "19.95"])
		def expectedMap = [42 : GeoUtils.xyToPoint(3.07,19.95)]
		when:
		def actualMap = source.buildIdToCoordinateMap(validStream)
		then:
		actualMap == expectedMap
	}
}
