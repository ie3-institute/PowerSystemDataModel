/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import javax.measure.Quantity;

/** Describes a price for energy */
public class EnergyPriceValue implements Value {
  /** Price of energy (typically in €/MWh) */
  private Quantity<EnergyPrice> price;

  /** @param price per MWh */
  public EnergyPriceValue(Quantity<EnergyPrice> price) {
    this.price = price.to(StandardUnits.ENERGY_PRICE);
  }

  public Quantity<EnergyPrice> getPrice() {
    return price;
  }

  public void setPrice(Quantity<EnergyPrice> price) {
    this.price = price.to(StandardUnits.ENERGY_PRICE);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EnergyPriceValue that = (EnergyPriceValue) o;
    return price.equals(that.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(price);
  }
}
