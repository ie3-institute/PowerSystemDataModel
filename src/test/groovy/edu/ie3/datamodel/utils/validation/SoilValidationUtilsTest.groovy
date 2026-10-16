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
import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.models.input.container.SoilLayer
import edu.ie3.datamodel.models.input.container.SoilType
import edu.ie3.util.quantities.interfaces.ThermalCapacitance
import edu.ie3.util.quantities.interfaces.ThermalResistivity
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

  def "Smoke Test: A valid soil type throws no exception"() {
    when:
    def tries = SoilValidationUtils.check(validSoilType())

    then:
    tries.every { it.success }
  }

  def "Smoke Test: A valid soil layer throws no exception"() {
    when:
    def tries = SoilValidationUtils.check(validSoilLayer())

    then:
    tries.every { it.success }
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
    validSoilType().copy().specificHeatCapacity(null).build() ||
        new InvalidEntityException("Soil type has incomplete thermal properties", validSoilType().copy().specificHeatCapacity(null).build()).message
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
        "Soil layer has a non-positive thickness", validSoilLayer().copy().zFrom(Quantities.getQuantity(-2d, METRE)).build()).message
  }

  def "validateAll accepts a consistent, gap free and non overlapping soil model"() {
    given:
    def layer1 = layerAt(GEOMETRY, 0d, -2d)
    def layer2 = layerAt(GEOMETRY, -2d, -4d)

    when:
    SoilValidationUtils.validateAll([layer1, layer2], [validSoilType()])

    then:
    notThrown(ValidationException)
  }

  def "validateAll reports overlapping soil layers at the same coordinate"() {
    given:
    def layer1 = layerAt(GEOMETRY, 0d, -2d)
    def layer2 = layerAt(GEOMETRY, -1d, -3d)

    when:
    SoilValidationUtils.validateAll([layer1, layer2], [validSoilType()])

    then:
    def ex = thrown(ValidationException)
    ex.message.contains("Overlapping soil layers")
  }

  def "validateAll reports gaps in soil coverage at the same coordinate"() {
    given:
    def layer1 = layerAt(GEOMETRY, 0d, -2d)
    def layer2 = layerAt(GEOMETRY, -3d, -5d)

    when:
    SoilValidationUtils.validateAll([layer1, layer2], [validSoilType()])

    then:
    def ex = thrown(ValidationException)
    ex.message.contains("Gap in soil coverage")
  }

  def "validateAll reports invalid soil types"() {
    given:
    def invalidType = validSoilType().copy().id(null).build()

    when:
    SoilValidationUtils.validateAll([validSoilLayer()], [invalidType])

    then:
    def ex = thrown(ValidationException)
    ex.message.contains("No ID assigned")
  }

  def "validateAll tolerates null collections"() {
    when:
    SoilValidationUtils.validateAll(null, null)

    then:
    notThrown(ValidationException)
  }

  def "totalThicknessBySoilType sums layer thickness per soil type"() {
    given:
    def typeA = UUID.fromString("32b43a78-7721-431d-b1c2-56975a123670")
    def typeB = UUID.fromString("44c54b89-8832-442e-c2d3-67086b234781")
    def layers = [
      layerAt(GEOMETRY, 0d, -2d, typeA),
      layerAt(GEOMETRY, -2d, -5d, typeA),
      layerAt(GEOMETRY, 0d, -1d, typeB)
    ]

    when:
    def result = SoilValidationUtils.totalThicknessBySoilType(layers)

    then:
    result.size() == 2
    result.get(typeA).to(METRE).value.doubleValue() == 5.0d
    result.get(typeB).to(METRE).value.doubleValue() == 1.0d

    when:
    def empty = SoilValidationUtils.totalThicknessBySoilType(null)

    then:
    empty.isEmpty()
  }

  def "associateLayersWithTypes resolves references and reports missing ones"() {
    given:
    def knownType = validSoilType()
    def unknownTypeUuid = UUID.randomUUID()
    def layerKnown = layerAt(GEOMETRY, 0d, -1d, knownType.uuid)
    def layerUnknown = layerAt(GEOMETRY, -1d, -2d, unknownTypeUuid)
    def layerNoType = layerAt(GEOMETRY, -2d, -3d, null)

    when:
    def mapping = SoilValidationUtils.associateLayersWithTypes(
        [
          layerKnown,
          layerUnknown,
          layerNoType
        ], [knownType])

    then:
    mapping.size() == 3
    mapping.get(layerKnown) == Optional.of(knownType)
    !mapping.get(layerUnknown).present
    !mapping.get(layerNoType).present

    when:
    def empty = SoilValidationUtils.associateLayersWithTypes(null, [knownType])

    then:
    empty.isEmpty()
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
        Quantities.getQuantity(0.30d, KELVIN_METRE_PER_WATT).asType(ThermalResistivity.class)
    ComparableQuantity<ThermalResistivity> dry =
        Quantities.getQuantity(0.40d, KELVIN_METRE_PER_WATT).asType(ThermalResistivity.class)
    ComparableQuantity<ThermalCapacitance> capacity =
        Quantities.getQuantity(1.5d, JOULE_PER_CUBIC_METRE_KELVIN)
    ComparableQuantity<Temperature> critical = Quantities.getQuantity(15d, TEMPERATURE)
    return new SoilType(UUID_VALUE, "loam", wet, dry, capacity, critical)
  }

  private static ComparableQuantity<ThermalResistivity> negativeResistivity() {
    return Quantities.getQuantity(-0.30d, KELVIN_METRE_PER_WATT).asType(ThermalResistivity.class)
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