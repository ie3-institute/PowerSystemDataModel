/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import static edu.ie3.util.quantities.PowerSystemUnits.METRE;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.UniqueEntityFactory;
import edu.ie3.datamodel.models.input.SoilLayer;
import java.util.UUID;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import tech.units.indriya.ComparableQuantity;

/** Factory for building {@link SoilLayer} instances from raw entity data. */
public class SoilLayerInputFactory extends UniqueEntityFactory<SoilLayer, EntityData> {
  public SoilLayerInputFactory() {
    super(SoilLayer.class);
  }

  @Override
  protected SoilLayer buildModel(EntityData data) {
    UUID uuid = data.getUUID(UUID);
    Geometry parsedGeometry =
        data.getGeometry(GEOMETRY)
            .orElseThrow(() -> new FactoryException("Expected polygon geometry of soil layer"));
    if (!(parsedGeometry instanceof Polygon polygon)) {
      throw new FactoryException("Unsupported GeoJSON type: " + parsedGeometry.getGeometryType());
    }
    if (polygon.isEmpty()) {
      throw new FactoryException("Expected polygon coordinates of soil layer");
    }
    ComparableQuantity<Length> zFrom = data.getQuantity(Z_FROM, METRE);
    ComparableQuantity<Length> zTo = data.getQuantity(Z_TO, METRE);
    UUID soilType = data.getUUID(SOIL_TYPE);

    return new SoilLayer(uuid, polygon, zFrom, zTo, soilType, data.getFieldsToValues());
  }
}
