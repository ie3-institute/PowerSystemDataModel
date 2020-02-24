/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.graphics;

import com.vividsolutions.jts.geom.LineString;
import edu.ie3.models.input.connector.LineInput;
import java.util.Objects;
import java.util.UUID;

/** Describes the graphic data belonging to a {@link edu.ie3.models.input.connector.LineInput} */
public class LineGraphicInput extends GraphicInput {
  /** The LineInput to this graphic data */
  private LineInput line;

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

  public LineInput getLine() {
    return line;
  }

  public void setLine(LineInput line) {
    this.line = line;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LineGraphicInput that = (LineGraphicInput) o;
    return Objects.equals(line, that.line);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), line);
  }
}
