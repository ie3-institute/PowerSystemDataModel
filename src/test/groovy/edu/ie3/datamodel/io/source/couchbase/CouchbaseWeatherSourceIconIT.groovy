/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.couchbase

import edu.ie3.datamodel.io.connectors.CouchbaseConnector
import edu.ie3.datamodel.io.factory.timeseries.IconTimeBasedWeatherValueFactory
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.test.common.IconWeatherTestData
import edu.ie3.test.helper.TestContainerHelper
import edu.ie3.test.helper.WeatherSourceTestHelper
import edu.ie3.util.interval.ClosedInterval
import groovy.json.JsonSlurper
import org.testcontainers.couchbase.BucketDefinition
import org.testcontainers.couchbase.CouchbaseContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

import java.time.Duration

@Testcontainers
class CouchbaseWeatherSourceIconIT extends Specification implements TestContainerHelper, WeatherSourceTestHelper {

  @Shared
  BucketDefinition bucketDefinition = new BucketDefinition("ie3_in")

  @Shared
  CouchbaseContainer couchbaseContainer = new CouchbaseContainer("couchbase/server:8.0.0")
  .withBucket(bucketDefinition)
  .withExposedPorts(8091, 8092, 8093, 8094, 11210)
  .withStartupAttempts(3) // 3 attempts because startup (node renaming) sometimes fails when executed too early

  @Shared
  CouchbaseWeatherSource source

  static String coordinateIdColumnName = "coordinateid"

  def setupSpec() {
    // create an index for the document keys
    couchbaseContainer.execInContainer("cbq",
        "-e", "http://localhost:8093",
        "-u", couchbaseContainer.username,
        "-p", couchbaseContainer.password,
        "-s", "CREATE index id_idx ON `" + bucketDefinition.name + "` (META().id);")

    // Create connector to import the data from json document
    def importConnector = new CouchbaseConnector(
        couchbaseContainer.connectionString,
        bucketDefinition.name,
        couchbaseContainer.username,
        couchbaseContainer.password,
        Duration.ofSeconds(30))

    // Wait for bucket to be ready
    println "Waiting for Couchbase bucket to be ready..."
    int maxTries = 10
    boolean ready = false
    for (int i = 0; i < maxTries; i++) {
      try {
        importConnector.getSourceFields()
        ready = true
        println "Couchbase bucket ready"
        break
      } catch (Exception ex) {
        if (i % 10 == 0) {
          println "Waiting for bucket... (try ${i+1})"
        }
        sleep(1000)
      }
    }

    if (!ready) {
      println "Couchbase bucket did not become ready in time!"
      System.out.flush()
      throw new RuntimeException("Couchbase bucket did not become ready in time!")
    }

    // Insert test data from JSON file
    println "Inserting test data from JSON file..."
    def jsonFile = new File("src/test/resources/edu/ie3/datamodel/io/source/couchbase/_weather/icon/weather.json")
    def jsonSlurper = new groovy.json.JsonSlurper()
    def weatherDocs = jsonSlurper.parse(jsonFile) as List
    def insertCount = 0
    weatherDocs.each { doc ->
      try {
        def coordinateId = doc["coordinateid"]
        def time = doc["time"]
        def key = "weather::${coordinateId}::${time}"
        importConnector.persist(key, doc).join()
        insertCount++
      } catch (Exception ex) {
        println "WARNING: Failed to insert document for coordinateid ${doc["coordinateid"]} and time ${doc["time"]}: ${ex.message}"
      }
    }
    println "Inserted ${insertCount}/${weatherDocs.size()} test documents from JSON file"
    importConnector.shutdown()
    System.out.flush()

    // increased timeout to deal with CI under high load
    def connector = new CouchbaseConnector(
        couchbaseContainer.connectionString,
        bucketDefinition.name,
        couchbaseContainer.username,
        couchbaseContainer.password,
        Duration.ofSeconds(20))
    def weatherFactory = new IconTimeBasedWeatherValueFactory()
    source = new CouchbaseWeatherSource(connector, IconWeatherTestData.coordinateSource, coordinateIdColumnName, weatherFactory)
    println "setupSpec completed"
    System.out.flush()
  }

  def "The test container can establish a valid connection"() {
    when:
    def connector = new CouchbaseConnector(couchbaseContainer.connectionString, bucketDefinition.name, couchbaseContainer.username, couchbaseContainer.password)

    then:
    connector.connectionValid
  }

  def "A CouchbaseWeatherSource can read and correctly parse a single value for a specific date and coordinate"() {
    given:
    def expectedTimeBasedValue = new TimeBasedValue(IconWeatherTestData.TIME_15H, IconWeatherTestData.WEATHER_VALUE_67775_15H)

    when:
    def optTimeBasedValue = source.getWeather(IconWeatherTestData.TIME_15H, IconWeatherTestData.COORDINATE_67775)

    then:
    optTimeBasedValue.present
    equalsIgnoreUUID(optTimeBasedValue.get(), expectedTimeBasedValue)
  }

  def "A CouchbaseWeatherSource can read multiple time series values for multiple coordinates"() {
    given:
    def coordinates = [
      IconWeatherTestData.COORDINATE_67775,
      IconWeatherTestData.COORDINATE_67776
    ]
    def timeInterval = new ClosedInterval(IconWeatherTestData.TIME_16H, IconWeatherTestData.TIME_17H)
    def timeSeries67775 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67775_16H),
          new TimeBasedValue(IconWeatherTestData.TIME_17H, IconWeatherTestData.WEATHER_VALUE_67775_17H)
        ]
        as Set<TimeBasedValue>)
    def timeSeries67776 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67776_16H)
        ] as Set<TimeBasedValue>)

    when:
    def coordinateToTimeSeries = source.getWeather(timeInterval, coordinates)

    then:
    coordinateToTimeSeries.keySet().size() == 2
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67775), timeSeries67775)
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67776), timeSeries67776)
  }

  def "A CouchbaseWeatherSource can read all weather data in a given time interval"() {
    given:
    def timeInterval = new ClosedInterval(IconWeatherTestData.TIME_15H, IconWeatherTestData.TIME_17H)
    def timeSeries67775 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_15H, IconWeatherTestData.WEATHER_VALUE_67775_15H),
          new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67775_16H),
          new TimeBasedValue(IconWeatherTestData.TIME_17H, IconWeatherTestData.WEATHER_VALUE_67775_17H)
        ] as Set<TimeBasedValue>)
    def timeSeries67776 = new IndividualTimeSeries(null,
        [
          new TimeBasedValue(IconWeatherTestData.TIME_15H, IconWeatherTestData.WEATHER_VALUE_67776_15H),
          new TimeBasedValue(IconWeatherTestData.TIME_16H, IconWeatherTestData.WEATHER_VALUE_67776_16H)
        ] as Set<TimeBasedValue>)

    when:
    def coordinateToTimeSeries = source.getWeather(timeInterval)

    then:
    coordinateToTimeSeries.keySet().size() == 2
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67775).entries, timeSeries67775.entries)
    equalsIgnoreUUID(coordinateToTimeSeries.get(IconWeatherTestData.COORDINATE_67776).entries, timeSeries67776.entries)
  }

  def "The CouchbaseWeatherSource returns all time keys after a given time key correctly"() {
    given:
    def time = IconWeatherTestData.TIME_15H

    when:
    def actual = source.getTimeKeysAfter(time)

    then:
    actual.size() == 2

    actual.get(IconWeatherTestData.COORDINATE_67775) == [
      IconWeatherTestData.TIME_16H,
      IconWeatherTestData.TIME_17H
    ]
    actual.get(IconWeatherTestData.COORDINATE_67776) == [IconWeatherTestData.TIME_16H]
  }
}
