/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.markov

import com.fasterxml.jackson.databind.ObjectMapper
import edu.ie3.datamodel.io.factory.markov.MarkovLoadModelFactory
import edu.ie3.datamodel.io.factory.markov.MarkovModelData
import edu.ie3.datamodel.io.source.PowerValueSource
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.profile.markov.MarkovPowerProfile
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime
import java.util.OptionalDouble
import java.util.OptionalInt

class MarkovLoadValueSourceTest extends Specification {

  private final ObjectMapper objectMapper = new ObjectMapper()
  private final MarkovLoadModelFactory factory = new MarkovLoadModelFactory()
  private final MarkovPowerProfile profile = new MarkovPowerProfile("profile1")

  def "supplier scales deterministic normalized values and exposes next state"() {
    given:
    def model = loadModel(deterministicTransitions(), deterministicStates())
    def source = new MarkovLoadValueSource(profile, model)
    def reference = Quantities.getQuantity(5d, StandardUnits.ACTIVE_POWER_IN)
    def input = new PowerValueSource.MarkovInputValue(
        ZonedDateTime.parse("2025-01-01T00:00:00Z"),
        OptionalInt.of(0),
        OptionalDouble.empty(),
        reference,
        99L
        )

    when:
    def supplier = source.getValueSupplier(input)
    def value = supplier.get()

    then:
    value.isPresent()
    supplier.getNextState() == 1
    value.get().p.get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 4.2d
    supplier.get().is(value) // cached result reused
    source.getMaxPower().isPresent()
    source.getMaxPower().get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 5d
  }

  def "supplier denormalizes using model min and max power"() {
    given:
    def model = loadModel(deterministicTransitions(), deterministicStates())
    def source = new MarkovLoadValueSource(profile, model)
    def reference = Quantities.getQuantity(5d, StandardUnits.ACTIVE_POWER_IN)
    def input = new PowerValueSource.MarkovInputValue(
        ZonedDateTime.parse("2025-01-01T00:00:00Z"),
        OptionalInt.of(0),
        OptionalDouble.empty(),
        reference,
        17L
        )

    when:
    def supplier = source.getValueSupplier(input)
    def value = supplier.get()

    then:
    supplier.getNextState() == 1
    value.get().p.get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 4.2d
  }

  def "supplier uses initial normalized value when no previous state is present"() {
    given:
    def model = loadModel(selfLoopTransitions(), deterministicStates())
    def source = new MarkovLoadValueSource(profile, model)
    def reference = Quantities.getQuantity(5d, StandardUnits.ACTIVE_POWER_IN)
    def input = new PowerValueSource.MarkovInputValue(
        ZonedDateTime.parse("2025-01-01T00:00:00Z"),
        OptionalInt.empty(),
        OptionalDouble.of(0.25d),
        reference,
        13L
        )

    when:
    def supplier = source.getValueSupplier(input)

    then:
    supplier.getNextState() == 0 // discretized from initial normalized value
    supplier.get().get().p.get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 2.6d
  }

  def "supplier returns zero power when transitions row has no usable probabilities"() {
    given:
    def model = loadModel(emptyTransitions(), missingStateGmms())
    def source = new MarkovLoadValueSource(profile, model)
    def reference = Quantities.getQuantity(3d, StandardUnits.ACTIVE_POWER_IN)
    def input = new PowerValueSource.MarkovInputValue(
        ZonedDateTime.parse("2025-01-01T00:00:00Z"),
        OptionalInt.of(0),
        OptionalDouble.empty(),
        reference,
        7L
        )

    when:
    def supplier = source.getValueSupplier(input)
    def value = supplier.get()

    then:
    supplier.getNextState() == 0 // stays in current state
    value.isPresent()
    value.get().p.get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 1d
  }

  private loadModel(String transitions, String states) {
    def json = modelJson(transitions, states)
    def root = objectMapper.readTree(json)
    factory.get(new MarkovModelData(root)).getOrThrow()
  }

  private static String deterministicTransitions() {
    return """
      [
        [
          [0.0, 1.0],
          [0.0, 1.0]
        ]
      ]
    """.stripIndent()
  }

  private static String deterministicStates() {
    return """
      [
        {
          "weights": [1.0],
          "means": [0.4],
          "variances": [0.0]
        },
        {
          "weights": [1.0],
          "means": [0.8],
          "variances": [0.0]
        }
      ]
    """.stripIndent()
  }

  private static String selfLoopTransitions() {
    return """
      [
        [
          [1.0, 0.0],
          [0.0, 1.0]
        ]
      ]
    """.stripIndent()
  }

  private static String emptyTransitions() {
    return """
      [
        [
          [0.0, 0.0],
          [0.0, 0.0]
        ]
      ]
    """.stripIndent()
  }

  private static String missingStateGmms() {
    return """
      [
        {
          "weights": [1.0],
          "means": [0.4],
          "variances": [0.0]
        },
        null
      ]
    """.stripIndent()
  }

  private static String modelJson(String transitions, String states) {
    def normalization = """
        {
          "method": "none",
          "max_power": { "value": 5.0, "unit": "kW" },
          "min_power": { "value": 1.0, "unit": "kW" }
        }
        """
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
          "normalization": $normalization,
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
}
