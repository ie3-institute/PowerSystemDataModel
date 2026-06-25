/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import static org.junit.jupiter.api.Assertions.*;

import javax.measure.quantity.Length;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/** Unit tests for CableDeploymentInput class. */
@DisplayName("CableDeploymentInput Tests")
class CableDeploymentInputTest {

  private ComparableQuantity<Length> depthCables;
  private ComparableQuantity<Length> distanceCables;

  @BeforeEach
  void setUp() {
    depthCables = Quantities.getQuantity(-1.0, Units.METRE);
    distanceCables = Quantities.getQuantity(0.3, Units.METRE);
  }

  @Test
  @DisplayName("Test CableDeploymentInput creation with valid parameters")
  void testCableDeploymentInputCreation() {
    CableDeploymentInput env = new CableDeploymentInput("TREFOIL", depthCables, distanceCables);
    assertNotNull(env);
    assertEquals("TREFOIL", env.layoutFormation());
    assertEquals(depthCables, env.depthCables());
    assertEquals(distanceCables, env.distanceCables());
  }

  @Test
  @DisplayName("Test CableDeploymentInput with FLAT layout")
  void testCableDeploymentInputFlat() {
    CableDeploymentInput env = new CableDeploymentInput("FLAT", depthCables, distanceCables);
    assertEquals("FLAT", env.layoutFormation());
  }

  @Test
  @DisplayName("Test CableDeploymentInput null layoutFormation validation")
  void testCableDeploymentInputNullLayoutFormation() {
    assertThrows(
        NullPointerException.class,
        () -> new CableDeploymentInput(null, depthCables, distanceCables));
  }

  @Test
  @DisplayName("Test CableDeploymentInput empty layoutFormation validation")
  void testCableDeploymentInputEmptyLayoutFormation() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new CableDeploymentInput("", depthCables, distanceCables));
  }

  @Test
  @DisplayName("Test CableDeploymentInput positive depthCables validation")
  void testCableDeploymentInputNegativeDepth() {
    ComparableQuantity<Length> positiveDepth = Quantities.getQuantity(1.0, Units.METRE);
    assertThrows(
        IllegalArgumentException.class,
        () -> new CableDeploymentInput("TREFOIL", positiveDepth, distanceCables));
  }

  @Test
  @DisplayName("Test CableDeploymentInput zero distanceCables validation")
  void testCableDeploymentInputNegativeDistance() {
    ComparableQuantity<Length> zeroDistance = Quantities.getQuantity(0.0, Units.METRE);
    assertThrows(
        IllegalArgumentException.class,
        () -> new CableDeploymentInput("TREFOIL", depthCables, zeroDistance));
  }

  @Test
  @DisplayName("Test CableDeploymentInput equals method")
  void testCableDeploymentInputEquals() {
    CableDeploymentInput env1 = new CableDeploymentInput("TREFOIL", depthCables, distanceCables);
    CableDeploymentInput env2 = new CableDeploymentInput("TREFOIL", depthCables, distanceCables);
    assertEquals(env1, env2);
  }

  @Test
  @DisplayName("Test CableDeploymentInput hashCode consistency")
  void testCableDeploymentInputHashCode() {
    CableDeploymentInput env1 = new CableDeploymentInput("TREFOIL", depthCables, distanceCables);
    CableDeploymentInput env2 = new CableDeploymentInput("TREFOIL", depthCables, distanceCables);
    assertEquals(env1.hashCode(), env2.hashCode());
  }

  @Test
  @DisplayName("Test CableDeploymentInput toString")
  void testCableDeploymentInputToString() {
    CableDeploymentInput env = new CableDeploymentInput("TREFOIL", depthCables, distanceCables);
    String str = env.toString();
    assertNotNull(str);
    assertTrue(str.contains("TREFOIL"));
  }

  @Test
  @DisplayName("Test CableDeploymentInput null depthCables validation")
  void testCableDeploymentInputNullDepth() {
    assertThrows(
        NullPointerException.class,
        () -> new CableDeploymentInput("TREFOIL", null, distanceCables));
  }

  @Test
  @DisplayName("Test CableDeploymentInput null distanceCables validation")
  void testCableDeploymentInputNullDistance() {
    assertThrows(
        NullPointerException.class, () -> new CableDeploymentInput("TREFOIL", depthCables, null));
  }

  @Test
  @DisplayName("Test CableDeploymentInput not equals for different layouts")
  void testCableDeploymentInputNotEqualsLayout() {
    CableDeploymentInput env1 = new CableDeploymentInput("TREFOIL", depthCables, distanceCables);
    CableDeploymentInput env2 = new CableDeploymentInput("FLAT", depthCables, distanceCables);
    assertNotEquals(env1, env2);
  }

  @Test
  @DisplayName("Test CableDeploymentInput with zero depths")
  void testCableDeploymentInputZeroDepths() {
    ComparableQuantity<Length> zeroDepth = Quantities.getQuantity(0.0, Units.METRE);
    ComparableQuantity<Length> zeroDistance = Quantities.getQuantity(0.0, Units.METRE);
    assertThrows(
        IllegalArgumentException.class,
        () -> new CableDeploymentInput("TREFOIL", zeroDepth, zeroDistance));
  }

  @Test
  @DisplayName("Test CableDeploymentInput getters")
  void testCableDeploymentInputGetters() {
    CableDeploymentInput env = new CableDeploymentInput("TREFOIL", depthCables, distanceCables);
    assertEquals("TREFOIL", env.layoutFormation());
    assertEquals(depthCables, env.depthCables());
    assertEquals(distanceCables, env.distanceCables());
  }
}
