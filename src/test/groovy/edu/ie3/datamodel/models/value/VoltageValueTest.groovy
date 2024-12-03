/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.value

import static edu.ie3.util.quantities.PowerSystemUnits.*

import edu.ie3.util.quantities.QuantityUtil
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class VoltageValueTest extends Specification {

  @Shared
  double tolerance = 1e-10

  def "A VoltageValue should return the real part correctly"() {
    when:
    def actual = value.realPart

    then:
    actual.isPresent()
    QuantityUtil.isEquivalentAbs(actual.get(), expected, tolerance)

    where:
    value                                                                                    | expected
    new VoltageValue(Quantities.getQuantity(1, PU), Quantities.getQuantity(0, DEGREE_GEOM))  | Quantities.getQuantity(1, PU)
    new VoltageValue(Quantities.getQuantity(1, PU), Quantities.getQuantity(45, DEGREE_GEOM)) | Quantities.getQuantity(0.7071067811865476, PU)
    new VoltageValue(Quantities.getQuantity(1, PU), Quantities.getQuantity(90, DEGREE_GEOM)) | Quantities.getQuantity(0, PU)
  }


  def "A VoltageValue should return the imaginary part correctly"() {
    when:
    def actual = value.imagPart

    then:
    actual.isPresent()
    QuantityUtil.isEquivalentAbs(actual.get(), expected, tolerance)

    where:
    value                                                                                    | expected
    new VoltageValue(Quantities.getQuantity(1, PU), Quantities.getQuantity(0, DEGREE_GEOM))  | Quantities.getQuantity(0, PU)
    new VoltageValue(Quantities.getQuantity(1, PU), Quantities.getQuantity(45, DEGREE_GEOM)) | Quantities.getQuantity(0.7071067811865475, PU)
    new VoltageValue(Quantities.getQuantity(1, PU), Quantities.getQuantity(90, DEGREE_GEOM)) | Quantities.getQuantity(1, PU)
  }
}
