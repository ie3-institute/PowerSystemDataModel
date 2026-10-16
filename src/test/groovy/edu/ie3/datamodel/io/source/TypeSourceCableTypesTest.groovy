/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.models.input.connector.type.CableTypeInput
import spock.lang.Specification

class TypeSourceCableTypesTest extends Specification {

  def "getStandardCableTypes returns a non-empty map of built-in cable types"() {
    when:
    def result = TypeSource.getStandardCableTypes()

    then:
    result != null
    !result.isEmpty()
    result.values().every { it instanceof CableTypeInput }
  }

  def "getStandardCableTypes entries have valid UUIDs as keys"() {
    when:
    def result = TypeSource.getStandardCableTypes()

    then:
    result.keySet().every { it == it }
    result.each { uuid, cableType ->
      cableType.uuid == uuid
    }
  }

  def "getCableTypes(false) throws SourceException when source data is invalid"() {
    given:
    def source = DummyDataSource.of(["uuid": "not-a-uuid", "id": "bad"])
    def typeSource = new TypeSource(source)

    when:
    typeSource.getCableTypes(false)

    then:
    thrown(SourceException)
  }
}