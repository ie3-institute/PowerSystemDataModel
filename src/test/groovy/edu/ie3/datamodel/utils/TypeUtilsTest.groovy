/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import static edu.ie3.test.common.TypeTestData.*
import static edu.ie3.test.helper.QuantityHelper.*

import spock.lang.Specification

class TypeUtilsTest extends Specification {

  def "The TypeUtils should find a suitable line type"() {
    given:
    def types = TypeUtils.findSuitableLineTypes(lineTypes(), potential(10d))

    when:
    def typeOption = TypeUtils.findSuitableLineType(types, current(value))

    then:
    typeOption.isPresent() == present

    where:
    value || present
    300d  || true
    500d  || true
    501d  || false
  }

  def "The TypeUtils should find a suitable transformer2w type"() {
    given:
    def types = TypeUtils.findSuitableTransformerTypes(transformer2WTypes(), potential(380d), potential(110d))

    when:
    def typeOption = TypeUtils.findSuitableTransformerType(types, power(value))

    then:
    typeOption.isPresent() == present

    where:
    value   || present
    20000d  || true
    40000d  || true
    40001d  || false
  }

  def "The TypeUtils should find a suitable transformer3w type"() {
    given:
    def types = TypeUtils.findSuitableTransformerTypes(transformer3WTypes(), potential(380d), potential(20d), potential(10d))

    when:
    def typeOption = TypeUtils.findSuitableTransformerType(types, power(valueA), power(valueB), power(valueC))

    then:
    typeOption.isPresent() == present

    where:
    valueA || valueB || valueC || present
    50000d || 30000d || 10000d  || true
    60000d || 40000d || 20000d  || true
    60001d || 40000d || 20000d  || false
    60000d || 40001d || 20000d  || false
    60000d || 40000d || 20001d  || false
  }

  def "The TypeUtils should find all suitable line types for a given voltage rating"() {
    when:
    def types = TypeUtils.findSuitableLineTypes(lineTypes(), vRated)

    then:
    types == expected

    where:
    vRated          || expected
    potential(380d) || [lineType380kV_1300]
    potential(220d) || []
    potential(30d)  || []
    potential(20d)  || [lineType20kV_400]
    potential(10d)  || [
      lineType10kV_300,
      lineType10kV_500
    ]
    potential(0.4d) || [
      lineType400V_170,
      lineType400V_120
    ]
  }

  def "The TypeUtils should find all suitable transformer2w types for a given voltage rating"() {
    when:
    def types = TypeUtils.findSuitableTransformerTypes(transformer2WTypes(), vRatedA, vRatedB)

    then:
    types == expected

    where:
    vRatedA         || vRatedB         || expected
    potential(380d) || potential(220d) || []
    potential(380d) || potential(110d) || [
      transformerTypeEHV_HV_40,
      transformerTypeEHV_HV_30
    ]
    potential(110d) || potential(20d)  || [transformerTypeHV_20kV_40]
    potential(110d) || potential(10d)  || [
      transformerTypeHV_10kV_20,
      transformerTypeHV_10kV_10
    ]
  }

  def "The TypeUtils should find all suitable transformer3w types for a given voltage rating"() {
    when:
    def types = TypeUtils.findSuitableTransformerTypes(transformer3WTypes(), vRatedA, vRatedB, vRatedC)

    then:
    types == expected

    where:
    vRatedA         || vRatedB         || vRatedC        || expected
    potential(380d) || potential(220d) || potential(30d) || []
    potential(380d) || potential(110)  || potential(20d) || [transformerTypeEHV_HV_20kV]
    potential(380d) || potential(20)   || potential(10d) || [transformerTypeEHV_20kV_10kV]
    potential(110d) || potential(30d)  || potential(10d) || []
  }

  def "The TypeUtils should find a suitable type"() {
    // just to test every method
    // same as TypeUtils.findSuitableLineTypes(lineTypes(), potential(10d))

    given:
    def types = TypeUtils.findSuitableLineTypes(lineTypes(), potential(10d))

    when:
    def typeOption = TypeUtils.findSuitableType(types, current(value), type -> type.getiMax())

    then:
    typeOption.isPresent() == present

    where:
    value || present
    300d  || true
    500d  || true
    501d  || false
  }
}
