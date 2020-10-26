/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.connectors.CsvFileConnector
import edu.ie3.datamodel.utils.CoordinateDistance
import edu.ie3.util.geo.GeoUtils
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

class CsvIdCoordinateSourceTest extends Specification implements CsvTestDataMeta {

	@Shared
	CsvIdCoordinateSource source

	def setupSpec() {
		source = new CsvIdCoordinateSource(csvSep, coordinatesFolderPath, fileNamingStrategy)
	}

	def "The CsvCoordinateSource is able to create a valid stream from a coordinate file"() {
		def expectedStream = Stream.of(
				["id": "193186", "lat": "48.038719", "lon": "14.39335"],
				["id": "193187", "lat": "48.035011", "lon": "14.48661"],
				["id": "193188", "lat": "48.031231", "lon": "14.57985"])
		def connector = new CsvFileConnector(coordinatesFolderPath, fileNamingStrategy)
		when:
		def actualStream = source.buildStreamWithFieldsToAttributesMap(fileNamingStrategy.idCoordinateFileName, connector)
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

	def "The CsvIdCoordinateSource is able to look up a specific point or an empty Optional otherwise" () {
		def idA = 193186
		def expectedPointA = Optional.of(GeoUtils.xyToPoint(48.038719, 14.39335))
		def idB = 42
		when:
		def actualPointA = source.getCoordinate(idA)
		def actualPointB = source.getCoordinate(idB)
		then:
		actualPointA == expectedPointA
		actualPointB == Optional.empty()
	}

	def "The CsvIdCoordinateSource is able to look up specified points" () {
		int[] ids = 193187..193192
		def expectedCoordinates = [
			GeoUtils.xyToPoint(48.031231, 14.57985),
			GeoUtils.xyToPoint(48.035011, 14.48661)
		].toSet()
		when:
		def actualCoordinates = source.getCoordinates(ids)
		then:
		actualCoordinates == expectedCoordinates
	}

	def "The CsvIdCoordinateSource is able to return a specific ID or an empty Optional otherwise" () {
		def pointA = GeoUtils.xyToPoint(48.038719, 14.39335)
		def expectedIdForA = Optional.of(193186)
		def pointB = GeoUtils.xyToPoint(48.035011, 14.39335)
		when:
		def actualIdForA = source.getId(pointA)
		def actualIdForB = source.getId(pointB)
		then:
		actualIdForA == expectedIdForA
		actualIdForB == Optional.empty()
	}

	def "The CsvIdCoordinateSource is able to return a count of all available coordinates" () {
		def expectedCount = 3
		when:
		def actualCount = source.getCoordinateCount()
		then:
		actualCount == expectedCount
	}

	def "The CsvIdCoordinateSource is able to return all available coordinates" () {
		def expectedCoordinates = [
			GeoUtils.xyToPoint(48.038719, 14.39335),
			GeoUtils.xyToPoint(48.035011, 14.48661),
			GeoUtils.xyToPoint(48.031231, 14.57985)
		].toSet()
		when:
		def actualCoordinates = source.allCoordinates.toSet()
		then:
		actualCoordinates == expectedCoordinates
	}

	def "The CsvIdCoordinateSource is able to return the nearest n coordinates in a collection" () {
		def allCoordinates = [
			GeoUtils.xyToPoint(48.038719, 14.39335),
			GeoUtils.xyToPoint(48.035011, 14.48661),
			GeoUtils.xyToPoint(48.031231, 14.57985)
		]
		def basePoint = GeoUtils.xyToPoint(48.0365, 14.48661)
		def expectedDistances = [
			new CoordinateDistance(basePoint, allCoordinates[0]),
			new CoordinateDistance(basePoint, allCoordinates[1])
		].sort()
		when:
		def actualDistances = source.getNearestCoordinates(basePoint, 2)
		then:
		actualDistances == expectedDistances
	}

	def "If no collection is given, the CsvIdCoordinateSource is able to return the nearest n coordinates of all available coordinates" () {
		def n = 2
		def allCoordinates = source.getAllCoordinates()
		def basePoint = GeoUtils.xyToPoint(48.0365, 14.48661)
		def expectedDistances = source.getNearestCoordinates(basePoint, n, allCoordinates)
		when:
		def actualDistances = source.getNearestCoordinates(basePoint, n)
		then:
		actualDistances == expectedDistances
	}
}
