/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import edu.ie3.datamodel.exceptions.ParsingException
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Speed

import static edu.ie3.util.quantities.PowerSystemUnits.METRE_PER_SECOND
import static edu.ie3.util.quantities.PowerSystemUnits.PU

class WecCharacteristicTest extends Specification {
  @Shared
  WecCharacteristicInput validInput

  def setupSpec() {
    SortedSet<CharacteristicPoint<Speed, Dimensionless>> points = [
      new CharacteristicPoint<Speed, Dimensionless>(Quantities.getQuantity(10, METRE_PER_SECOND),
      Quantities.getQuantity(0.05, PU)),
      new CharacteristicPoint<Speed, Dimensionless>(Quantities.getQuantity(15, METRE_PER_SECOND),
      Quantities.getQuantity(0.10, PU)),
      new CharacteristicPoint<Speed, Dimensionless>(Quantities.getQuantity(20, METRE_PER_SECOND),
      Quantities.getQuantity(0.20, PU))
    ] as SortedSet

    validInput = new WecCharacteristicInput(points)
  }

  def "A WecCharacteristicInput is correctly serialized"() {
    when:
    String actual = validInput.serialize()

    then:
    actual == "cP:{(10.0,0.05),(15.0,0.1),(20.0,0.2)}"
  }

  def "A WecCharacteristicInput is correctly set up from a correctly formatted string"() {
    when:
    WecCharacteristicInput actual = new WecCharacteristicInput("cP:{(10.0,0.05),(15.0,0.10),(20.0,0.2)}")

    then:
    actual.points == validInput.points
  }

  def "A WecCharacteristicInput throws an exception if it should be set up from a malformed string"() {
    when:
    new WecCharacteristicInput("cP:{(10.00),(15.00),(20.00)}")

    then:
    ParsingException exception = thrown(ParsingException)
    exception.message == "Cannot parse '(10.00),(15.00),(20.00)' to Set of points as it contains a malformed point."
  }
}
