/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.EvInput
import edu.ie3.test.common.SystemParticipantTestData as sptd
import spock.lang.Specification

class EvInputEntityDataTest extends Specification {

  def "An EvInputEntityData should be constructable without operator"() {
    given:
    def fields = ["uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7", "id": "test"]
    def data = new EvInputEntityData(fields, EvInput, sptd.evTypeInput)

    expect:
    data.typeInput == sptd.evTypeInput
    data.operatorInput == OperatorInput.NO_OPERATOR_ASSIGNED
    data.fieldsToValues == fields
    data.targetClass == EvInput
  }

  def "An EvInputEntityData should be constructable with operator"() {
    given:
    def fields = ["uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7", "id": "test"]
    def operator = new OperatorInput(UUID.randomUUID(), "TestOperator")
    def data = new EvInputEntityData(fields, EvInput, operator, sptd.evTypeInput)

    expect:
    data.typeInput == sptd.evTypeInput
    data.operatorInput == operator
  }

  def "An EvInputEntityData should have correct equals and hashCode"() {
    given:
    def fields = ["uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7", "id": "test"]
    def data1 = new EvInputEntityData(fields, EvInput, sptd.evTypeInput)
    def data2 = new EvInputEntityData(fields, EvInput, sptd.evTypeInput)
    def differentType = new EvInputEntityData(fields, EvInput, sptd.evTypeInput.copy().id("other").build())
    def differentFields = new EvInputEntityData(["uuid": "different"], EvInput, sptd.evTypeInput)

    expect:
    data1 == data1
    data1 == data2
    data1.hashCode() == data2.hashCode()
    data1 != null
    data1 != "string"
    data1 != differentType
    data1 != differentFields
  }

  def "An EvInputEntityData toString should work as expected"() {
    given:
    def fields = ["uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7", "id": "test"]
    def data = new EvInputEntityData(fields, EvInput, sptd.evTypeInput)

    expect:
    data.toString().contains("EvInputEntityData{")
    data.toString().contains(sptd.evTypeInput.uuid.toString())
    data.toString().contains("operatorInput=")
    data.toString().contains("fieldsToValues=")
    data.toString().contains("targetClass=")
  }
}
