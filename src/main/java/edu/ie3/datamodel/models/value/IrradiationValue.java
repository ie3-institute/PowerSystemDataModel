/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Irradiation;
import java.util.Objects;
import javax.measure.Quantity;

/** Describes an irradiation value as a pair of diffuse and direct radiation */
public class IrradiationValue implements Value {
  /** Direct sun radiation (typically in W/m²) */
  private Quantity<Irradiation> directIrradiation;
  /** Diffuse sun radiation (typically in W/m²) */
  private Quantity<Irradiation> diffuseIrradiation;

  /**
   * @param directIrradiation Direct sun radiation (typically in W/m²)
   * @param diffuseIrradiation Diffuse sun radiation (typically in W/m²)
   */
  public IrradiationValue(
      Quantity<Irradiation> directIrradiation, Quantity<Irradiation> diffuseIrradiation) {
    this.directIrradiation = directIrradiation.to(StandardUnits.IRRADIATION);
    this.diffuseIrradiation = diffuseIrradiation.to(StandardUnits.IRRADIATION);
  }

  public Quantity<Irradiation> getDiffuseIrradiation() {
    return diffuseIrradiation;
  }

  public void setDiffuseIrradiation(Quantity<Irradiation> diffuseIrradiation) {
    this.diffuseIrradiation = diffuseIrradiation.to(StandardUnits.IRRADIATION);
  }

  public Quantity<Irradiation> getDirectIrradiation() {
    return directIrradiation;
  }

  public void setDirectIrradiation(Quantity<Irradiation> directIrradiation) {
    this.directIrradiation = directIrradiation.to(StandardUnits.IRRADIATION);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IrradiationValue that = (IrradiationValue) o;
    return directIrradiation.equals(that.directIrradiation)
        && diffuseIrradiation.equals(that.diffuseIrradiation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(directIrradiation, diffuseIrradiation);
  }
}
