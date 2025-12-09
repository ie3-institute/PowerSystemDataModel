/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.io.source.SourceValidator
import edu.ie3.datamodel.io.source.TypeSource
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class OperatorInputTest extends Specification {

  def "An OperatorInput should return possible fields correctly"() {
    when:
    List<Set<String>> fields = OperatorInput.getFields().fields()

    then:
    fields == [["uuid", "id"] as Set]
  }

  def "An OperatorInput should throw an exception on incorrect fields correctly"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, OperatorInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'OperatorInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'OperatorInput' are possible (NOT case-sensitive!):\n" +
        "0: [id, uuid] or [id, uuid]\n"
  }

  def "An OperatorInput can be build correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"  : "TestOperatorId",
    ]

    when:
    def input = TypeSource.operatorBuildFunction.apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
    }
  }

  def "An OperatorInput copy method should work as expected"() {
    given:
    def operator = GridTestData.profBroccoli

    when:
    def alteredUuid = UUID.randomUUID()
    def alteredUnit = operator.copy().uuid(alteredUuid).id("Univ.-Prof. Dr.-Ing. Christian Rehtanz").build()

    then:
    alteredUnit.with {
      assert uuid == alteredUuid
      assert id == "Univ.-Prof. Dr.-Ing. Christian Rehtanz"
    }
  }
}
