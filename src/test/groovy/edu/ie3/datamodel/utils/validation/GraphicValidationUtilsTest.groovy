/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.utils.options.Try
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class GraphicValidationUtilsTest extends Specification {

  def "Smoke Test: Correct graphic input throws no exception"() {
    given:
    def lineGraphicInput = GridTestData.lineGraphicCtoD
    def nodeGraphicInput = GridTestData.nodeGraphicC

    when:
    ValidationUtils.check(lineGraphicInput)
    then:
    noExceptionThrown()

    when:
    ValidationUtils.check(nodeGraphicInput)
    then:
    noExceptionThrown()
  }

  def "GraphicValidationUtils.check() recognizes all potential errors for a graphic input"() {
    when:
    List<Try<Void, InvalidEntityException>> exceptions = GraphicValidationUtils.check(invalidGraphicInput).stream().filter {it -> it.failure}.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidGraphicInput                                               || expectedSize || expectedException
    GridTestData.lineGraphicCtoD.copy().graphicLayer(null).build()    || 1            || new InvalidEntityException("Graphic Layer of graphic element is not defined", invalidGraphicInput)
  }

  def "GraphicValidationUtils.checkLineGraphicInput() recognizes all potential errors for a line graphic input"() {
    when:
    List<Try<Void, InvalidEntityException>> exceptions = GraphicValidationUtils.check(invalidLineGraphicInput).stream().filter {it -> it.failure}.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidLineGraphicInput                                    || expectedSize        || expectedException
    GridTestData.lineGraphicCtoD.copy().path(null).build()     || 1                   || new InvalidEntityException("Path of line graphic element is not defined", invalidLineGraphicInput)
  }

  def "GraphicValidationUtils.checkNodeGraphicInput() recognizes all potential errors for a line graphic input"() {
    when:
    List<Try<Void, InvalidEntityException>> exceptions = GraphicValidationUtils.check(invalidNodeGraphicInput).stream().filter {it -> it.failure}.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidNodeGraphicInput                                    || expectedSize      || expectedException
    GridTestData.nodeGraphicC.copy().point(null).build()       || 1                 || new InvalidEntityException("Point of node graphic is not defined", invalidNodeGraphicInput)
  }
}
