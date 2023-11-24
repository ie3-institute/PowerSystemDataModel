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
  private DummyFactory factory = new DummyFactory(String)


  def "A Factory can return additional fields correctly"() {
    when:
    def additional = factory.getAdditionalFields(actualFields as Set<String>, validFieldSets)

    then:
    additional == expected as Set<String>

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
  }

  def "A Factory should allow additional fields"() {
    given:
    def foundFields = factory.newSet("uuid", "id", "time", "value_1", "value_2", "value_3")

    when:
    Try<Void, FactoryException> input = Try.ofVoid(() -> factory.validate(foundFields, String), FactoryException)

    then:
    input.success
  }


  private class DummyFactory extends Factory<String, SimpleFactoryData, String> {

    protected DummyFactory(Class<? extends String>... supportedClasses) {
      super(supportedClasses)
    }

    @Override
    protected String buildModel(SimpleFactoryData data) {
      return null
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
