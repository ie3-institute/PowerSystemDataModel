/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.graphics;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import java.util.Map;
import java.util.Objects;

/**
 * Data used by {@link LineGraphicInputFactory} used to create instances of {@link
 * edu.ie3.datamodel.models.input.graphics.LineGraphicInput}s holding one {@link LineInput} entity.
 */
public class LineGraphicInputEntityData extends EntityData {

  /** The LineInput to this graphic data */
  private final LineInput line;

  /**
   * Creates a new EntityData object
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param line Referenced electrical line
   */
  public LineGraphicInputEntityData(Map<String, String> fieldsToAttributes, LineInput line) {
    super(fieldsToAttributes, LineGraphicInput.class);
    this.line = line;
  }

  public LineInput getLine() {
    return line;
  }

  @Override
  public String toString() {
    return "LineGraphicInputEntityData{"
        + "line="
        + line.getUuid()
        + ", fieldsToValues="
        + getFieldsToValues()
        + ", targetClass="
        + getTargetClass()
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LineGraphicInputEntityData that = (LineGraphicInputEntityData) o;
    return getLine().equals(that.getLine());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getLine());
  }
}
