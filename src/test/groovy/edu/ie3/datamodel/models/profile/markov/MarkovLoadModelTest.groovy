/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.profile.markov

import edu.ie3.datamodel.io.factory.markov.MarkovLoadModelFactory
import edu.ie3.datamodel.io.factory.markov.MarkovModelData
import edu.ie3.datamodel.io.source.PowerValueSource
import edu.ie3.datamodel.models.StandardUnits

import java.time.ZonedDateTime

class MarkovLoadModelTest extends MarkovModelJsonTestSupport {

  private final MarkovLoadModelFactory factory = new MarkovLoadModelFactory()

  def "supplier scales deterministic normalized values and exposes next state"() {
    given:
    def model = loadModel(deterministicTransitions(), deterministicStates())
    def input = new PowerValueSource.MarkovIdentifier(
        ZonedDateTime.parse("2025-01-01T00:00:00Z"),
        OptionalInt.of(0),
        OptionalDouble.empty(),
        99L
        )

    when:
    def supplier = model.getValueSupplier(input)
    def output = supplier.get()
    def outputAgain = supplier.get()

    then:
    output.value().isPresent()
    output.nextState() == 1
    output.value().get().p.get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 4.2d
    outputAgain.value() == output.value()
    outputAgain.nextState() == output.nextState()
    model.getMaxPower().isPresent()
    model.getMaxPower().get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 5d
  }

  def "supplier denormalizes using model min and max power"() {
    given:
    def model = loadModel(deterministicTransitions(), deterministicStates())
    def input = new PowerValueSource.MarkovIdentifier(
        ZonedDateTime.parse("2025-01-01T00:00:00Z"),
        OptionalInt.of(0),
        OptionalDouble.empty(),
        17L
        )

    when:
    def supplier = model.getValueSupplier(input)
    def output = supplier.get()

    then:
    output.nextState() == 1
    output.value().get().p.get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 4.2d
  }

  def "supplier uses initial normalized value when no previous state is present"() {
    given:
    def model = loadModel(selfLoopTransitions(), deterministicStates())
    def input = new PowerValueSource.MarkovIdentifier(
        ZonedDateTime.parse("2025-01-01T00:00:00Z"),
        OptionalInt.empty(),
        OptionalDouble.of(0.25d),
        13L
        )

    when:
    def supplier = model.getValueSupplier(input)
    def output = supplier.get()

    then:
    output.nextState() == 0 // discretized from initial normalized value
    output.value().get().p.get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 2.6d
  }

  def "initial normalized value exactly on a threshold maps to the upper state"() {
    given: "trainer semantics: searchsorted(side='right') puts boundary values into the upper bin"
    def model = loadModel(selfLoopTransitions(), deterministicStates())
    def input = new PowerValueSource.MarkovIdentifier(
        ZonedDateTime.parse("2025-01-01T00:00:00Z"),
        OptionalInt.empty(),
        OptionalDouble.of(0.5d),
        13L
        )

    when:
    def output = model.getValueSupplier(input).get()

    then:
    output.nextState() == 1
    output.value().get().p.get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue() == 4.2d
  }

  def "supplier rejects an out-of-bounds previous state"() {
    given:
    def model = loadModel(deterministicTransitions(), deterministicStates())
    def input = new PowerValueSource.MarkovIdentifier(
        ZonedDateTime.parse("2025-01-01T00:00:00Z"),
        OptionalInt.of(5),
        OptionalDouble.empty(),
        1L
        )

    when:
    model.getValueSupplier(input).get()

    then:
    thrown(IllegalArgumentException)
  }

  private loadModel(String transitions, String states) {
    def json = markovModelJson(transitions, states, "5.0", "1.0")
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
}
