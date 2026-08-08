/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.markov

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.profile.markov.MarkovModelJsonTestSupport
import tech.units.indriya.quantity.Quantities
import tools.jackson.databind.node.ObjectNode

class MarkovLoadModelFactoryTest extends MarkovModelJsonTestSupport {
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
    def gmmState = model.gmmBuckets().get().buckets().first().states().first()
    gmmState.weights() == [1.0d]
    gmmState.means() == [1.0d]
    gmmState.variances() == [0.2d]
    model.valueModel().normalization().maxPower().isPresent()
    model.valueModel().normalization().maxPower().get() == Quantities.getQuantity(1.5d, StandardUnits.ACTIVE_POWER_IN)
    model.valueModel().normalization().minPower().isPresent()
    model.valueModel().normalization().minPower().get() == Quantities.getQuantity(0.1d, StandardUnits.ACTIVE_POWER_IN)
  }

  def "buildModel throws FactoryException on transition dimension mismatch"() {
    given:
    def invalidJson = objectMapper.readTree(validModelJson().replace("\"shape\": [1,2,2]", "\"shape\": [2,2,2]"))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
  }

  def "buildModel throws FactoryException when transition row does not sum to one"() {
    given:
    def invalidJson = objectMapper.readTree(validModelJson()
        .replace('[0.1, 0.9]', '[0.1, 0.8]'))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
  }

  def "buildModel throws FactoryException when threshold count does not match state count"() {
    given: "states=2 requires exactly 1 threshold, but 2 are provided"
    def invalidJson = objectMapper.readTree(validModelJson()
        .replace('"thresholds_right": [0.5]', '"thresholds_right": [0.3, 0.7]'))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
  }

  def "buildModel throws FactoryException when schema field is missing"() {
    given:
    def invalidJson = objectMapper.readTree(validModelJson()
        .replace('"schema": "markov.load.v1",', ''))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
  }

  def "buildModel throws FactoryException when generated_at timestamp is invalid"() {
    given:
    def invalidJson = objectMapper.readTree(validModelJson()
        .replace('"generated_at": "2025-01-01T00:00:00Z"', '"generated_at": "not-a-timestamp"'))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
  }

  def "buildModel throws FactoryException when bucket_encoding formula is missing"() {
    given:
    def invalidJson = objectMapper.readTree(validModelJson()
        .replace('"bucket_encoding": { "formula": "hour_of_day" }', '"bucket_encoding": {}'))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
  }

  def "buildModel throws FactoryException when gmms buckets array is missing"() {
    given:
    def invalidJson = objectMapper.readTree(validModelJson()
        .replace('"buckets":', '"not_buckets":'))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
  }

  def "buildModel throws FactoryException when GMM component arrays differ in size"() {
    given:
    def invalidJson = objectMapper.readTree(validModelJson()
        .replace('"variances": [0.2]', '"variances": [0.2, 0.3]'))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
  }

  def "buildModel tolerates a missing parameters block"() {
    given:
    def root = objectMapper.readTree(validModelJson())
    ((ObjectNode) root).remove("parameters")

    when:
    def model = factory.get(new MarkovModelData(root)).getOrThrow()

    then:
    model.parameters().transitions().isEmpty()
    model.parameters().gmm().isEmpty()
  }

  def "buildModel throws FactoryException when max_power is missing"() {
    given:
    def invalidJson = objectMapper.readTree(validModelJson()
        .replace('"max_power": { "value": 1.5, "unit": "kW" },', ''))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
  }

  def "buildModel throws FactoryException when normalization range is non-positive"() {
    given: "max_power below min_power"
    def invalidJson = objectMapper.readTree(validModelJson()
        .replace('"max_power": { "value": 1.5, "unit": "kW" }', '"max_power": { "value": 0.05, "unit": "kW" }'))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
  }

  def "buildModel throws FactoryException on unsupported power unit"() {
    given:
    def invalidJson = objectMapper.readTree(validModelJson()
        .replace('"max_power": { "value": 1.5, "unit": "kW" }', '"max_power": { "value": 1500.0, "unit": "W" }'))

    when:
    factory.get(new MarkovModelData(invalidJson)).getOrThrow()

    then:
    thrown(FactoryException)
  }
}
