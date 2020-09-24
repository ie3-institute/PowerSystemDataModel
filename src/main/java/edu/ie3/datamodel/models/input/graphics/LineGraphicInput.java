/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.graphics;

import edu.ie3.datamodel.io.extractor.HasLine;
import edu.ie3.datamodel.models.input.connector.LineInput;
import java.util.Objects;
import java.util.UUID;
import org.locationtech.jts.geom.LineString;

/**
 * Describes the graphic data belonging to a {@link
 * edu.ie3.datamodel.models.input.connector.LineInput}
 */
public class LineGraphicInput extends GraphicInput implements HasLine {
  /** The LineInput to this graphic data */
  private final LineInput line;

  /**
   * @param uuid of the input entity
   * @param graphicLayer Description of the graphic layer, this graphic is located on
   * @param path A graphic representation as path
   * @param line The LineInput to this graphic data
   */
  public LineGraphicInput(UUID uuid, String graphicLayer, LineString path, LineInput line) {
    super(uuid, graphicLayer, path);
    this.line = line;
  }

  @Override
  public LineInput getLine() {
    return line;
  }

  public LineGraphicInputCopyBuilder copy() {
    return new LineGraphicInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LineGraphicInput that = (LineGraphicInput) o;
    return line.equals(that.line);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), line);
  }

  @Override
  public String toString() {
    return "LineGraphicInput{" + "uuid=" + getUuid() + ", line=" + line.getUuid() + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link LineGraphicInput} entities with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * LineGraphicInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class LineGraphicInputCopyBuilder
      extends GraphicInputCopyBuilder<LineGraphicInputCopyBuilder> {

    private LineInput line;

    private LineGraphicInputCopyBuilder(LineGraphicInput entity) {
      super(entity);
      this.line = entity.getLine();
    }

    @Override
    protected LineGraphicInputCopyBuilder childInstance() {
      return this;
    }

    @Override
    public LineGraphicInput build() {
      return new LineGraphicInput(getUuid(), getGraphicLayer(), getPath(), line);
    }

    public LineGraphicInputCopyBuilder line(LineInput line) {
      this.line = line;
      return this;
    }
  }
}
