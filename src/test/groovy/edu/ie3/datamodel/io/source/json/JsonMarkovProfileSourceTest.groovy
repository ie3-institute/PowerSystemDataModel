/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.json

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.file.FileType
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.naming.timeseries.FileLoadProfileMetaInformation
import edu.ie3.datamodel.io.source.PowerValueSource
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.nio.file.Files
import java.nio.file.Path
import java.time.ZonedDateTime

class JsonMarkovProfileSourceTest extends Specification {

  Path tempDir
  Path jsonFile

  def setup() {
    tempDir = Files.createTempDirectory("markovProfileSource")
    jsonFile = tempDir.resolve("model.json")
  }

  def cleanup() {
    if (tempDir != null) {
      Files.walk(tempDir)
          .sorted(Comparator.reverseOrder())
          .forEach { Files.deleteIfExists(it) }
    }
  }

  def "getModel reads and caches Markov model from JSON file"() {
    given:
    Files.writeString(jsonFile, validModelJson())
    def source = new JsonMarkovProfileSource(
        new JsonDataSource(tempDir, new FileNamingStrategy()),
        new FileLoadProfileMetaInformation("profile1", jsonFile, FileType.JSON)
        )

    when:
    MarkovLoadModel modelFirst = source.getModel()
    MarkovLoadModel modelSecond = source.getModel()

    then:
    modelFirst.is(modelSecond) // cached instance reused
    modelFirst.schema() == "markov.load.v1"
    noExceptionThrown()

    when: "validation is executed on the same file"
    source.validate()

    then:
    noExceptionThrown()
  }

  def "getModel throws SourceException on invalid JSON file"() {
    given:
    Files.writeString(jsonFile, "{}")
    def source = new JsonMarkovProfileSource(
        new JsonDataSource(tempDir, new FileNamingStrategy()),
        new FileLoadProfileMetaInformation("brokenProfile", jsonFile, FileType.JSON)
        )

    when:
    source.getModel()

    then:
    thrown(SourceException)
  }

  def "source exposes Markov-based power value supplier"() {
    given:
    Files.writeString(jsonFile, validModelJson())
    def source = new JsonMarkovProfileSource(
        new JsonDataSource(tempDir, new FileNamingStrategy()),
        new FileLoadProfileMetaInformation("profile1", jsonFile, FileType.JSON)
        )
    def referencePower = Quantities.getQuantity(10d, StandardUnits.ACTIVE_POWER_IN)
    def input = new PowerValueSource.MarkovIdentifier(
        ZonedDateTime.parse("2025-01-01T00:00:00Z"),
        OptionalInt.of(0),
        OptionalDouble.empty(),
        referencePower,
        42L)

    when:
    def supplier = source.getValueSupplier(input)
    def output = supplier.get()

    then:
    source.getProfile().key() == "profile1"
    source.getMaxPower().isPresent()
    source.getMaxPower().get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 10d
    output.value().isPresent()
    output.nextState() == 0
    output.value().get().p.get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 10d
  }

  private static String validModelJson() {
    return """
      {
        "schema": "markov.load.v1",
        "generated_at": "2025-01-01T00:00:00Z",
        "generator": {
          "name": "simonaMarkovLoad",
          "version": "1.0.0",
          "config": { "foo": "bar" }
        },
        "time_model": {
          "bucket_count": 1,
          "bucket_encoding": { "formula": "hour_of_day" },
          "sampling_interval_minutes": 60,
          "timezone": "UTC"
        },
        "value_model": {
          "value_unit": "W",
          "normalization": {
            "method": "none",
            "max_power": { "value": 10.0, "unit": "kW" },
            "min_power": { "value": 0.5, "unit": "kW" }
          },
          "discretization": {
            "states": 2,
            "thresholds_right": [0.5]
          }
        },
        "parameters": {
          "transitions": { "empty_row_strategy": "fill" },
          "gmm": {
            "value_col": "p",
            "verbose": 1,
            "heartbeat_seconds": 5
          }
        },
        "data": {
          "transitions": {
            "dtype": "float64",
            "encoding": "dense",
            "shape": [1,2,2],
            "values": [
              [
                [0.1, 0.9],
                [0.3, 0.7]
              ]
            ]
          },
          "gmms": {
            "buckets": [
              {
                "states": [
                    {
                      "weights": [0.6],
                      "means": [1.0],
                    "variances": [0.0]
                    },
                  null
                ]
              }
            ]
          }
        }
      }
    """.stripIndent()
  }
}
