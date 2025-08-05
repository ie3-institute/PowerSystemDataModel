/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.graph;

import static tech.units.indriya.unit.Units.OHM;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.ElectricResistance;
import org.jgrapht.graph.DefaultWeightedEdge;
import tech.units.indriya.quantity.Quantities;

/**
 * A default implementation for edges in a {@link ImpedanceWeightedGraph}. All access to the weight
 * of an edge must go through the graph interface, which is why this class doesn't expose any public
 * methods.
 */
public class ImpedanceWeightedEdge extends DefaultWeightedEdge {
  /** Default constructor for ImpedanceWeightedEdge. */
  public ImpedanceWeightedEdge() {}

  private static final long serialVersionUID = -3331046813188425729L;

  /** The constant DEFAULT_IMPEDANCE_UNIT. */
  protected static final Unit<ElectricResistance> DEFAULT_IMPEDANCE_UNIT = OHM;

  /**
   * Gets impedance.
   *
   * @return the impedance
   */
  public Quantity<ElectricResistance> getImpedance() {
    return Quantities.getQuantity(getWeight(), DEFAULT_IMPEDANCE_UNIT);
  }

  @Override
  public String toString() {
    return "ImpedanceWeightedEdge{" + "impedance=" + getImpedance() + "} " + super.toString();
  }
}
