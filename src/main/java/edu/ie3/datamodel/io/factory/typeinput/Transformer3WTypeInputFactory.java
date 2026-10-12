/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import java.util.Map;
import javax.measure.quantity.*;
import tech.units.indriya.ComparableQuantity;

public class Transformer3WTypeInputFactory
    extends AssetTypeInputEntityFactory<Transformer3WTypeInput, Transformer3WTypeInput> {

  public Transformer3WTypeInputFactory() {
    super(Transformer3WTypeInput.class);
  }

  @Override
  protected Transformer3WTypeInput buildModel(Map<String, String> data) {
    ComparableQuantity<Power> sRatedA = getQuantity(data, S_RATED_A, StandardUnits.S_RATED);
    ComparableQuantity<Power> sRatedB = getQuantity(data, S_RATED_B, StandardUnits.S_RATED);
    ComparableQuantity<Power> sRatedC = getQuantity(data, S_RATED_C, StandardUnits.S_RATED);
    ComparableQuantity<ElectricPotential> vRatedA =
        getQuantity(data, V_RATED_A, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    ComparableQuantity<ElectricPotential> vRatedB =
        getQuantity(data, V_RATED_B, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    ComparableQuantity<ElectricPotential> vRatedC =
        getQuantity(data, V_RATED_C, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    ComparableQuantity<ElectricResistance> rScA =
        getQuantity(data, R_SC_A, StandardUnits.RESISTANCE);
    ComparableQuantity<ElectricResistance> rScB =
        getQuantity(data, R_SC_B, StandardUnits.RESISTANCE);
    ComparableQuantity<ElectricResistance> rScC =
        getQuantity(data, R_SC_C, StandardUnits.RESISTANCE);
    ComparableQuantity<ElectricResistance> xScA =
        getQuantity(data, X_SC_A, StandardUnits.REACTANCE);
    ComparableQuantity<ElectricResistance> xScB =
        getQuantity(data, X_SC_B, StandardUnits.REACTANCE);
    ComparableQuantity<ElectricResistance> xScC =
        getQuantity(data, X_SC_C, StandardUnits.REACTANCE);
    ComparableQuantity<ElectricConductance> gM = getQuantity(data, G_M, StandardUnits.CONDUCTANCE);
    ComparableQuantity<ElectricConductance> bM = getQuantity(data, B_M, StandardUnits.SUSCEPTANCE);
    ComparableQuantity<Dimensionless> dV = getQuantity(data, D_V, StandardUnits.DV_TAP);
    ComparableQuantity<Angle> dPhi = getQuantity(data, D_PHI, StandardUnits.DPHI_TAP);
    int tapNeutr = getInt(data, TAP_NEUTR);
    int tapMin = getInt(data, TAP_MIN);
    int tapMax = getInt(data, TAP_MAX);

    return new Transformer3WTypeInput(
        getUUID(data),
        getID(data),
        sRatedA,
        sRatedB,
        sRatedC,
        vRatedA,
        vRatedB,
        vRatedC,
        rScA,
        rScB,
        rScC,
        xScA,
        xScB,
        xScC,
        gM,
        bM,
        dV,
        dPhi,
        tapNeutr,
        tapMin,
        tapMax,
        data);
  }
}
