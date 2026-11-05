/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import static edu.ie3.datamodel.models.StandardUnits.TEMPERATURE
import static edu.ie3.util.quantities.PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN
import static edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT
import static tech.units.indriya.unit.Units.METRE

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.models.input.SoilLayer
import edu.ie3.datamodel.models.input.SoilType
import edu.ie3.util.quantities.interfaces.ThermalResistivity
import edu.ie3.util.quantities.interfaces.VolumetricHeatCapacity
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Temperature

class SoilValidationUtilsTest extends Specification {
  private static final UUID UUID_VALUE = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
  private static final UUID TYPE_UUID = UUID.fromString("32b43a78-7721-431d-b1c2-56975a123670")
  private static final Geometry GEOMETRY =
  new GeometryFactory().createPolygon(
  ([
    new Coordinate(0d, 0d),
    new Coordinate(1d, 0d),
    new Coordinate(1d, 1d),
    new Coordinate(0d, 1d),
    new Coordinate(0d, 0d)
  ] as Coordinate[]))

  def "Smoke Test: A valid soil type and soil layer throws no exception"() {
    when:
    def type = SoilValidationUtils.check(validSoilType())
    def layer = SoilValidationUtils.check(validSoilLayer())
    then:
    type.every { it.success }
    layer.every { it.success }
  }
  def "The check method recognizes all potential errors for a soil type"() {
    when:
    def exceptions =
        SoilValidationUtils.check(invalidSoilType).stream().filter {
          it.failure
        }.toList()

    then:
    exceptions.size() == 1
    def ex = exceptions.get(0).exception.get()
    ex.class == InvalidEntityException
    ex.message == expectedMessage

    where:
    invalidSoilType || expectedMessage
    validSoilType().copy().id(null).build() || new InvalidEntityException("No ID assigned", validSoilType().copy().id(null).build()).message
    validSoilType().copy().id("  ").build() || new InvalidEntityException("No ID assigned", validSoilType().copy().id("  ").build()).message
    validSoilType().copy().volumetricHeatCapacity(null).build() ||
        new InvalidEntityException("Soil type has incomplete thermal properties", validSoilType().copy().volumetricHeatCapacity(null).build()).message
  }

  def "The check method reports non-positive thermal properties of a soil type"() {
    given:
    def soilType = validSoilType().copy().thermalResistivityWet(negativeResistivity()).build()

    when:
    def exceptions = SoilValidationUtils.check(soilType).stream().filter {
      it.failure
    }.toList()

    then:
    exceptions.size() == 1
    def ex = exceptions.get(0).exception.get()
    ex.class == InvalidEntityException
    ex.message.contains("The following quantities have to be positive")
  }

  def "The check method recognizes all potential errors for a soil layer"() {
    when:
    def exceptions =
        SoilValidationUtils.check(invalidSoilLayer).stream().filter {
          it.failure
        }.toList()

    then:
    exceptions.size() == 1
    def ex = exceptions.get(0).exception.get()
    ex.class == InvalidEntityException
    ex.message == expectedMessage

    where:
    invalidSoilLayer || expectedMessage
    validSoilLayer().copy().geometry(null).build() ||
        new InvalidEntityException("Soil layer has incomplete data", validSoilLayer().copy().geometry(null).build()).message
    validSoilLayer().copy().zFrom(Quantities.getQuantity(-2d, METRE)).build() ||
        new InvalidEntityException(
        "Soil layer zFrom must be greater than zTo", validSoilLayer().copy().zFrom(Quantities.getQuantity(-2d, METRE)).build()).message
    validSoilLayer().copy().zFrom(Quantities.getQuantity(-3d, METRE)).build() ||
        new InvalidEntityException(
        "Soil layer zFrom must be greater than zTo", validSoilLayer().copy().zFrom(Quantities.getQuantity(-3d, METRE)).build()).message
  }

  def "validateAll accepts a consistent, gap free and non overlapping soil model"() {
    given:
    def layer1 = layerAt(GEOMETRY, 0d, -2d)
    def layer2 = layerAt(GEOMETRY, -2d, -4d)

    when:
    def result = SoilValidationUtils.validateAll([layer1, layer2], [validSoilType()])

    then:
    result.success
  }

  def "validateAll reports overlapping soil layers at the same coordinate"() {
    given:
    def layer1 = layerAt(GEOMETRY, 0d, -2d)
    def layer2 = layerAt(GEOMETRY, -1d, -3d)

    when:
    def result = SoilValidationUtils.validateAll([layer1, layer2], [validSoilType()])

    then:
    result.failure
    result.getException().get().message.contains("Overlapping soil layers")
  }

  def "validateAll reports gaps in soil coverage at the same coordinate"() {
    given:
    def layer1 = layerAt(GEOMETRY, 0d, -2d)
    def layer2 = layerAt(GEOMETRY, -3d, -5d)

    when:
    def result = SoilValidationUtils.validateAll([layer1, layer2], [validSoilType()])

    then:
    result.failure
    result.getException().get().message.contains("Gap in soil coverage")
  }

  def "validateAll reports invalid soil types"() {
    given:
    def invalidType = validSoilType().copy().id(null).build()

    when:
    def result = SoilValidationUtils.validateAll([validSoilLayer()], [invalidType])

    then:
    result.failure
    result.getException().get().message.contains("No ID assigned")
  }

  def "validateAll tolerates null collections"() {
    when:
    def result = SoilValidationUtils.validateAll(null, null)

    then:
    result.success
  }

  private static SoilLayer layerAt(Geometry geometry, double zFrom, double zTo) {
    return layerAt(geometry, zFrom, zTo, TYPE_UUID)
  }

  private static SoilLayer layerAt(Geometry geometry, double zFrom, double zTo, UUID soilType) {
    return new SoilLayer(
        UUID.randomUUID(),
        geometry,
        Quantities.getQuantity(zFrom, METRE),
        Quantities.getQuantity(zTo, METRE),
        soilType)
  }

  private static SoilType validSoilType() {
    ComparableQuantity<ThermalResistivity> wet =
        Quantities.getQuantity(0.30d, KELVIN_METRE_PER_WATT)
    ComparableQuantity<ThermalResistivity> dry =
        Quantities.getQuantity(0.40d, KELVIN_METRE_PER_WATT)
    ComparableQuantity<VolumetricHeatCapacity> capacity =
        Quantities.getQuantity(1.5d, JOULE_PER_CUBIC_METRE_KELVIN)
    ComparableQuantity<Temperature> critical = Quantities.getQuantity(15d, TEMPERATURE)
    return new SoilType(UUID_VALUE, "loam", wet, dry, capacity, critical)
  }

  private static ComparableQuantity<ThermalResistivity> negativeResistivity() {
    return Quantities.getQuantity(-0.30d, KELVIN_METRE_PER_WATT)
  }

  private static SoilLayer validSoilLayer() {
    return new SoilLayer(
        UUID_VALUE,
        GEOMETRY,
        Quantities.getQuantity(0d, METRE),
        Quantities.getQuantity(-2d, METRE),
        TYPE_UUID)
  }
}