/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.system.characteristic

import edu.ie3.datamodel.exceptions.ParsingException
import edu.ie3.datamodel.models.input.system.characteristic.*
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless

import static edu.ie3.util.quantities.PowerSystemUnits.PU

class ReactivePowerCharacteristicTest extends Specification {
  @Shared
  CosPhiFixed validCosPhiFixed

  @Shared
  String validCosPhiFixedSerialized = "cosPhiFixed:{(0.0,0.95)}"

  @Shared
  CosPhiP validCosPhiP

  @Shared
  String validCosPhiPSerialized = "cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"

  @Shared
  QV validQV

  @Shared
  String validQVSerialized = "qV:{(0.9,-0.3),(0.95,0.0),(1.05,0.0),(1.1,0.3)}"

  def setupSpec() {
    validCosPhiFixed = new CosPhiFixed(
        [
          new CharacteristicPoint<Dimensionless, Dimensionless>(
          Quantities.getQuantity(0d, PU), Quantities.getQuantity(0.95, PU))
        ] as SortedSet<CharacteristicPoint<Dimensionless, Dimensionless>>
        )

    validCosPhiP = new CosPhiP(
        [
          new CharacteristicPoint<Dimensionless, Dimensionless>(
          Quantities.getQuantity(0d, PU), Quantities.getQuantity(1.0, PU)),
          new CharacteristicPoint<Dimensionless, Dimensionless>(
          Quantities.getQuantity(0.9, PU), Quantities.getQuantity(1.0, PU)),
          new CharacteristicPoint<Dimensionless, Dimensionless>(
          Quantities.getQuantity(1.2, PU), Quantities.getQuantity(-0.3, PU))
        ] as SortedSet<CharacteristicPoint<Dimensionless, Dimensionless>>
        )

    validQV = new QV(
        [
          new CharacteristicPoint<Dimensionless, Dimensionless>(
          Quantities.getQuantity(0.9, PU), Quantities.getQuantity(-0.3, PU)),
          new CharacteristicPoint<Dimensionless, Dimensionless>(
          Quantities.getQuantity(0.95, PU), Quantities.getQuantity(0.0, PU)),
          new CharacteristicPoint<Dimensionless, Dimensionless>(
          Quantities.getQuantity(1.05, PU), Quantities.getQuantity(0.0, PU)),
          new CharacteristicPoint<Dimensionless, Dimensionless>(
          Quantities.getQuantity(1.1, PU), Quantities.getQuantity(0.3, PU))
        ] as SortedSet<CharacteristicPoint<Dimensionless, Dimensionless>>
        )
  }

  def "A valid CosPhiFixed is correctly serialized"() {
    when: "Serializing a valid input"
    String actual = validCosPhiFixed.serialize()

    then: "it returns the correct string"
    actual == validCosPhiFixedSerialized
  }

  def "A CosPhiFixed is correctly set up from a correctly formatted string"() {
    when:
    CosPhiFixed actual = new CosPhiFixed(validCosPhiFixedSerialized)

    then:
    actual.points == validCosPhiFixed.points
  }

  def "A CosPhiFixed throws an exception if it should be set up from a malformed string"() {
    when:
    new CosPhiFixed("cosPhiFixed:{(10.00)}")

    then:
    ParsingException exception = thrown(ParsingException)
    exception.message == "Cannot parse '(10.00)' to Set of points as it contains a malformed point."
  }

  def "A valid CosPhiP is correctly serialized"() {
    when: "Serializing a valid input"
    String actual = validCosPhiP.serialize()

    then: "it returns the correct string"
    actual == validCosPhiPSerialized
  }

  def "A CosPhiP is correctly set up from a correctly formatted string"() {
    when:
    CosPhiP actual = new CosPhiP(validCosPhiPSerialized)

    then:
    actual.points == validCosPhiP.points
  }

  def "A CosPhiP throws an exception if it should be set up from a malformed string"() {
    when:
    new CosPhiFixed("cosPhiP:{(0.00),(0.90),(1.20)}")

    then:
    ParsingException exception = thrown(ParsingException)
    exception.message == "Cannot parse 'cosPhiP:{(0.00),(0.90),(1.20)}' to characteristic. It has to be of the form 'cosPhiFixed:{(%d,%d),...}'"
  }

  def "A valid QV is correctly serialized"() {
    when: "Serializing a valid input"
    String actual = validQV.serialize()

    then: "it returns the correct string"
    actual == validQVSerialized
  }

  def "A QV is correctly set up from a correctly formatted string"() {
    when:
    QV actual = new QV(validQVSerialized)

    then:
    actual.points == validQV.points
  }

  def "A QV throws an exception if it should be set up from a malformed string"() {
    when:
    new CosPhiFixed("qV:{(0.90),(0.95),(1.05),(1.10)}")

    then:
    ParsingException exception = thrown(ParsingException)
    exception.message == "Cannot parse 'qV:{(0.90),(0.95),(1.05),(1.10)}' to characteristic. It has to be of the form 'cosPhiFixed:{(%d,%d),...}'"
  }

  def "The ReactivePowerCharacteristic is able to parse a fixed power factor correctly from string"() {
    when:
    ReactivePowerCharacteristic actual = ReactivePowerCharacteristic.parse(validCosPhiFixedSerialized)

    then:
    actual instanceof CosPhiFixed
    actual.points == validCosPhiFixed.points
  }

  def "The ReactivePowerCharacteristic is able to parse a power dependent power factor correctly from string"() {
    when:
    ReactivePowerCharacteristic actual = ReactivePowerCharacteristic.parse(validCosPhiPSerialized)

    then:
    actual instanceof CosPhiP
    actual.points == validCosPhiP.points
  }

  def "The ReactivePowerCharacteristic is able to parse voltage dependent reactive power correctly from string"() {
    when:
    ReactivePowerCharacteristic actual = ReactivePowerCharacteristic.parse(validQVSerialized)

    then:
    actual instanceof QV
    actual.points == validQV.points
  }

  def "The ReactivePowerCharacteristic throws an Exception, if no valid characteristic format can be recognized"() {
    when:
    ReactivePowerCharacteristic.parse("nonSense:{bli,bla,blob}")

    then:
    ParsingException exception = thrown(ParsingException)
    exception.message == "Cannot parse 'nonSense:{bli,bla,blob}' to a reactive power characteristic, as it does " +
        "not meet the specifications of any of the available classes."
  }
}
