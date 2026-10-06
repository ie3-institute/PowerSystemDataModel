/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.*;
import java.util.Map;

public class Transformer2WTypeInputFactory
    extends AssetTypeInputEntityFactory<Transformer2WTypeInput, Transformer2WTypeInput> {

  public Transformer2WTypeInputFactory() {
    super(Transformer2WTypeInput.class);
  }

  @Override
  protected Transformer2WTypeInput buildModel(Map<String, String> data) {
    ComparableQuantity<ElectricResistance> rSc = getQuantity(data, R_SC, StandardUnits.RESISTANCE);
    ComparableQuantity<ElectricResistance> xSc = getQuantity(data, X_SC, StandardUnits.REACTANCE);
    ComparableQuantity<Power> sRated = getQuantity(data, S_RATED, StandardUnits.S_RATED);
    ComparableQuantity<ElectricPotential> vRatedA = getQuantity(data, V_RATED_A, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    ComparableQuantity<ElectricPotential> vRatedB = getQuantity(data, V_RATED_B, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    ComparableQuantity<ElectricConductance> gM = getQuantity(data, G_M, StandardUnits.CONDUCTANCE);
    ComparableQuantity<ElectricConductance> bM = getQuantity(data, B_M, StandardUnits.SUSCEPTANCE);
    ComparableQuantity<Dimensionless> dV = getQuantity(data, D_V, StandardUnits.DV_TAP);
    ComparableQuantity<Angle> dPhi = getQuantity(data, D_PHI, StandardUnits.DPHI_TAP);
    boolean tapSide = getBoolean(data, TAP_SIDE);
    int tapNeutr = getInt(data, TAP_NEUTR);
    int tapMin = getInt(data, TAP_MIN);
    int tapMax = getInt(data, TAP_MAX);

    return new Transformer2WTypeInput(
        getUUID(data),
        getID(data),
        rSc,
        xSc,
        sRated,
        vRatedA,
        vRatedB,
        gM,
        bM,
        dV,
        dPhi,
        tapSide,
        tapNeutr,
        tapMin,
        tapMax,
        data);
  }
}
