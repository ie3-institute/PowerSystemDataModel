/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.couchbase

import edu.ie3.datamodel.io.connectors.CouchbaseConnector
import edu.ie3.test.helper.TestContainerHelper
import edu.ie3.test.helper.WeatherSourceTestHelper
import groovy.json.JsonSlurper
import org.testcontainers.couchbase.BucketDefinition
import org.testcontainers.couchbase.CouchbaseContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

import java.time.Duration

@Testcontainers
abstract class AbstractCouchbaseWeatherSourceIT extends Specification implements TestContainerHelper, WeatherSourceTestHelper {

  @Shared
  BucketDefinition bucketDefinition = new BucketDefinition("ie3_in")

  @Shared
  CouchbaseContainer couchbaseContainer = new CouchbaseContainer("couchbase/server:8.0.0")
  .withBucket(bucketDefinition)
  .withExposedPorts(8091, 8092, 8093, 8094, 11210)
  .withStartupAttempts(3)

  @Shared
  CouchbaseWeatherSource source

  static String coordinateIdColumnName = "coordinateid"

  abstract String getJsonResourcePath()
  abstract Object getWeatherFactory()
  abstract Object getCoordinateSource()


  def setupSpec() {
    // create an index for the document keys
    couchbaseContainer.execInContainer("cbq",
        "-e", "http://localhost:8093",
        "-u", couchbaseContainer.username,
        "-p", couchbaseContainer.password,
        "-s", "CREATE index id_idx ON `" + bucketDefinition.name + "` (META().id);")

    // Create connector to import the data from json document
    def connector = new CouchbaseConnector(
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
        connector.getSourceFields()
        ready = true
        println "Couchbase bucket ready"
        break
      } catch (Exception ignored) {
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
    def jsonFile = new File(getJsonResourcePath())
    def jsonSlurper = new JsonSlurper()
    def weatherDocs = jsonSlurper.parse(jsonFile) as List
    def insertCount = 0
    weatherDocs.each { doc ->
      try {
        def coordinateId = doc[coordinateIdColumnName]
        def time = doc["time"]
        def key = "weather::${coordinateId}::${time}"
        connector.persist(key, doc).join()
        insertCount++
      } catch (Exception ex) {
        println "WARNING: Failed to insert document for coordinateid ${doc[coordinateIdColumnName]} and time ${doc["time"]}: ${ex.message}"
      }
    }
    println "Inserted ${insertCount}/${weatherDocs.size()} test documents from JSON file"

    source = new CouchbaseWeatherSource(connector, getCoordinateSource(), coordinateIdColumnName, getWeatherFactory(), getDtfPattern())
    println "setupSpec completed"
    System.out.flush()
  }

  def "The test container can establish a valid connection"() {
    when:
    def connector = new CouchbaseConnector(couchbaseContainer.connectionString, bucketDefinition.name, couchbaseContainer.username, couchbaseContainer.password)

    then:
    connector.connectionValid
  }
}
