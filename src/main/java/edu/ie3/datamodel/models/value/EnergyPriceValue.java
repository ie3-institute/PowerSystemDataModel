/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.utils.QuantityUtils;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.Optional;
import tech.units.indriya.ComparableQuantity;

/** Describes a price for energy. */
public class EnergyPriceValue implements Value {
  /** Price of energy (typically in €/MWh). */
  private final ComparableQuantity<EnergyPrice> price;

  /**
   * @param price per MWh
   */
  public EnergyPriceValue(ComparableQuantity<EnergyPrice> price) {
    this.price = price;
  }

  public Optional<ComparableQuantity<EnergyPrice>> getPrice() {
    return Optional.ofNullable(price);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EnergyPriceValue that)) return false;
    return QuantityUtils.equals(price, that.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(price);
  }

  @Override
  public String toString() {
    return "EnergyPriceValue{" + "price=" + price + "}";
  }
}
