/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.models.input.container.EnergyManagementUnits
import edu.ie3.datamodel.models.input.system.EmInput

class EnergyManagementTestData {

  public static final UUID[] connectedAssets = new UUID[]{
    SystemParticipantTestData.loadInput.uuid,
    SystemParticipantTestData.pvInput.uuid
  }
  public static final String emControlStrategy = "self_optimization"
  public static final UUID parentEm = UUID.fromString("897bfc17-8e54-43d0-8d98-740786fd94dd")


  public static final emInput = new EmInput(
  UUID.fromString("977157f4-25e5-4c72-bf34-440edc778792"),
  "test_emInput",
  SystemParticipantTestData.operator,
  SystemParticipantTestData.operationTime,
  emControlStrategy,
  parentEm
  )

  public static EnergyManagementUnits emptyEnergyManagementUnits =
  new EnergyManagementUnits([] as List)
}
