/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Shared
import spock.lang.Specification

import java.time.ZonedDateTime

class EmInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared private UUID operatorUuid = UUID.randomUUID()

  @Shared private def operatorInput = Mock(OperatorInput)
  @Shared private UUID emUuid = UUID.randomUUID()
  @Shared private def parentEmUnit = Mock(EmInput)

  @Shared private EmInputFactory inputFactory

  def setupSpec() {
    operatorInput.getUuid() >> operatorUuid
    parentEmUnit.getUuid() >> emUuid

    inputFactory = new EmInputFactory(map(operatorInput), map(parentEmUnit))
  }

  def "A EmInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [EmInput] as List

    expect:
    inputFactory.supportedClasses == expectedClasses
  }

  def "An EmInputFactory should return the valid fields correctly"() {
    given:
    def validCombinations = [
      [
        "uuid",
        "id",
        "controlStrategy",
        "controllingEm"
      ],
      [
        "uuid",
        "id",
        "controlStrategy",
        "controllingEm",
        "operatesFrom"
      ],
      [
        "uuid",
        "id",
        "controlStrategy",
        "controllingEm",
        "operatesUntil"
      ],
      [
        "uuid",
        "id",
        "controlStrategy",
        "controllingEm",
        "operatesFrom",
        "operatesUntil"
      ],
      [
        "uuid",
        "id",
        "controlStrategy",
        "controllingEm",
        "operator"
      ],
      [
        "uuid",
        "id",
        "controlStrategy",
        "controllingEm",
        "operator",
        "operatesFrom"
      ],
      [
        "uuid",
        "id",
        "controlStrategy",
        "controllingEm",
        "operator",
        "operatesUntil"
      ],
      [
        "uuid",
        "id",
        "controlStrategy",
        "controllingEm",
        "operator",
        "operatesFrom",
        "operatesUntil"
      ]
    ].collect { it as Set }

    when:
    def fieldCombinations = inputFactory.getFields(EmInput)

    then:
    fieldCombinations == validCombinations
  }

  def "A EmInputFactory should parse a valid EmInput with parent EM correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id" : "TestID",
      "controlStrategy" : "no_control",
      "operator": operatorUuid.toString(),
      "controllingEm": emUuid.toString()
    ]

    when:
    Try<EmInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == EmInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime.startDate.present
      operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesFrom"])
      operationTime.endDate.present
      operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesUntil"])
      operator == operatorInput
      id == parameter["id"]
      controlStrategy == parameter["controlStrategy"]
      controllingEm == Optional.of(parentEmUnit)
    }
  }

  def "A EmInputFactory should parse a valid EmInput without parent EM correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id" : "TestID",
      "controlStrategy" : "no_control",
      "operator": operatorUuid.toString(),
    ]

    when:
    Try<EmInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == EmInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime.startDate.present
      operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesFrom"])
      operationTime.endDate.present
      operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesUntil"])
      operator == operatorInput
      id == parameter["id"]
      controlStrategy == parameter["controlStrategy"]
      controllingEm == Optional.empty()
    }
  }

  def "A EmInputFactory should fail when passing an invalid UUID"() {
    given:
    Map<String, String> parameter = [
      "uuid" : "- broken -",
      "id" : "TestID",
      "controlStrategy" : "no_control",
      "operator": operatorUuid.toString(),
    ]

    when:
    Try<EmInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.failure
    input.exception.get().cause.message == "Exception while trying to parse UUID of field \"uuid\" with value \"- broken -\""
  }
}
