/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FieldMapTest {

  @Test
  void getOptionalParsesPresentValueAndConsumesField() {
    UUID uuid = UUID.randomUUID();
    FieldMap fieldMap = new FieldMap(Map.of("value", uuid.toString()));

    assertEquals(Optional.of(uuid), fieldMap.getOptional("value", UUID::fromString));
    assertFalse(fieldMap.containsKey("value"));
  }

  @Test
  void getOptionalReturnsEmptyWhenFieldIsAbsent() {
    FieldMap fieldMap = new FieldMap(Map.of());

    assertEquals(Optional.empty(), fieldMap.getOptional("value", UUID::fromString));
  }

  @Test
  void getOptionalReturnsEmptyWhenFieldIsBlankAndConsumesField() {
    FieldMap fieldMap = new FieldMap(Map.of("value", " \t"));

    assertEquals(Optional.empty(), fieldMap.getOptional("value", UUID::fromString));
    assertFalse(fieldMap.containsKey("value"));
  }

  @Test
  void getOptionalReturnsEmptyWhenFieldValueIsNull() {
    Map<String, String> values = new HashMap<>();
    values.put("value", null);
    FieldMap fieldMap = new FieldMap(values);

    assertEquals(Optional.empty(), fieldMap.getOptional("value", UUID::fromString));
    assertFalse(fieldMap.containsKey("value"));
  }

  @Test
  void getOptionalPropagatesParserFailureAndConsumesField() {
    FieldMap fieldMap = new FieldMap(Map.of("value", "invalid"));

    assertThrows(
        IllegalArgumentException.class, () -> fieldMap.getOptional("value", UUID::fromString));
    assertFalse(fieldMap.containsKey("value"));
  }
}
