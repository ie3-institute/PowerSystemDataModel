/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.utils.options.Try

import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT
import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.UnsafeEntityException
import edu.ie3.datamodel.models.voltagelevels.CommonVoltageLevel
import edu.ie3.util.interval.RightOpenInterval

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.test.common.GridTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class NodeValidationUtilsTest extends Specification {

  def "Smoke Test: Correct node throws no exception"() {
    given:
    def node = GridTestData.nodeA

    when:
    NodeValidationUtils.check(node)

    then:
    noExceptionThrown()
  }

  def "The check method recognizes all potential errors for a node"() {
    when:
    List<Try<Void, ValidationException>> exceptions = NodeValidationUtils.check(invalidNode).stream().filter { it -> it.failure}.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidNode                                                            	    || expectedSize || expectedException
    GridTestData.nodeA.copy().voltLvl(null).build()								|| 1            || new InvalidEntityException("Expected a voltage level, but got nothing. :-(", new NullPointerException())
    GridTestData.nodeA.copy().voltLvl(new CommonVoltageLevel(
        "null",
        null,
        new HashSet<>(Arrays.asList("null")),
        new RightOpenInterval<>(
        Quantities.getQuantity(380d, KILOVOLT), Quantities.getQuantity(560d, KILOVOLT)))).build()																	|| 1            || new InvalidEntityException("Node has invalid voltage level", invalidNode)
    GridTestData.nodeA.copy().voltLvl(new CommonVoltageLevel(
        "zero volt",
        Quantities.getQuantity(0d, KILOVOLT),
        new HashSet<>(Arrays.asList("zero volt")),
        new RightOpenInterval<>(
        Quantities.getQuantity(380d, KILOVOLT), Quantities.getQuantity(560d, KILOVOLT)))).build()																	|| 1            || new InvalidEntityException("Node has invalid voltage level", invalidNode)
    GridTestData.nodeA.copy().subnet(0).build()									|| 1            || new InvalidEntityException("Subnet can't be zero or negative", invalidNode)
    GridTestData.nodeA.copy().geoPosition(null).build()							|| 1            || new InvalidEntityException("GeoPosition of node is null", invalidNode)
    GridTestData.nodeA.copy().vTarget(Quantities.getQuantity(0d, PU)).build()   || 1            || new InvalidEntityException("Target voltage (p.u.) is not a positive value", invalidNode)
    GridTestData.nodeA.copy().vTarget(Quantities.getQuantity(2.1d, PU)).build() || 1            || new UnsafeEntityException("Target voltage (p.u.) might be too high", invalidNode)
  }
}
