/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.markov

import com.fasterxml.jackson.databind.ObjectMapper
import edu.ie3.datamodel.exceptions.FactoryException
import spock.lang.Specification

class MarkovLoadModelFactoryTest extends Specification {
  private final ObjectMapper objectMapper = new ObjectMapper()
  private final MarkovLoadModelFactory factory = new MarkovLoadModelFactory()

  def "buildModel returns parsed Markov load model from valid JSON"() {
    given:
    def root = objectMapper.readTree(validModelJson())

    when:
    def model = factory.get(new MarkovModelData(root)).getOrThrow()

    then:
    model.schema() == "markov.load.v1"
    model.generator().name() == "simonaMarkovLoad"
    model.timeModel().bucketCount() == 1
    model.valueModel().discretization().states() == 2
    model.transitionData().bucketCount() == 1
    model.transitionData().stateCount() == 2
    model.transitionData().values()[0][0][1] == 0.9d
    model.gmmBuckets().isPresent()
    def gmmState = model.gmmBuckets().get().buckets().first().states().first().get()
    gmmState.weights() == [0.6d]
    gmmState.means() == [1.0d]
    gmmState.variances() == [0.2d]
    model.valueModel().normalization().referencePower().isPresent()
    with(model.valueModel().normalization().referencePower().get()) {
      value() == 1.5d
      unit() == "kW"
    }
  }

  def "buildModel throws FactoryException on transition dimension mismatch"() {
    given:
    def invalidJson = objectMapper.readTree(validModelJson().replace("\"shape\": [1,2,2]", "\"shape\": [2,2,2]"))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
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
            "reference_power": { "value": 1.5, "unit": "kW" }
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
                    "variances": [0.2]
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
