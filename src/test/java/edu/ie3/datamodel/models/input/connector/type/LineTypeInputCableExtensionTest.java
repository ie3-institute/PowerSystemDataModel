/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import static edu.ie3.util.quantities.PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN;
import static edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT;
import static org.junit.jupiter.api.Assertions.*;
import static tech.units.indriya.unit.Units.*;
import static tech.units.indriya.unit.Units.CELSIUS;
import static tech.units.indriya.unit.Units.FARAD;
import static tech.units.indriya.unit.Units.HERTZ;
import static tech.units.indriya.unit.Units.METRE;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.util.quantities.interfaces.SpecificConductance;
import edu.ie3.util.quantities.interfaces.SpecificResistance;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/** Unit tests for LineTypeInput cable-related extensions. */
@DisplayName("LineTypeInput Cable Extensions Tests")
class LineTypeInputCableExtensionTest {
  UUID uuid = UUID.randomUUID();

  ComparableQuantity<SpecificConductance> b =
      Quantities.getQuantity(1.0, StandardUnits.SUSCEPTANCE_PER_LENGTH);

  ComparableQuantity<SpecificConductance> g =
      Quantities.getQuantity(2.0, StandardUnits.CONDUCTANCE_PER_LENGTH);

  ComparableQuantity<SpecificResistance> r =
      Quantities.getQuantity(0.1, StandardUnits.RESISTANCE_PER_LENGTH);

  ComparableQuantity<SpecificResistance> x =
      Quantities.getQuantity(0.05, StandardUnits.REACTANCE_PER_LENGTH);

  ComparableQuantity<ElectricCurrent> iMax = Quantities.getQuantity(500.0, Units.AMPERE);
  ComparableQuantity<ElectricPotential> vRated =
      Quantities.getQuantity(30.0, PowerSystemUnits.KILOVOLT);

  CableTypeInput cableType =
      new CableTypeInput(
          UUID.fromString("289ad5c9-90f8-4640-afee-ecbf84a5d4c7"),
          "test cable type input",
          1,
          new ConductorInput(
              UUID.randomUUID(),
              "conductor",
              CableMaterial.COPPER,
              Quantities.getQuantity(400.0e-6, SQUARE_METRE),
              Quantities.getQuantity(0.0225, METRE),
              false,
              Quantities.getQuantity(1.0 / 384.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(3449600.0, JOULE_PER_CUBIC_METRE_KELVIN),
              Optional.empty()),
          List.of(
              new LayerInput(
                  UUID.randomUUID(),
                  "Main insulation",
                  CableMaterial.XLPE,
                  Quantities.getQuantity(0.0225, METRE),
                  Quantities.getQuantity(0.027, METRE),
                  Quantities.getQuantity(3.5, KELVIN_METRE_PER_WATT),
                  Quantities.getQuantity(2.4, JOULE_PER_CUBIC_METRE_KELVIN),
                  Optional.empty())),
          Optional.empty(),
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>(),
          Quantities.getQuantity(90.0, CELSIUS),
          Quantities.getQuantity(50.0, HERTZ),
          1.0,
          1.0,
          Quantities.getQuantity(350e-9, FARAD),
          0.1,
          0.0,
          0.0);
  CableTypeInput cableType2 =
      new CableTypeInput(
          UUID.fromString("289ad5c9-90f8-4640-afee-ecbf84a5d4c7"),
          "test cable type input",
          1,
          new ConductorInput(
              UUID.randomUUID(),
              "conductor",
              CableMaterial.COPPER,
              Quantities.getQuantity(400.0e-6, SQUARE_METRE),
              Quantities.getQuantity(0.0225, METRE),
              false,
              Quantities.getQuantity(1.0 / 384.0, KELVIN_METRE_PER_WATT),
              Quantities.getQuantity(3449600.0, JOULE_PER_CUBIC_METRE_KELVIN),
              Optional.empty()),
          List.of(
              new LayerInput(
                  UUID.randomUUID(),
                  "Main insulation",
                  CableMaterial.XLPE,
                  Quantities.getQuantity(0.0225, METRE),
                  Quantities.getQuantity(0.027, METRE),
                  Quantities.getQuantity(3.5, KELVIN_METRE_PER_WATT),
                  Quantities.getQuantity(2.4, JOULE_PER_CUBIC_METRE_KELVIN),
                  Optional.empty())),
          Optional.empty(),
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>(),
          Quantities.getQuantity(90.0, CELSIUS),
          Quantities.getQuantity(50.0, HERTZ),
          1.0,
          1.0,
          Quantities.getQuantity(350e-9, FARAD),
          0.1,
          0.0,
          0.0);

  @Test
  @DisplayName("Test LineTypeInput creation with cable parameters")
  void testLineTypeInputWithCableParameters() {
    LineTypeInput line =
        new LineTypeInput(uuid, "Test Line", b, g, r, x, iMax, vRated, Optional.of(cableType));
    assertNotNull(line);
    assertEquals(Optional.of(cableType), line.getCableType());
  }

  @Test
  @DisplayName("Test LineTypeInput with cableType")
  void testLineTypeInputWithOnlyCableTypeUuid() {
    LineTypeInput line =
        new LineTypeInput(uuid, "Test Line", b, g, r, x, iMax, vRated, Optional.of(cableType));
    assertEquals("test cable type input", line.getCableType().get().getId());
  }

  @Test
  @DisplayName("Test LineTypeInput equals with same cable parameters")
  void testLineTypeInputEqualsWithCableParameters() {
    LineTypeInput line1 =
        new LineTypeInput(uuid, "Test Line", b, g, r, x, iMax, vRated, Optional.of(cableType));
    LineTypeInput line2 =
        new LineTypeInput(uuid, "Test Line", b, g, r, x, iMax, vRated, Optional.of(cableType));
    assertEquals(line1, line2);
  }

  @Test
  @DisplayName("Test LineTypeInput not equals with different cable UUIDs")
  void testLineTypeInputNotEqualsDifferentCableUuid() {

    LineTypeInput line1 =
        new LineTypeInput(uuid, "Test Line", b, g, r, x, iMax, vRated, Optional.of(cableType));
    LineTypeInput line2 =
        new LineTypeInput(uuid, "Test Line", b, g, r, x, iMax, vRated, Optional.of(cableType2));
    assertNotEquals(line1, line2);
  }

  @Test
  @DisplayName("Test LineTypeInput copy builder with cable parameters")
  void testLineTypeInputCopyBuilderWithCableParameters() {
    LineTypeInput original =
        new LineTypeInput(uuid, "Test Line", b, g, r, x, iMax, vRated, Optional.of(cableType));

    LineTypeInput copied = original.copy().build();

    assertEquals(Optional.of(cableType), copied.getCableType());
  }

  @Test
  @DisplayName("Test LineTypeInput toString includes cable parameters")
  void testLineTypeInputToStringWithCableParameters() {
    LineTypeInput line =
        new LineTypeInput(uuid, "Test Line", b, g, r, x, iMax, vRated, Optional.of(cableType));
    String str = line.toString();
    assertNotNull(str);
    assertTrue(str.contains("cableType"));
  }

  @Test
  @DisplayName("Test LineTypeInput hashCode with cable parameters")
  void testLineTypeInputHashCodeWithCableParameters() {
    LineTypeInput line1 =
        new LineTypeInput(uuid, "Test Line", b, g, r, x, iMax, vRated, Optional.of(cableType));
    LineTypeInput line2 =
        new LineTypeInput(uuid, "Test Line", b, g, r, x, iMax, vRated, Optional.of(cableType));
    assertEquals(line1.hashCode(), line2.hashCode());
  }

  @Test
  @DisplayName("Test LineTypeInput getters for cable parameters")
  void testLineTypeInputGettersCableParameters() {
    UUID uuid = UUID.randomUUID();

    LineTypeInput line =
        new LineTypeInput(uuid, "Test Line", b, g, r, x, iMax, vRated, Optional.of(cableType));

    assertEquals(Optional.of(cableType), line.getCableType());
  }
}
