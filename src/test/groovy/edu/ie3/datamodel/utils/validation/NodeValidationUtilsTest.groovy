/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.test.common.GridTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class NodeValidationUtilsTest extends Specification{

    def "The check method in ValidationUtils delegates the check to NodeValidationUtils for a node"() {
        given:
        def node = GridTestData.nodeB

        when:
        ValidationUtils.check(node)

        then:
        0 * NodeValidationUtils.check(node)
        // TODO NSteffan: Why is the method invoked 0 times?
    }

    def "The check method in ValidationUtils recognizes a null object"() {
        // TODO NSteffan: Only in main ValidationUtilsTest?
        when:
        ValidationUtils.check(null)

        then:
        ValidationException ex = thrown()
        ex.message == "Expected an object, but got nothing. :-("
    }

    def "The check method recognizes an invalid voltage level"() {
        given:
        def correctNode = GridTestData.nodeB
        def errorNode = correctNode.copy().voltLvl(null).build()

        when:
        NodeValidationUtils.check(correctNode)
        then:
        noExceptionThrown()

        when:
        NodeValidationUtils.check(errorNode)
        then:
        ValidationException ex = thrown()
    }

    def "The check method recognizes an invalid target voltage"() {
        given:
        def correctNode = GridTestData.nodeB
        def errorNode = correctNode.copy().vTarget(Quantities.getQuantity(0d, PU)).build()

        when:
        NodeValidationUtils.check(correctNode)
        then:
        noExceptionThrown()

        when:
        NodeValidationUtils.check(errorNode)
        then:
        InvalidEntityException ex = thrown()
    }

    def "The check method recognizes an invalid subnet"() {
        given:
        def correctNode = GridTestData.nodeB
        def errorNode = correctNode.copy().subnet(0).build()

        when:
        NodeValidationUtils.check(correctNode)
        then:
        noExceptionThrown()

        when:
        NodeValidationUtils.check(errorNode)
        then:
        InvalidEntityException ex = thrown()
    }

    def "The check method recognizes an invalid geoPosition"() {
        given:
        def correctNode = GridTestData.nodeB
        def errorNode = correctNode.copy().geoPosition(null).build()

        when:
        NodeValidationUtils.check(correctNode)
        then:
        noExceptionThrown()

        when:
        NodeValidationUtils.check(errorNode)
        then:
        InvalidEntityException ex = thrown()
    }

    /*
    def "The check method recognizes an invalid geoPosition2"() {
        given:
        def node = GridTestData.nodeB.copy().geoPosition(input).build()
        def testNode = node.copy().geoPosition(input).build()

        when:
        NodeValidationUtils.check(testNode)

        then:
        thrown(ex)

        where:
        input || ex
        null  || InvalidEntityException
        node.getGeoPosition()  || null
    }
     */

}
