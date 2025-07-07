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
import spock.lang.Specification

import java.time.ZonedDateTime
import java.util.stream.Collectors

class EmInputFactoryTest extends Specification {

  def "A EmInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new EmInputFactory()
    def expectedClasses = [EmInput] as List

    expect:
    inputFactory.supportedClasses == expectedClasses
  }

  def "An EmInputFactory should return the valid fields correctly"() {
    given:
    def inputFactory = new EmInputFactory()

    def requiredFields = [
      "uuid",
      "id",
      "controlStrategy",
      "controllingEm"
    ] as Set

    def operationCombinations = [
      ["operatesFrom"],
      ["operatesUntil"],
      [
        "operatesFrom",
        "operatesUntil"
      ],
      ["operator"],
      ["operatesFrom", "operator"],
      ["operatesUntil", "operator"],
      [
        "operatesFrom",
        "operatesUntil",
        "operator"
      ]
    ]

    def validCombinations = [
      requiredFields,
      requiredFields + operationCombinations.get(0),
      requiredFields + operationCombinations.get(1),
      requiredFields + operationCombinations.get(2),
      requiredFields + operationCombinations.get(3),
      requiredFields + operationCombinations.get(4),
      requiredFields + operationCombinations.get(5),
      requiredFields + operationCombinations.get(6),
    ]

    when:
    def fieldCombinations = inputFactory.getFields(EmInput)

    then:
    fieldCombinations == validCombinations
  }

  def "A EmInputFactory should parse a valid EmInput with parent EM correctly"() {
    given:
    def inputFactory = new EmInputFactory()
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "controlstrategy" : "no_control"
    ]
    def inputClass = EmInput
    def operatorInput = Mock(OperatorInput)
    def parentEmUnit = Mock(EmInput)

    when:
    Try<EmInput, FactoryException> input = inputFactory.get(
        new EmAssetInputEntityData(parameter, inputClass, operatorInput, parentEmUnit))

    then:
    input.success
    input.data.get().getClass() == inputClass
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert controlStrategy == parameter["controlstrategy"]
      assert controllingEm == Optional.of(parentEmUnit)
    }
  }

  def "A EmInputFactory should parse a valid EmInput without parent EM correctly"() {
    given:
    def inputFactory = new EmInputFactory()
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "controlstrategy" : "no_control"
    ]
    def inputClass = EmInput
    def operatorInput = Mock(OperatorInput)

    when:
    Try<EmInput, FactoryException> input = inputFactory.get(
        new EmAssetInputEntityData(parameter, inputClass, operatorInput, null))

    then:
    input.success
    input.data.get().getClass() == inputClass
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert controlStrategy == parameter["controlstrategy"]
      assert controllingEm == Optional.empty()
    }
  }

  def "A EmInputFactory should fail when passing an invalid UUID"() {
    given:
    def inputFactory = new EmInputFactory()
    Map<String, String> parameter = [
      "uuid"            : "- broken -",
      "id"              : "TestID",
      "controlstrategy" : "no_control"
    ]
    def inputClass = EmInput
    def operatorInput = Mock(OperatorInput)

    when:
    Try<EmInput, FactoryException> input = inputFactory.get(
        new EmAssetInputEntityData(parameter, inputClass, operatorInput, null))

    then:
    input.failure
    input.exception.get().cause.message == "Exception while trying to parse UUID of field \"uuid\" with value \"- broken -\""
  }
}
