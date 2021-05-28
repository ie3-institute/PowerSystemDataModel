/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.graphics;

import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.util.geo.GeoUtils;
import java.util.Objects;
import java.util.UUID;
import org.locationtech.jts.geom.LineString;

/** Describes the graphic data belonging to grid assets */
public abstract class GraphicInput extends InputEntity {
  /** Description of the graphic layer, this graphic is located on */
  private final String graphicLayer;
  /** A graphic representation as path */
  private final LineString path;

  /**
   * @param uuid of the input entity
   * @param graphicLayer Description of the graphic layer, this graphic is located on
   * @param path A graphic representation as path
   */
  public GraphicInput(UUID uuid, String graphicLayer, LineString path) {
    super(uuid);
    this.graphicLayer = graphicLayer;
    this.path =
        path == null
            ? null // can be null for NodeGraphicInput entities
            : GeoUtils.buildSafeLineString(path);
  }

  public String getGraphicLayer() {
    return graphicLayer;
  }

  public LineString getPath() {
    return path;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    GraphicInput that = (GraphicInput) o;
    return graphicLayer.equals(that.graphicLayer) && Objects.equals(path, that.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), graphicLayer, path);
  }

  @Override
  public String toString() {
    return "GraphicInput{"
        + "uuid="
        + getUuid()
        + ", graphicLayer="
        + graphicLayer
        + ", path="
        + path
        + '}';
  }

  /**
   * Abstract class for all builder that build child entities of abstract class {@link GraphicInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  protected abstract static class GraphicInputCopyBuilder<T extends GraphicInputCopyBuilder<T>>
      extends UniqueEntityCopyBuilder<T> {

    private String graphicLayer;
    private LineString path;

    protected GraphicInputCopyBuilder(GraphicInput entity) {
      super(entity);
      this.graphicLayer = entity.getGraphicLayer();
      this.path = entity.getPath();
    }

    public T graphicLayer(String graphicLayer) {
      this.graphicLayer = graphicLayer;
      return childInstance();
    }

    public T path(LineString path) {
      this.path = path;
      return childInstance();
    }

    protected String getGraphicLayer() {
      return graphicLayer;
    }

    protected LineString getPath() {
      return path;
    }

    @Override
    public abstract GraphicInput build();

    @Override
    protected abstract T childInstance();
  }
}
