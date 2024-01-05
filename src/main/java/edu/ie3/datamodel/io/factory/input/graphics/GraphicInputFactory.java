/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.graphics;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.util.geo.GeoUtils;
import java.util.*;
import org.locationtech.jts.geom.LineString;

/**
 * Abstract factory implementation for all {@link GraphicInput} elements
 *
 * @version 0.1
 * @since 08.04.20
 */
public abstract class GraphicInputFactory<T extends GraphicInput, D extends EntityData>
    extends EntityFactory<T, D> {

  private static final String UUID = "uuid";
  private static final String GRAPHIC_LAYER = "graphicLayer";
  private static final String PATH_LINE_STRING = "path";

  protected GraphicInputFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }

  /**
   * Returns list of sets of attribute names that the entity requires to be built.
   *
   * <p>The mandatory attributes required to create an {@link GraphicInput} are enhanced with custom
   * attribute names that each subclass factory determines in {@link #getAdditionalFields()}.
   *
   * @param entityClass class of the entity
   * @return list of possible attribute sets
   */
  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    Set<String> constructorParamsMin = newSet(UUID, GRAPHIC_LAYER, PATH_LINE_STRING);
    final String[] additionalFields = getAdditionalFields();
    constructorParamsMin = expandSet(constructorParamsMin, additionalFields);
    return Collections.singletonList(constructorParamsMin);
  }

  /**
   * Returns fields other than the required fields of {@link GraphicInput} that have to be present.
   *
   * @return Array of field names, can be empty but not null
   */
  protected abstract String[] getAdditionalFields();

  @Override
  protected T buildModel(D data) {
    UUID uuid = data.getUUID(UUID);

    final String graphicLayer = data.getField(GRAPHIC_LAYER);
    final LineString path =
        data.getLineString(PATH_LINE_STRING)
            .orElse(
                GeoUtils.buildSafeLineStringBetweenCoords(
                    NodeInput.DEFAULT_GEO_POSITION.getCoordinate(),
                    NodeInput.DEFAULT_GEO_POSITION.getCoordinate()));

    return buildModel(data, uuid, graphicLayer, path);
  }

  /**
   * Creates a graphic input entity with given parameters
   *
   * @param data entity data
   * @param uuid UUID of the input entity
   * @param graphicLayer Identifier of the graphic layer to place the object on
   * @param path Line string of the drawing
   * @return newly created asset object
   */
  protected abstract T buildModel(D data, UUID uuid, String graphicLayer, LineString path);
}
