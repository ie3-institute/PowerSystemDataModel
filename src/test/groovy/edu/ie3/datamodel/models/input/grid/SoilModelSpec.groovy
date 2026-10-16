/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.grid

import static edu.ie3.datamodel.models.StandardUnits.TEMPERATURE
import static edu.ie3.util.quantities.PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN
import static edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT
import static tech.units.indriya.unit.Units.METRE

import edu.ie3.datamodel.models.input.container.SoilLayer
import edu.ie3.datamodel.models.input.container.SoilType
import edu.ie3.util.quantities.interfaces.ThermalCapacitance
import edu.ie3.util.quantities.interfaces.ThermalResistivity
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Temperature

class SoilModelSpec extends Specification {
  static final UUID UUID_VALUE = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
  static final UUID TYPE_UUID = UUID.fromString("32b43a78-7721-431d-b1c2-56975a123670")

  def "SoilLayer exposes fields and computes absolute thickness; copy builder allows overrides"() {
    given:
    def layer = new SoilLayer(
        UUID_VALUE,
        null,
        Quantities.getQuantity(0, METRE),
        Quantities.getQuantity(-0.4, METRE),
        TYPE_UUID)

    when:
    def modified = layer.copy().zFrom(Quantities.getQuantity(-0.5, METRE)).soilType(UUID.fromString("aaaaaaaa-1111-2222-3333-444444444444")).build()

    then:
    modified.uuid == UUID_VALUE
    modified.soilType == UUID.fromString("aaaaaaaa-1111-2222-3333-444444444444")
    Math.abs(modified.thickness().to(METRE).value.doubleValue() - 0.1d) < 1e-12
  }

  def "SoilLayer.thickness throws when zFrom or zTo missing"() {
    when:
    new SoilLayer(UUID_VALUE, null, null, Quantities.getQuantity(1, METRE), TYPE_UUID).thickness()
    then:
    thrown(IllegalStateException)

    when:
    new SoilLayer(UUID_VALUE, null, Quantities.getQuantity(1, METRE), null, TYPE_UUID).thickness()
    then:
    thrown(IllegalStateException)
  }

  def "SoilLayer.zInterval returns the normalized depth interval in metres"() {
    given:
    def normalOrder = new SoilLayer(
        UUID_VALUE, null,
        Quantities.getQuantity(-2d, METRE),
        Quantities.getQuantity(0d, METRE),
        TYPE_UUID)
    def reversedOrder = new SoilLayer(
        UUID_VALUE, null,
        Quantities.getQuantity(0d, METRE),
        Quantities.getQuantity(-2d, METRE),
        TYPE_UUID)

    expect:
    normalOrder.zInterval() == new SoilLayer.ZInterval(-2d, 0d)
    reversedOrder.zInterval() == new SoilLayer.ZInterval(-2d, 0d)
  }

  def "SoilLayer.zInterval throws when zFrom or zTo missing"() {
    when:
    new SoilLayer(UUID_VALUE, null, null, Quantities.getQuantity(1, METRE), TYPE_UUID).zInterval()
    then:
    thrown(IllegalStateException)

    when:
    new SoilLayer(UUID_VALUE, null, Quantities.getQuantity(1, METRE), null, TYPE_UUID).zInterval()
    then:
    thrown(IllegalStateException)
  }

  def "ZInterval.overlaps detects shared depth range"() {
    expect:
    a.overlaps(b) == expected

    where:
    a                              | b || expected
    new SoilLayer.ZInterval(0d, 2d) | new SoilLayer.ZInterval(1d, 3d) || true
    new SoilLayer.ZInterval(0d, 2d) | new SoilLayer.ZInterval(2d, 4d) || false
    new SoilLayer.ZInterval(0d, 2d) | new SoilLayer.ZInterval(3d, 4d) || false
    new SoilLayer.ZInterval(0d, 2d) | new SoilLayer.ZInterval(-1d, 1d) || true
  }

  def "ZInterval.gapAbove detects missing coverage above another interval"() {
    given:
    def above = new SoilLayer.ZInterval(upper, upper + 1d)
    def below = new SoilLayer.ZInterval(lower, lower + 1d)

    expect:
    above.gapAbove(below) == expected

    where:
    upper || lower || expected
    3d || 1d || true
    2d || 1d || false
    3d || 0.5d || true
  }
}
