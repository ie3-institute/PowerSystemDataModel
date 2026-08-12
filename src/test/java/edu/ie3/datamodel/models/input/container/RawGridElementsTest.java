/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import static org.junit.jupiter.api.Assertions.*;

import edu.ie3.datamodel.models.input.AssetInput;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RawGridElements Cable Deployment Tests")
class RawGridElementsCableDeploymentTest {

  @Test
  @DisplayName("Copy builder preserves cable deployments and enforces immutability")
  void copyBuilderPreservesCableDeployments() {
    UUID lineUuid = UUID.randomUUID();

    Object deployment = new Object();

    Map<UUID, List<Object>> deploymentsByLineRaw = new HashMap<>();
    deploymentsByLineRaw.put(lineUuid, new ArrayList<>(List.of(deployment)));

    RawGridElements base = new RawGridElements(new ArrayList<AssetInput>());

    RawGridElements elements =
        base.copy().cableDeploymentsByLine((Map) deploymentsByLineRaw).build();

    assertTrue(elements.getCableDeploymentsByLine().containsKey(lineUuid));
    assertEquals(1, elements.getCableDeploymentsByLine().get(lineUuid).size());

    Map rawMap = elements.getCableDeploymentsByLine();
    assertThrows(
        UnsupportedOperationException.class,
        () -> rawMap.put(UUID.randomUUID(), List.of(deployment)));

    Object entryList = rawMap.get(lineUuid);
    assertThrows(UnsupportedOperationException.class, () -> ((List) entryList).add(deployment));
  }
}
