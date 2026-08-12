/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.UniqueEntityFactory;
import edu.ie3.datamodel.io.naming.FieldNamingStrategy;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.connector.CableDeploymentInput;
import java.util.UUID;
import javax.measure.quantity.Length;
import tech.units.indriya.ComparableQuantity;

public class CableDeploymentInputFactory
    extends UniqueEntityFactory<CableDeploymentInput, EntityData> {

  public CableDeploymentInputFactory() {
    super(CableDeploymentInput.class);
  }

  @Override
  protected CableDeploymentInput buildModel(EntityData data) {
    UUID uuid = data.getUUID(FieldNamingStrategy.UUID);
    UUID lineUuid = data.getUUID("lineUuid");
    String layoutFormation =
        data.getFieldOptional(FieldNamingStrategy.LAYOUT_FORMATION).orElse(null);
    ComparableQuantity<Length> depthCables =
        data.getQuantityOptional(FieldNamingStrategy.DEPTH_CABLES, StandardUnits.LINE_LENGTH)
            .orElse(null);
    ComparableQuantity<Length> distanceCables =
        data.getQuantityOptional(FieldNamingStrategy.DISTANCE_CABLES, StandardUnits.LINE_LENGTH)
            .orElse(null);

    return new CableDeploymentInput(
        uuid, lineUuid, layoutFormation, depthCables, distanceCables, data.getFieldsToValues());
  }
}
