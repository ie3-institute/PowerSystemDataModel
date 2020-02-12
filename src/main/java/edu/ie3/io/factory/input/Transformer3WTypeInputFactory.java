/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.connector.type.Transformer3WTypeInput;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.*;

public class Transformer3WTypeInputFactory
    extends AssetTypeInputEntityFactory<Transformer3WTypeInput> {
  private static final String S_RATED_A = "srateda";
  private static final String S_RATED_B = "sratedb";
  private static final String S_RATED_C = "sratedc";
  private static final String V_RATED_A = "vrateda";
  private static final String V_RATED_B = "vratedb";
  private static final String V_RATED_C = "vratedc";
  private static final String R_SC_A = "rsca";
  private static final String R_SC_B = "rscb";
  private static final String R_SC_C = "rscc";
  private static final String X_SC_A = "xsca";
  private static final String X_SC_B = "xscb";
  private static final String X_SC_C = "xscc";
  private static final String G_M = "gm";
  private static final String B_M = "bm";
  private static final String D_V = "dv";
  private static final String D_PHI = "dphi";
  private static final String TAP_NEUTR = "tapneutr";
  private static final String TAP_MIN = "tapmin";
  private static final String TAP_MAX = "tapmax";

  public Transformer3WTypeInputFactory() {
    super(Transformer3WTypeInput.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData data) {
    Set<String> constructorParams =
        newSet(
            ENTITY_UUID,
            ENTITY_ID,
            S_RATED_A,
            S_RATED_B,
            S_RATED_C,
            V_RATED_A,
            V_RATED_B,
            V_RATED_C,
            R_SC_A,
            R_SC_B,
            R_SC_C,
            X_SC_A,
            X_SC_B,
            X_SC_C,
            G_M,
            B_M,
            D_V,
            D_PHI,
            TAP_NEUTR,
            TAP_MIN,
            TAP_MAX);

    return Collections.singletonList(constructorParams);
  }

  @Override
  protected Transformer3WTypeInput buildModel(SimpleEntityData data) {
    UUID uuid = data.getUUID(ENTITY_UUID);
    String id = data.get(ENTITY_ID);
    Quantity<Power> sRatedA = data.get(S_RATED_A, StandardUnits.S_RATED);
    Quantity<Power> sRatedB = data.get(S_RATED_B, StandardUnits.S_RATED);
    Quantity<Power> sRatedC = data.get(S_RATED_C, StandardUnits.S_RATED);
    Quantity<ElectricPotential> vRatedA =
        data.get(V_RATED_A, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    Quantity<ElectricPotential> vRatedB =
        data.get(V_RATED_B, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    Quantity<ElectricPotential> vRatedC =
        data.get(V_RATED_C, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    Quantity<ElectricResistance> rScA = data.get(R_SC_A, StandardUnits.IMPEDANCE);
    Quantity<ElectricResistance> rScB = data.get(R_SC_B, StandardUnits.IMPEDANCE);
    Quantity<ElectricResistance> rScC = data.get(R_SC_C, StandardUnits.IMPEDANCE);
    Quantity<ElectricResistance> xScA = data.get(X_SC_A, StandardUnits.IMPEDANCE);
    Quantity<ElectricResistance> xScB = data.get(X_SC_B, StandardUnits.IMPEDANCE);
    Quantity<ElectricResistance> xScC = data.get(X_SC_C, StandardUnits.IMPEDANCE);
    Quantity<ElectricConductance> gM = data.get(G_M, StandardUnits.ADMITTANCE);
    Quantity<ElectricConductance> bM = data.get(B_M, StandardUnits.ADMITTANCE);
    Quantity<Dimensionless> dV = data.get(D_V, StandardUnits.DV_TAP);
    Quantity<Angle> dPhi = data.get(D_PHI, StandardUnits.DPHI_TAP);
    int tapNeutr = Integer.parseInt(data.get(TAP_NEUTR));
    int tapMin = Integer.parseInt(data.get(TAP_MIN));
    int tapMax = Integer.parseInt(data.get(TAP_MAX));

    return new Transformer3WTypeInput(
        uuid, id, sRatedA, sRatedB, sRatedC, vRatedA, vRatedB, vRatedC, rScA, rScB, rScC, xScA,
        xScB, xScC, gM, bM, dV, dPhi, tapNeutr, tapMin, tapMax);
  }
}
