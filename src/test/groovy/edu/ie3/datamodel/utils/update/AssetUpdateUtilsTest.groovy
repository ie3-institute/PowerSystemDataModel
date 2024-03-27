/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.update

import static edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.HV
import static edu.ie3.test.helper.QuantityHelper.*

import edu.ie3.datamodel.exceptions.MissingTypeException
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.TypeTestData
import spock.lang.Specification

class AssetUpdateUtilsTest extends Specification {


  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for updating nodes

  def "The AssetUpdateUtils should update node voltage level correctly"() {
    given:
    def node1 = GridTestData.nodeF
    def node2 = GridTestData.nodeG

    when:
    def updatedNodes = AssetUpdateUtils.updateNodes([node1, node2] as Set, HV)

    then:
    updatedNodes.size() == 2
    updatedNodes.keySet() == [node1, node2] as Set

    updatedNodes.get(node1).with {
      assert it.uuid == node1.uuid
      assert it.id == node1.id
      assert it.operator == node1.operator
      assert it.operationTime == node1.operationTime
      assert it.vTarget == node1.vTarget
      assert it.slack == node1.slack
      assert it.geoPosition == node1.geoPosition
      assert it.voltLvl == HV
      assert it.subnet == node1.subnet
    }

    updatedNodes.get(node2).with {
      assert it.uuid == node2.uuid
      assert it.id == node2.id
      assert it.operator == node2.operator
      assert it.operationTime == node2.operationTime
      assert it.vTarget == node2.vTarget
      assert it.slack == node2.slack
      assert it.geoPosition == node2.geoPosition
      assert it.voltLvl == HV
      assert it.subnet == node2.subnet
    }
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for updating connectors

  def "The AssetUpdateUtils should update line current correctly"() {
    given:
    def line = GridTestData.lineFtoG
    def types = TypeTestData.lineTypes()

    when:
    def updatedLine = AssetUpdateUtils.updateLineCurrent(line, current(value), types)

    then:
    updatedLine.with {
      assert it.uuid == line.uuid
      assert it.id == line.id
      assert it.operator == line.operator
      assert it.operationTime == line.operationTime
      assert it.nodeA == line.nodeA
      assert it.nodeB == line.nodeB
      assert it.parallelDevices == line.parallelDevices
      assert it.type == expectedType
      assert it.length == line.length
      assert it.geoPosition == line.geoPosition
      assert it.olmCharacteristic == line.olmCharacteristic
    }

    where:
    value || expectedType
    121   || TypeTestData.lineType400V_170
    171   || TypeTestData.lineType400V_170
    500   || TypeTestData.lineType400V_170
  }

  def "The AssetUpdateUtils should update line voltage correctly"() {
    given:
    def line = GridTestData.lineFtoG
    def types = TypeTestData.lineTypes()

    when:
    def updatedLine = AssetUpdateUtils.updateLineVoltage(line, potential(value), types)

    then:
    updatedLine.with {
      assert it.uuid == line.uuid
      assert it.id == line.id
      assert it.operator == line.operator
      assert it.operationTime == line.operationTime
      assert it.nodeA == line.nodeA
      assert it.nodeB == line.nodeB
      assert it.parallelDevices == line.parallelDevices
      assert it.type == expectedType
      assert it.length == line.length
      assert it.geoPosition == line.geoPosition
      assert it.olmCharacteristic == line.olmCharacteristic
    }

    where:
    value || expectedType
    380d  || TypeTestData.lineType380kV_1300
    20d   || TypeTestData.lineType20kV_400
  }

  def "The AssetUpdateUtils should update line current and voltage correctly"() {
    given:
    def line = GridTestData.lineFtoG
    def types = TypeTestData.lineTypes()

    when:
    def updatedLine = AssetUpdateUtils.updateLine(line, current, potential, types)

    then:
    updatedLine.with {
      assert it.uuid == line.uuid
      assert it.id == line.id
      assert it.operator == line.operator
      assert it.operationTime == line.operationTime
      assert it.nodeA == line.nodeA
      assert it.nodeB == line.nodeB
      assert it.parallelDevices == line.parallelDevices
      assert it.type == expectedType
      assert it.length == line.length
      assert it.geoPosition == line.geoPosition
      assert it.olmCharacteristic == line.olmCharacteristic
    }

    where:
    current       || potential       || expectedType
    current(500d) || potential(380d) || TypeTestData.lineType380kV_1300
    current(500d) || potential(20d)  || TypeTestData.lineType20kV_400
    current(500d) || potential(10d)  || TypeTestData.lineType10kV_500
    current(500d) || potential(0.4d) || TypeTestData.lineType400V_170
  }

  def "The AssetUpdateUtils should throw a MissingTypeException if no suitable line type was found"() {
    given:
    def line = GridTestData.lineFtoG
    def types = [TypeTestData.lineType10kV_500]

    when:
    AssetUpdateUtils.updateLine(line, current(130), potential(20d), types)

    then:
    MissingTypeException ex = thrown()
    ex.message == "No suitable line type found for rating: 20 kV"
  }

  def "The AssetUpdateUtils should update transformator2W power correctly"() {
    given:
    def transformer = GridTestData.transformerBtoE // hv to mv
    def types = TypeTestData.transformer2WTypes()

    when:
    def updatedTransformer = AssetUpdateUtils.updateTransformerPower(transformer, power(value), types)

    then:
    updatedTransformer.with {
      assert it.uuid == transformer.uuid
      assert it.id == transformer.id
      assert it.operator == transformer.operator
      assert it.operationTime == transformer.operationTime
      assert it.nodeA == transformer.nodeA
      assert it.nodeB == transformer.nodeB
      assert it.parallelDevices == transformer.parallelDevices
      assert it.type == expectedType
      assert it.tapPos == transformer.tapPos
      assert it.autoTap == transformer.autoTap
    }

    where:
    value  || expectedType
    10001d || TypeTestData.transformerTypeHV_10kV_20
    20000d || TypeTestData.transformerTypeHV_10kV_20
    20001d || TypeTestData.transformerTypeHV_10kV_20
  }

  def "The AssetUpdateUtils should update transformator2W voltage correctly"() {
    given:
    def transformer = GridTestData.transformerBtoE // hv to mv
    def types = TypeTestData.transformer2WTypes()

    when:
    def updatedTransformer = AssetUpdateUtils.updateTransformerVoltage(transformer, potential(valueA), potential(valueB), types)

    then:
    updatedTransformer.with {
      assert it.uuid == transformer.uuid
      assert it.id == transformer.id
      assert it.operator == transformer.operator
      assert it.operationTime == transformer.operationTime
      assert it.nodeA == transformer.nodeA
      assert it.nodeB == transformer.nodeB
      assert it.parallelDevices == transformer.parallelDevices
      assert it.type == expectedType
      assert it.tapPos == transformer.tapPos
      assert it.autoTap == transformer.autoTap
    }

    where:
    valueA || valueB || expectedType
    380d   || 110d   || TypeTestData.transformerTypeEHV_HV_40
    110d   || 30d    || TypeTestData.transformerTypeHV_30kV_40
    110d   || 20d    || TypeTestData.transformerTypeHV_20kV_40
  }

  def "The AssetUpdateUtils should update transformator2W power and voltage correctly"() {
    given:
    def transformer = GridTestData.transformerBtoE // hv to mv
    def types = TypeTestData.transformer2WTypes()

    when:
    def updatedTransformer = AssetUpdateUtils.updateTransformer(transformer, power, vRatedA, vRatedB, types)

    then:
    updatedTransformer.with {
      assert it.uuid == transformer.uuid
      assert it.id == transformer.id
      assert it.operator == transformer.operator
      assert it.operationTime == transformer.operationTime
      assert it.nodeA == transformer.nodeA
      assert it.nodeB == transformer.nodeB
      assert it.parallelDevices == transformer.parallelDevices
      assert it.type == expectedType
      assert it.tapPos == transformer.tapPos
      assert it.autoTap == transformer.autoTap
    }

    where:
    power         || vRatedA         || vRatedB         || expectedType
    power(40001d) || potential(380d) || potential(110d) || TypeTestData.transformerTypeEHV_HV_40
    power(50000d) || potential(110d) || potential(30d)  || TypeTestData.transformerTypeHV_30kV_40
    power(35000d) || potential(110d) || potential(20d)  || TypeTestData.transformerTypeHV_20kV_40
    power(16000d) || potential(110d) || potential(10d)  || TypeTestData.transformerTypeHV_10kV_20
  }

  def "The AssetUpdateUtils should throw a MissingTypeException if no suitable transformator2W type was found"() {
    given:
    def transformer = GridTestData.transformerBtoE // hv to mv
    def types = [
      TypeTestData.transformerTypeEHV_HV_40
    ]

    when:
    AssetUpdateUtils.updateTransformerVoltage(transformer, potential(110d), potential(20d), types)

    then:
    MissingTypeException ex = thrown()
    ex.message == "No suitable two winding transformer type found for rating: 110 kV -> 20 kV"
  }

  def "The AssetUpdateUtils should update transformator3W power correctly"() {
    given:
    def transformer = GridTestData.transformerAtoBtoC // ehv to hv to 20 kV
    def types = TypeTestData.transformer3WTypes()

    when:
    def updatedTransformer = AssetUpdateUtils.updateTransformerPower(transformer, power(valueA), power(valueB), power(valueC), types)

    then:
    updatedTransformer.with {
      assert it.uuid == transformer.uuid
      assert it.id == transformer.id
      assert it.operator == transformer.operator
      assert it.operationTime == transformer.operationTime
      assert it.nodeA == transformer.nodeA
      assert it.nodeB == transformer.nodeB
      assert it.nodeC == transformer.nodeC
      assert it.parallelDevices == transformer.parallelDevices
      assert it.type == expectedType
      assert it.tapPos == transformer.tapPos
      assert it.autoTap == transformer.autoTap
    }

    where:
    valueA  || valueB || valueC || expectedType
    120000d || 60000d || 40000d || TypeTestData.transformerTypeEHV_HV_20kV
    120001d || 60000d || 40000d || TypeTestData.transformerTypeEHV_HV_20kV
  }

  def "The AssetUpdateUtils should update transformator3W voltage correctly"() {
    given:
    def transformer = GridTestData.transformerAtoBtoC // ehv to hv to 20 kV
    def types = TypeTestData.transformer3WTypes()

    when:
    def updatedTransformer = AssetUpdateUtils.updateTransformerVoltage(transformer, potential(valueA), potential(valueB), potential(valueC), types)

    then:
    updatedTransformer.with {
      assert it.uuid == transformer.uuid
      assert it.id == transformer.id
      assert it.operator == transformer.operator
      assert it.operationTime == transformer.operationTime
      assert it.nodeA == transformer.nodeA
      assert it.nodeB == transformer.nodeB
      assert it.nodeC == transformer.nodeC
      assert it.parallelDevices == transformer.parallelDevices
      assert it.type == expectedType
      assert it.tapPos == transformer.tapPos
      assert it.autoTap == transformer.autoTap
    }

    where:
    valueA || valueB || valueC || expectedType
    380d   || 110d   || 20d    || TypeTestData.transformerTypeEHV_HV_20kV
    380d   || 20d    || 10d    || TypeTestData.transformerTypeEHV_20kV_10kV
    110d   || 20d    || 10d    || TypeTestData.transformerTypeHV_20kV_10kV
  }

  def "The AssetUpdateUtils should update transformator3W power and voltage correctly"() {
    given:
    def transformer = GridTestData.transformerAtoBtoC // ehv to hv to 20 kV
    def types = TypeTestData.transformer3WTypes()

    when:
    def updatedTransformer = AssetUpdateUtils.updateTransformer(transformer, powerA, powerB, powerC, vRatedA, vRatedB, vRatedC, types)

    then:
    updatedTransformer.with {
      assert it.uuid == transformer.uuid
      assert it.id == transformer.id
      assert it.operator == transformer.operator
      assert it.operationTime == transformer.operationTime
      assert it.nodeA == transformer.nodeA
      assert it.nodeB == transformer.nodeB
      assert it.nodeC == transformer.nodeC
      assert it.parallelDevices == transformer.parallelDevices
      assert it.type == expectedType
      assert it.tapPos == transformer.tapPos
      assert it.autoTap == transformer.autoTap
    }

    where:
    powerA        || powerB        || powerC        || vRatedA         || vRatedB         || vRatedC        || expectedType
    power(40000d) || power(40000d) || power(40001d) || potential(380d) || potential(110d) || potential(20d) || TypeTestData.transformerTypeEHV_HV_20kV
    power(40000d) || power(40000d) || power(20000d) || potential(380d) || potential(20d)  || potential(10d) || TypeTestData.transformerTypeEHV_20kV_10kV
    power(40000d) || power(30000d) || power(20000d) || potential(110d) || potential(20d)  || potential(10d) || TypeTestData.transformerTypeHV_20kV_10kV
  }

  def "The AssetUpdateUtils should throw a MissingTypeException if no suitable transformator3W type was found"() {
    given:
    def transformer = GridTestData.transformerAtoBtoC // hv to mv
    def types = [
      TypeTestData.transformerTypeEHV_HV_20kV
    ]

    when:
    AssetUpdateUtils.updateTransformerVoltage(transformer, potential(110d), potential(20d), potential(10d), types)

    then:
    MissingTypeException ex = thrown()
    ex.message == "No suitable three winding transformer type found for rating: 110 kV -> 20 kV -> 10 kV"
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for calculating parallel devices count

  def "The AssetUpdateUtils should calculate the needed parallel devices for lines correctly"() {
    given:
    def type = TypeTestData.lineType10kV_300

    when:
    def amount = AssetUpdateUtils.calculateNeededParallelDevices(type, current(value))

    then:
    amount == expectedAmount

    where:
    value || expectedAmount
    200d  || 1
    300d  || 1
    301d  || 2
    901d  || 4
  }

  def "The AssetUpdateUtils should calculate the needed parallel devices for transformer2w correctly"() {
    given:
    def type = TypeTestData.transformerTypeHV_10kV_10

    when:
    def amount = AssetUpdateUtils.calculateNeededParallelDevices(type, power(value))

    then:
    amount == expectedAmount

    where:
    value  || expectedAmount
    5000d  || 1
    10000d || 1
    10001d || 2
    30001d || 4
  }

  def "The AssetUpdateUtils should calculate the needed parallel devices for transformer3w correctly"() {
    given:
    def type = TypeTestData.transformerTypeEHV_20kV_10kV

    when:
    def amount = AssetUpdateUtils.calculateNeededParallelDevices(type, power(valueA), power(valueB), power(valueC))

    then:
    amount == expectedAmount

    where:
    valueA || valueB || valueC || expectedAmount
    60000d || 40000d || 20000d || 1
    60001d || 40000d || 20000d || 2
    60000d || 40001d || 20000d || 2
    60000d || 40000d || 20001d || 2
    60000d || 40000d || 40001d || 3
  }
}
