/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.SourceValidator
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class MeasurementUnitInputTest extends Specification {

  def "A MeasurementUnitInput should return valid fields correctly"() {
    expect:
    MeasurementUnitInput.getFields().fields() == [
      [
        "id",
        "node",
        "p",
        "q",
        "uuid",
        "vAng",
        "vMag"
      ] as Set,
      [
        "id",
        "node",
        "operator",
        "p",
        "q",
        "uuid",
        "vAng",
        "vMag"
      ] as Set,
      [
        "id",
        "node",
        "operatesFrom",
        "p",
        "q",
        "uuid",
        "vAng",
        "vMag"
      ] as Set,
      [
        "id",
        "node",
        "operatesFrom",
        "operator",
        "p",
        "q",
        "uuid",
        "vAng",
        "vMag"
      ] as Set,
      [
        "id",
        "node",
        "operatesUntil",
        "p",
        "q",
        "uuid",
        "vAng",
        "vMag"
      ] as Set,
      [
        "id",
        "node",
        "operatesUntil",
        "operator",
        "p",
        "q",
        "uuid",
        "vAng",
        "vMag"
      ] as Set,
      [
        "id",
        "node",
        "operatesFrom",
        "operatesUntil",
        "p",
        "q",
        "uuid",
        "vAng",
        "vMag"
      ] as Set,
      [
        "id",
        "node",
        "operatesFrom",
        "operatesUntil",
        "operator",
        "p",
        "q",
        "uuid",
        "vAng",
        "vMag"
      ] as Set,
    ]
  }

  def "A MeasurementUnitInput should throw an exception on incorrect fields correctly"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, MeasurementUnitInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'MeasurementUnitInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'MeasurementUnitInput' are possible (NOT case-sensitive!):\n" +
        "0: [id, node, p, q, uuid, vAng, vMag] or [id, node, p, q, uuid, v_ang, v_mag]\n" +
        "1: [id, node, operator, p, q, uuid, vAng, vMag] or [id, node, operator, p, q, uuid, v_ang, v_mag]\n" +
        "2: [id, node, operatesFrom, p, q, uuid, vAng, vMag] or [id, node, operates_from, p, q, uuid, v_ang, v_mag]\n" +
        "3: [id, node, operatesFrom, operator, p, q, uuid, vAng, vMag] or [id, node, operates_from, operator, p, q, uuid, v_ang, v_mag]\n" +
        "4: [id, node, operatesUntil, p, q, uuid, vAng, vMag] or [id, node, operates_until, p, q, uuid, v_ang, v_mag]\n" +
        "5: [id, node, operatesUntil, operator, p, q, uuid, vAng, vMag] or [id, node, operates_until, operator, p, q, uuid, v_ang, v_mag]\n" +
        "6: [id, node, operatesFrom, operatesUntil, p, q, uuid, vAng, vMag] or [id, node, operates_from, operates_until, p, q, uuid, v_ang, v_mag]\n" +
        "7: [id, node, operatesFrom, operatesUntil, operator, p, q, uuid, vAng, vMag] or [id, node, operates_from, operates_until, operator, p, q, uuid, v_ang, v_mag]\n"
  }

  def "A MeasurementUnitInput copy method should work as expected"() {
    given:
    def unit = GridTestData.measurementUnitInput

    when:
    def alteredUnit = unit.copy().node(GridTestData.nodeB).vMag(false).vAng(false).p(false).q(false).build()

    then:
    alteredUnit.with {
      assert uuid == unit.uuid
      assert operationTime == unit.operationTime
      assert operator == unit.operator
      assert id == unit.id
      assert node == GridTestData.nodeB
      assert !getVMag()
      assert !getVAng()
      assert !getP()
      assert !getQ()
    }
  }
}
