/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.profile.markov

import spock.lang.Specification
import tools.jackson.databind.ObjectMapper

abstract class MarkovModelJsonTestSupport extends Specification {

  protected final ObjectMapper objectMapper = new ObjectMapper()

  protected static String validModelJson() {
    return markovModelJson(defaultTransitionValues(), defaultGmmStates())
  }

  protected static String markovModelJson(
      String transitions,
      String states,
      String maxPower = "1.5",
      String minPower = "0.1"
  ) {
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
          "value_unit": "normalized",
          "normalization": {
            "method": "none",
            "max_power": { "value": $maxPower, "unit": "kW" },
            "min_power": { "value": $minPower, "unit": "kW" }
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
            "dtype": "float32",
            "encoding": "nested_lists",
            "shape": [1,2,2],
            "values": $transitions
          },
          "gmms": {
            "buckets": [
              {
                "states": $states
              }
            ]
          }
        }
      }
    """.stripIndent()
  }

  protected static String defaultTransitionValues() {
    return """
      [
        [
          [0.1, 0.9],
          [0.3, 0.7]
        ]
      ]
    """.stripIndent()
  }

  protected static String defaultGmmStates() {
    return """
      [
        {
          "weights": [1.0],
          "means": [1.0],
          "variances": [0.2]
        },
        null
      ]
    """.stripIndent()
  }
}
