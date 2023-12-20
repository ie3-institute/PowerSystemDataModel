/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.measure.quantity.*;
import tech.units.indriya.ComparableQuantity;

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
  protected List<Set<String>> getFields(EntityData data) {
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
  protected Transformer3WTypeInput buildModel(EntityData data) {
    UUID uuid = data.getUUID(ENTITY_UUID);
    String id = data.getField(ENTITY_ID);
    ComparableQuantity<Power> sRatedA = data.getQuantity(S_RATED_A, StandardUnits.S_RATED);
    ComparableQuantity<Power> sRatedB = data.getQuantity(S_RATED_B, StandardUnits.S_RATED);
    ComparableQuantity<Power> sRatedC = data.getQuantity(S_RATED_C, StandardUnits.S_RATED);
    ComparableQuantity<ElectricPotential> vRatedA =
        data.getQuantity(V_RATED_A, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    ComparableQuantity<ElectricPotential> vRatedB =
        data.getQuantity(V_RATED_B, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    ComparableQuantity<ElectricPotential> vRatedC =
        data.getQuantity(V_RATED_C, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    ComparableQuantity<ElectricResistance> rScA =
        data.getQuantity(R_SC_A, StandardUnits.RESISTANCE);
    ComparableQuantity<ElectricResistance> rScB =
        data.getQuantity(R_SC_B, StandardUnits.RESISTANCE);
    ComparableQuantity<ElectricResistance> rScC =
        data.getQuantity(R_SC_C, StandardUnits.RESISTANCE);
    ComparableQuantity<ElectricResistance> xScA = data.getQuantity(X_SC_A, StandardUnits.REACTANCE);
    ComparableQuantity<ElectricResistance> xScB = data.getQuantity(X_SC_B, StandardUnits.REACTANCE);
    ComparableQuantity<ElectricResistance> xScC = data.getQuantity(X_SC_C, StandardUnits.REACTANCE);
    ComparableQuantity<ElectricConductance> gM = data.getQuantity(G_M, StandardUnits.CONDUCTANCE);
    ComparableQuantity<ElectricConductance> bM = data.getQuantity(B_M, StandardUnits.SUSCEPTANCE);
    ComparableQuantity<Dimensionless> dV = data.getQuantity(D_V, StandardUnits.DV_TAP);
    ComparableQuantity<Angle> dPhi = data.getQuantity(D_PHI, StandardUnits.DPHI_TAP);
    int tapNeutr = data.getInt(TAP_NEUTR);
    int tapMin = data.getInt(TAP_MIN);
    int tapMax = data.getInt(TAP_MAX);

    return new Transformer3WTypeInput(
        uuid, id, sRatedA, sRatedB, sRatedC, vRatedA, vRatedB, vRatedC, rScA, rScB, rScC, xScA,
        xScB, xScC, gM, bM, dV, dPhi, tapNeutr, tapMin, tapMax);
  }
}
