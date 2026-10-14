/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.grid


import static edu.ie3.datamodel.models.StandardUnits.TEMPERATURE
import static edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE
import static tech.units.indriya.unit.Units.METRE

import edu.ie3.datamodel.models.input.container.EnergyManagementUnits
import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.util.quantities.interfaces.ThermalCapacitance
import edu.ie3.util.quantities.interfaces.ThermalResistivity
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class SoilDataSpec extends Specification {
  def "SoilData lists soil types"() {
    given:
    def uuid = UUID.randomUUID()
    def soilType = new SoilType(
        uuid,
        "s1",
        Quantities.getQuantity(1d, KELVIN_METRE_PER_WATT).asType(ThermalResistivity.class),
        Quantities.getQuantity(2d, KELVIN_METRE_PER_WATT).asType(ThermalResistivity.class),
        Quantities.getQuantity(3d, KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE).asType(ThermalCapacitance.class),
        Quantities.getQuantity(4d, TEMPERATURE)
    )

    when:
    def soilData = new SoilData(Set.of(soilType))
    def entities = soilData.allEntitiesAsList()

    then:
    entities.size() == 1
    entities.contains(soilType)
  }

  def "SoilData holds soil types and layers and exposes them unmodifiable"() {
    given:
    def soilType = soilType()
    def layer = soilLayer(soilType.uuid)

    when:
    def soilData = new SoilData(Set.of(soilType), Set.of(layer))

    then:
    soilData.soilTypes == Set.of(soilType)
    soilData.soilLayers == Set.of(layer)
    soilData.allEntitiesAsList() == [soilType, layer]
    when:
    soilData.allEntitiesAsList().add(new Object())
    then:
    thrown(UnsupportedOperationException)
  }

  def "SoilData treats null arguments as empty"() {
    when:
    def soilData = new SoilData(null, null)

    then:
    soilData.soilTypes.isEmpty()
    soilData.soilLayers.isEmpty()
    soilData.allEntitiesAsList().isEmpty()
  }

  def "SoilData.empty returns an empty holder"() {
    when:
    def soilData = SoilData.empty()

    then:
    soilData.soilTypes.isEmpty()
    soilData.soilLayers.isEmpty()
    soilData.allEntitiesAsList().isEmpty()
  }

  def "SoilData equality is based on soil types and layers"() {
    given:
    def soilType = soilType()
    def layer = soilLayer(soilType.uuid)

    when:
    def a = new SoilData(Set.of(soilType), Set.of(layer))
    def b = new SoilData(Set.of(soilType), Set.of(layer))

    then:
    a == b
    a.hashCode() == b.hashCode()
  }

  def "JointGridContainer exposes soil layers via allEntitiesAsList"() {
    given:
    def soilType = soilType()
    def layer = soilLayer(soilType.uuid)
    def soilData = new SoilData(Set.of(soilType), Set.of(layer))
    def rawGrid = new RawGridElements(Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet())
    def sysParts = new SystemParticipants(Collections.emptyList())
    def emUnits = new EnergyManagementUnits(Collections.emptySet())
    def container = new JointGridContainer("test", rawGrid, sysParts, emUnits, soilData)

    when:
    def all = container.allEntitiesAsList()

    then:
    all.contains(layer)
  }

  private static SoilType soilType() {
    new SoilType(
        UUID.randomUUID(),
        "s1",
        Quantities.getQuantity(1d, KELVIN_METRE_PER_WATT).asType(ThermalResistivity.class),
        Quantities.getQuantity(2d, KELVIN_METRE_PER_WATT).asType(ThermalResistivity.class),
        Quantities.getQuantity(3d, KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE).asType(ThermalCapacitance.class),
        Quantities.getQuantity(4d, TEMPERATURE)
    )
  }

  private static SoilLayer soilLayer(UUID typeUuid) {
    def gf = new GeometryFactory()
    def coords = [
      new Coordinate(0, 0),
      new Coordinate(1, 0),
      new Coordinate(1, 1),
      new Coordinate(0, 0)
    ] as Coordinate[]
    new SoilLayer(UUID.randomUUID(), gf.createPolygon(coords),
        Quantities.getQuantity(0d, METRE), Quantities.getQuantity(1d, METRE), typeUuid)
  }
}