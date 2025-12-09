/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.SourceValidator
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class NodeInputTest extends Specification implements FactoryTestHelper {

  def "A NodeInput should throw an exception on incorrect fields correctly"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, NodeInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'NodeInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'NodeInput' are possible (NOT case-sensitive!):\n" +
        "0: [geoPosition, id, slack, subnet, uuid, voltLvl, vRated, vTarget] or [geo_position, id, slack, subnet, uuid, v_rated, v_target, volt_lvl]\n" +
        "1: [geoPosition, id, operator, slack, subnet, uuid, voltLvl, vRated, vTarget] or [geo_position, id, operator, slack, subnet, uuid, v_rated, v_target, volt_lvl]\n" +
        "2: [geoPosition, id, operatesFrom, slack, subnet, uuid, voltLvl, vRated, vTarget] or [geo_position, id, operates_from, slack, subnet, uuid, v_rated, v_target, volt_lvl]\n" +
        "3: [geoPosition, id, operatesFrom, operator, slack, subnet, uuid, voltLvl, vRated, vTarget] or [geo_position, id, operates_from, operator, slack, subnet, uuid, v_rated, v_target, volt_lvl]\n" +
        "4: [geoPosition, id, operatesUntil, slack, subnet, uuid, voltLvl, vRated, vTarget] or [geo_position, id, operates_until, slack, subnet, uuid, v_rated, v_target, volt_lvl]\n" +
        "5: [geoPosition, id, operatesUntil, operator, slack, subnet, uuid, voltLvl, vRated, vTarget] or [geo_position, id, operates_until, operator, slack, subnet, uuid, v_rated, v_target, volt_lvl]\n" +
        "6: [geoPosition, id, operatesFrom, operatesUntil, slack, subnet, uuid, voltLvl, vRated, vTarget] or [geo_position, id, operates_from, operates_until, slack, subnet, uuid, v_rated, v_target, volt_lvl]\n" +
        "7: [geoPosition, id, operatesFrom, operatesUntil, operator, slack, subnet, uuid, voltLvl, vRated, vTarget] or [geo_position, id, operates_from, operates_until, operator, slack, subnet, uuid, v_rated, v_target, volt_lvl]\n"
  }

  def "A NodeInput copy method should work as expected"() {
    given:
    def node = GridTestData.nodeB

    when:
    def alteredUnit = node.copy().id("node_B_copy").slack(true).operator(GridTestData.profBroccoli).subnet(1)
        .voltLvl(GermanVoltageLevelUtils.EHV_220KV).build()

    then:
    alteredUnit.with {
      assert uuid == node.uuid
      assert operationTime == node.operationTime
      assert operator == GridTestData.profBroccoli
      assert id == "node_B_copy"
      assert vTarget == node.getvTarget()
      assert slack
      assert subnet == 1
      assert voltLvl == GermanVoltageLevelUtils.EHV_220KV
    }
  }
}
