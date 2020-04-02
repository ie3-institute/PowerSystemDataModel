/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import tec.uom.se.ComparableQuantity;

/** Describes a price for energy */
public class EnergyPriceValue implements Value {
  /** Price of energy (typically in €/MWh) */
  private ComparableQuantity<EnergyPrice> price; // TODO doublecheck

  /** @param price per MWh */
  public EnergyPriceValue(ComparableQuantity<EnergyPrice> price) {
    this.price = price.to(StandardUnits.ENERGY_PRICE);
  } // TODO doublecheck

  public ComparableQuantity<EnergyPrice> getPrice() {
    return price;
  } // TODO doublecheck

  public void setPrice(ComparableQuantity<EnergyPrice> price) {
    this.price = price.to(StandardUnits.ENERGY_PRICE);
  } // TODO doublecheck

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

  @Override
  public String toString() {
    return "EnergyPriceValue{" + "price=" + price + '}';
  }
}
