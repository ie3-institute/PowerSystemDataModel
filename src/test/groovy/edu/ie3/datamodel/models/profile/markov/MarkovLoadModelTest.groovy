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

  def "energy scaling of a chain with constant power is the energy of one year"() {
    given: "a chain that always moves to the state with normalized value 0.8, i.e. 4.2 kW"
    // the models of these tests range from 1 kW to 5 kW: 1 + 0.8 * (5 - 1) = 4.2 kW
    def model = loadModel(deterministicTransitions(), deterministicStates())

    when:
    def energyScaling = model.getProfileEnergyScaling()

    then: "the year 2019 has 8760 hours"
    energyScaling.isPresent()
    Math.abs(energyScaling.get().to(StandardUnits.ENERGY_IN).value.doubleValue() - 4.2d * 8760) < 1e-6
  }

  def "energy scaling weights the states with the stationary distribution of the chain"() {
    given: "a chain with stationary distribution [0.25, 0.75]"
    // state 0 is left with probability 0.9 and state 1 with 0.3, which is balanced for 0.25 * 0.9 = 0.75 * 0.3
    def model = loadModel(defaultTransitionValues(), states)

    when:
    def energyScaling = model.getProfileEnergyScaling().get()

    then:
    Math.abs(energyScaling.to(StandardUnits.ENERGY_IN).value.doubleValue() - averagePower * 8760) < 1e-6

    where:
    states || averagePower
    deterministicStates() || 3.8d // 0.25 * 0.4 + 0.75 * 0.8 = 0.7, 1 + 0.7 * 4 = 3.8 kW
    stateWithoutGmm() || 1.4d // 0.25 * 0.4 + 0.75 * 0 = 0.1, 1 + 0.1 * 4 = 1.4 kW
  }

  def "energy scaling uses the transitions and GMMs of the same bucket, as when sampling"() {
    given: "two buckets alternating every quarter-hour, moving to state 0 in bucket 0 and to state 1 in bucket 1"
    def template = loadModel(deterministicTransitions(), deterministicStates())
    def transitions = [
      [[1d, 0d], [1d, 0d]],
      [[0d, 1d], [0d, 1d]]
    ] as double[][][]
    // the GMMs with 0.6 and 0.3 are never reached and would only be used with a wrong bucket or state
    def gmms = new MarkovLoadModel.GmmBuckets([
      new MarkovLoadModel.GmmBuckets.GmmBucket([gmm(0.2d), gmm(0.6d)]),
      new MarkovLoadModel.GmmBuckets.GmmBucket([gmm(0.3d), gmm(0.9d)])
    ])
    def model = new MarkovLoadModel(
        template.schema(),
        template.generatedAt(),
        template.generator(),
        new MarkovLoadModel.TimeModel(2, "quarter_hour", 15, "UTC"),
        template.valueModel(),
        template.parameters(),
        new MarkovLoadModel.TransitionData("float32", "nested_lists", transitions),
        Optional.of(gmms)
    )

    when:
    def energyScaling = model.getProfileEnergyScaling().get()

    then: "the normalized values 0.2 and 0.9 alternate, i.e. 3.2 kW on average"
    // (0.2 + 0.9) / 2 = 0.55, 1 + 0.55 * 4 = 3.2 kW
    Math.abs(energyScaling.to(StandardUnits.ENERGY_IN).value.doubleValue() - 3.2d * 8760) < 1e-6
  }

  def "energy scaling is not affected by transition rows that sum up to one only within the tolerance"() {
    given: "a chain with stationary distribution [0.25, 0.75] and rows summing up to 0.999995"
    def transitions = """
      [
        [
          [0.1, 0.899995],
          [0.3, 0.699995]
        ]
      ]
    """.stripIndent()
    def model = loadModel(transitions, deterministicStates())

    when:
    def energyScaling = model.getProfileEnergyScaling().get()

    then: "the same 3.8 kW on average as with exact transition rows"
    Math.abs(energyScaling.to(StandardUnits.ENERGY_IN).value.doubleValue() / (3.8d * 8760) - 1d) < 1e-4
  }

  def "energy scaling matches the mean energy of sampled years"() {
    given: "GMMs with samples that are often clamped to [0, 1]"
    def model = loadModel(defaultTransitionValues(), clampedStates())
    def seeds = 1L..4L

    when: "sampling in hourly steps"
    def sampledEnergy = seeds.sum { seed ->
      sampleYear(model, seed)
    } / seeds.size()
    def energyScaling = model.getProfileEnergyScaling().get().to(StandardUnits.ENERGY_IN).value.doubleValue()

    then:
    Math.abs(sampledEnergy - energyScaling) / energyScaling < 0.01
  }

  private static double sampleYear(MarkovLoadModel model, long seed) {
    def start = ZonedDateTime.parse("2019-01-01T00:00:00Z")
    def energy = 0d
    def previousState = OptionalInt.empty()
    for (def time = start; time.isBefore(start.plusYears(1)); time = time.plusHours(1)) {
      def initialValue = previousState.isPresent() ? OptionalDouble.empty() : OptionalDouble.of(0d)
      def output = model.getValueSupplier(
          new PowerValueSource.MarkovIdentifier(time, previousState, initialValue, seed)).get()
      energy += output.value().get().p.get().to(StandardUnits.ACTIVE_POWER_IN).value.doubleValue()
      previousState = OptionalInt.of(output.nextState())
    }
    return energy
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

  private static String stateWithoutGmm() {
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

  private static MarkovLoadModel.GmmBuckets.GmmState gmm(double mean) {
    return new MarkovLoadModel.GmmBuckets.GmmState([1d], [mean], [0d])
  }

  private static String clampedStates() {
    return """
      [
        {
          "weights": [0.5, 0.5],
          "means": [0.05, 0.3],
          "variances": [0.01, 0.02]
        },
        {
          "weights": [1.0],
          "means": [0.9],
          "variances": [0.05]
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
