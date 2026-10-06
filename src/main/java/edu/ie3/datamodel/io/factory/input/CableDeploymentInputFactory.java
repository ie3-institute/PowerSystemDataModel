/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.io.factory.UniqueEntityFactory;
import edu.ie3.datamodel.io.naming.FieldNamingStrategy;
import edu.ie3.datamodel.models.input.connector.CableDeploymentInput;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.unit.Units;

import javax.measure.quantity.Length;
import java.util.Map;
import java.util.UUID;

/** Factory for building {@link CableDeploymentInput} instances from raw entity data. */
public class CableDeploymentInputFactory
    extends UniqueEntityFactory<CableDeploymentInput, CableDeploymentInput> {

  public CableDeploymentInputFactory() {
    super(CableDeploymentInput.class);
  }

  @Override
  protected CableDeploymentInput buildModel(Map<String, String> data) {
    UUID uuid = getUUID(data, FieldNamingStrategy.UUID);
    UUID lineUuid = getUUID(data, LINE_UUID);
    String layoutFormation = getField(data, LAYOUT_FORMATION);
    ComparableQuantity<Length> depthCables = getQuantity(data, DEPTH_CABLES, Units.METRE);
    ComparableQuantity<Length> distanceCables = getQuantity(data, DISTANCE_CABLES, Units.METRE);

    return new CableDeploymentInput(
        uuid, lineUuid, layoutFormation, depthCables, distanceCables, data);
  }
}
