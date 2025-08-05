/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.graphics;

import edu.ie3.datamodel.models.input.UniqueInputEntity;
import edu.ie3.util.geo.GeoUtils;
import java.util.Objects;
import java.util.UUID;
import org.locationtech.jts.geom.LineString;

/** Describes the graphic data belonging to grid assets */
public abstract class GraphicInput extends UniqueInputEntity {
  /** Description of the graphic layer, this graphic is located on */
  private final String graphicLayer;

  /** A graphic representation as path */
  private final LineString path;

  /**
   * Instantiates a new Graphic input.
   *
   * @param uuid of the input entity
   * @param graphicLayer Description of the graphic layer, this graphic is located on
   * @param path A graphic representation as path
   */
  protected GraphicInput(UUID uuid, String graphicLayer, LineString path) {
    super(uuid);
    this.graphicLayer = graphicLayer;
    this.path =
        path == null
            ? null // can be null for NodeGraphicInput entities
            : GeoUtils.buildSafeLineString(path);
  }

  /**
   * Gets graphic layer.
   *
   * @return the graphic layer
   */
  public String getGraphicLayer() {
    return graphicLayer;
  }

  /**
   * Gets path.
   *
   * @return the path
   */
  public LineString getPath() {
    return path;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GraphicInput that)) return false;
    if (!super.equals(o)) return false;
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
   * Copy graphic input copy builder.
   *
   * @return the graphic input copy builder
   */
  public abstract GraphicInputCopyBuilder<? extends GraphicInputCopyBuilder<?>> copy();

  /**
   * Abstract class for all builder that build child entities of abstract class {@link GraphicInput}
   *
   * @param <B> the type parameter
   * @version 0.1
   * @since 05.06.20
   */
  public abstract static class GraphicInputCopyBuilder<B extends GraphicInputCopyBuilder<B>>
      extends UniqueEntityCopyBuilder<B> {

    private String graphicLayer;
    private LineString path;

    /**
     * Instantiates a new Graphic input copy builder.
     *
     * @param entity the entity
     */
    protected GraphicInputCopyBuilder(GraphicInput entity) {
      super(entity);
      this.graphicLayer = entity.getGraphicLayer();
      this.path = entity.getPath();
    }

    /**
     * Graphic layer b.
     *
     * @param graphicLayer the graphic layer
     * @return the b
     */
    public B graphicLayer(String graphicLayer) {
      this.graphicLayer = graphicLayer;
      return thisInstance();
    }

    /**
     * Path b.
     *
     * @param path the path
     * @return the b
     */
    public B path(LineString path) {
      this.path = path;
      return thisInstance();
    }

    /**
     * Gets graphic layer.
     *
     * @return the graphic layer
     */
    protected String getGraphicLayer() {
      return graphicLayer;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    protected LineString getPath() {
      return path;
    }

    @Override
    public abstract GraphicInput build();

    @Override
    protected abstract B thisInstance();
  }
}
