/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.timeseries.CosmoIdCoordinateFactory
import edu.ie3.util.geo.CoordinateDistance
import edu.ie3.util.geo.GeoUtils
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units

import java.util.stream.Collectors
import java.util.stream.Stream

class CsvIdCoordinateSourceCosmoIT extends Specification implements CsvTestDataMeta {

  @Shared
  CsvIdCoordinateSource source

  def setupSpec() {
    source = new CsvIdCoordinateSource(new CosmoIdCoordinateFactory(), new CsvDataSource(csvSep, coordinatesCosmoFolderPath, fileNamingStrategy))
  }

  def "The CsvCoordinateSource is able to create a valid stream from a coordinate file"() {
    given:
    def expectedStream = Stream.of(
        ["id": "106580", "latGeo": "39.602772", "latRot": "-10", "longGeo": "1.279336", "longRot": "-6.8125", "tid": "1"],
        ["id": "106581", "latGeo": "39.610001", "latRot": "-10", "longGeo": "1.358673", "longRot": "-6.75", "tid": "2"],
        ["id": "106582", "latGeo": "39.617161", "latRot": "-10", "longGeo": "1.438028", "longRot": "-6.6875", "tid": "3"],
        ["id": "106583", "latGeo": "39.624249", "latRot": "-10", "longGeo": "1.5174021", "longRot": "-6.625", "tid": "4"])

    when:
    def actualStream = source.buildStreamWithFieldsToAttributesMap()

    then:
    actualStream.success
    actualStream.data.get().toList() == expectedStream.toList()
  }

  def "The CsvCoordinateSource is able to return the source fields from a coordinate file"() {
    when:
    def sourceFields = source.sourceFields

    then:
    sourceFields.present
    sourceFields.get() == [
      "id",
      "lat_geo",
      "lat_rot",
      "long_geo",
      "long_rot",
      "tid"
    ] as Set<String>
  }

  def "The CsvIdCoordinateSource is able to look up a specific point" () {
    given:
    def knownCoordinateId = 106582
    def expectedPoint = GeoUtils.buildPoint(39.617161, 1.438028)

    when:
    def actualPoint = source.getCoordinate(knownCoordinateId)

    then:
    actualPoint == expectedPoint
  }

  def "The CsvIdCoordinateSource throws an exception if a coordinate id is unknown" () {
    when:
    source.getCoordinate(42)

    then:
    def ex = thrown(SourceException)
    ex.message.contains("42")
  }

  def "The CsvIdCoordinateSource resolves coordinate ids in a batch" () {
    when:
    def actual = source.getCoordinatesById([106580, 106582])

    then:
    actual == [
      (106580): GeoUtils.buildPoint(39.602772, 1.279336),
      (106582): GeoUtils.buildPoint(39.617161, 1.438028)
    ]
  }

  def "The CsvIdCoordinateSource names every unresolvable coordinate id of a batch" () {
    when:
    source.getCoordinatesById([106580, 42, 43])

    then: "the message collects all missing ids instead of failing on the first one"
    def ex = thrown(SourceException)
    ex.message.contains("42")
    ex.message.contains("43")
  }

  def "The CsvIdCoordinateSource is able to look up specified points" () {
    int[] ids = 106580..106582
    def expectedCoordinates = [
      GeoUtils.buildPoint(39.602772, 1.279336),
      GeoUtils.buildPoint(39.610001, 1.358673),
      GeoUtils.buildPoint(39.617161, 1.438028)
    ].toSet()

    when:
    def actualCoordinates = source.getCoordinates(ids)

    then:
    actualCoordinates.toSet() == expectedCoordinates
  }

  def "The CsvIdCoordinateSource is able to return a specific ID" () {
    given:
    def knownCoordinate = GeoUtils.buildPoint(39.602772, 1.279336)

    when:
    def actualId = source.getId(knownCoordinate)

    then:
    actualId == 106580
  }

  def "The CsvIdCoordinateSource throws an exception if a coordinate is unknown" () {
    when:
    source.getId(GeoUtils.buildPoint(48.035011, 14.39335))

    then:
    thrown(SourceException)
  }

  def "The CsvIdCoordinateSource throws an exception if one of several queried ids is unknown" () {
    when:
    source.getCoordinates(106580, 42)

    then: "unresolvable ids no longer vanish from the result"
    thrown(SourceException)
  }

  def "The CsvIdCoordinateSource is able to return a count of all available coordinates" () {
    given:
    def expectedCount = 4

    when:
    def actualCount = source.coordinateCount

    then:
    actualCount == expectedCount
  }

  def "The CsvIdCoordinateSource is able to return all available coordinates" () {
    given:
    def expectedCoordinates = [
      GeoUtils.buildPoint(39.602772, 1.279336),
      GeoUtils.buildPoint(39.610001, 1.358673),
      GeoUtils.buildPoint(39.617161, 1.438028),
      GeoUtils.buildPoint(39.624249, 1.5174021)
    ].toSet()

    when:
    def actualCoordinates = source.allCoordinates.toSet()

    then:
    actualCoordinates == expectedCoordinates
  }

  def "The CsvIdCoordinateSource is able to return the nearest n coordinates in a collection" () {
    given:
    def allCoordinates = [
      GeoUtils.buildPoint(39d, 1d),
      GeoUtils.buildPoint(40d, 2d),
      GeoUtils.buildPoint(40d, 1d),
      GeoUtils.buildPoint(39d, 2d)
    ]

    def basePoint = GeoUtils.buildPoint(39.617162, 1.438029)
    def expectedDistances = [
      new CoordinateDistance(basePoint, allCoordinates[2]),
      new CoordinateDistance(basePoint, allCoordinates[1])
    ].sort()

    when:
    def actualDistances = source.calculateCoordinateDistances(basePoint, 2, allCoordinates)

    then:
    actualDistances == expectedDistances
  }

  def "The CsvIdCoordinateSource will return the nearest n coordinates" () {
    given:
    def n = 5
    def basePoint = GeoUtils.buildPoint(39.617162, 1.438029)
    def expectedDistances = source.calculateCoordinateDistances(basePoint, n, source.allCoordinates)

    when:
    def actualDistances = source.getNearestCoordinates(basePoint, n)

    then:
    actualDistances == expectedDistances
  }

  def "The CsvIdCoordinateSource will return no coordinates if no coordinates are in the given radius" () {
    given:
    def n = 5
    def basePoint = GeoUtils.buildPoint(37.617162, 1.438029)
    def distance = Quantities.getQuantity(100, Units.METRE)

    when:
    def actualDistances = source.getClosestCoordinates(basePoint, n, distance)

    then:
    actualDistances.empty
  }

  def "The CsvIdCoordinateSource will return the nearest n coordinates if n coordinates are in the search radius"() {
    given:
    def basePoint = GeoUtils.buildPoint(39.617162, 1.438029)
    def distance = Quantities.getQuantity(10000, Units.METRE)

    when:
    def actualDistances = source.getClosestCoordinates(basePoint, 3, distance)

    then:
    actualDistances.size() == 3
  }

  def "The CsvIdCoordinateSource will return the nearest m coordinates if less than n coordinates are in the given radius"() {
    given:
    def basePoint = GeoUtils.buildPoint(39.617162, 1.438029)
    def distance = Quantities.getQuantity(1000, Units.METRE)

    when:
    def actualDistances = source.getClosestCoordinates(basePoint, 2, distance)

    then:
    actualDistances.size() == 1
  }

  def "The CsvCoordinateSource will return less than four corner points if not enough points are within the given distance"() {
    given:
    def basePoint = GeoUtils.buildPoint(39.61, 1.38)
    def distance = Quantities.getQuantity(10000, Units.METRE)

    when:
    def actualDistances = source.findCornerPoints(basePoint, distance)

    then:
    actualDistances.size() == 3
  }

  def "The CsvCoordinateSource will return one point if there is an exact match"() {
    given:
    def basePoint = GeoUtils.buildPoint(39.617162, 1.438029)
    def distance = Quantities.getQuantity(100, Units.METRE)

    when:
    def actualDistances = source.findCornerPoints(basePoint, distance)

    then:
    actualDistances.size() == 1
  }
}
