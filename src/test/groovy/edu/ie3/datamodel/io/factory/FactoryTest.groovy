/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.utils.Try
import spock.lang.Shared
import spock.lang.Specification

class FactoryTest extends Specification {
  @Shared
  private final DummyFactory factory = new DummyFactory(String)


  def "A Factory can return unused fields correctly"() {
    when:
    def unused = factory.getUnusedFields(actualFields as Set<String>, validFieldSets)

    then:
    unused == expected as Set<String>

    where:
    actualFields | validFieldSets | expected
    [
      "uuid",
      "id",
      "time",
      "value_1"
    ] | [
      ["uuid", "value_1"] as Set<String>,
      ["id", "time", "value_1"] as Set<String>
    ] | ["uuid"]
    [
      "uuid",
      "id",
      "time",
      "value_1",
      "value_2",
      "value_3"
    ] | [
      ["uuid", "value_1", "value_3"] as Set<String>,
      ["id", "time", "value_1"] as Set<String>
    ] | ["id", "time", "value_2"]
    [
      "uuid",
      "id",
      "time",
      "value_1"
    ] | [
      [
        "uuid",
        "id",
        "time",
        "value_1",
        "value_2"
      ] as Set<String>,
      [
        "uuid",
        "id",
        "time",
        "value_1",
        "value_3"
      ] as Set<String>
    ] | []
  }

  def "A Factory should allow additional fields"() {
    given:
    def actualFields = DummyFactory.newSet("uuid", "id", "time", "value_1", "value_2", "value_3")

    when:
    def result = Try.ofVoid(() -> factory.validate(actualFields, String), FactoryException)

    then:
    result.success
  }


  private class DummyFactory extends Factory<String, SimpleFactoryData, String> {

    protected DummyFactory(Class<? extends String>... supportedClasses) {
      super(supportedClasses)
    }

    @Override
    protected String buildModel(SimpleFactoryData data) {
      return ""
    }

    @Override
    protected List<Set<String>> getFields(Class<?> entityClass) {
      return [
        ["uuid", "value1", "value3"] as Set<String>,
        ["id", "time", "value1"] as Set<String>
      ]
    }
  }
}
