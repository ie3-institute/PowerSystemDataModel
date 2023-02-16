/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.measure.quantity.*;
import tech.units.indriya.ComparableQuantity;

public class Transformer2WTypeInputFactory
    extends AssetTypeInputEntityFactory<Transformer2WTypeInput> {
  private static final String R_SC = "rsc";
  private static final String X_SC = "xsc";
  private static final String S_RATED = "srated";
  private static final String V_RATED_A = "vrateda";
  private static final String V_RATED_B = "vratedb";
  private static final String G_M = "gm";
  private static final String B_M = "bm";
  private static final String D_V = "dv";
  private static final String D_PHI = "dphi";
  private static final String TAP_SIDE = "tapside";
  private static final String TAP_NEUTR = "tapneutr";
  private static final String TAP_MIN = "tapmin";
  private static final String TAP_MAX = "tapmax";

  public Transformer2WTypeInputFactory() {
    super(Transformer2WTypeInput.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData data) {
    Set<String> constructorParams =
        newSet(
            ENTITY_UUID,
            ENTITY_ID,
            R_SC,
            X_SC,
            S_RATED,
            V_RATED_A,
            V_RATED_B,
            G_M,
            B_M,
            D_V,
            D_PHI,
            TAP_SIDE,
            TAP_NEUTR,
            TAP_MIN,
            TAP_MAX);

    return Collections.singletonList(constructorParams);
  }

  @Override
  protected Transformer2WTypeInput buildModel(SimpleEntityData data) {
    UUID uuid = data.getUUID(ENTITY_UUID);
    String id = data.getField(ENTITY_ID);
    ComparableQuantity<ElectricResistance> rSc = data.getQuantity(R_SC, StandardUnits.RESISTANCE);
    ComparableQuantity<ElectricResistance> xSc = data.getQuantity(X_SC, StandardUnits.REACTANCE);
    ComparableQuantity<Power> sRated = data.getQuantity(S_RATED, StandardUnits.S_RATED);
    ComparableQuantity<ElectricPotential> vRatedA =
        data.getQuantity(V_RATED_A, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    ComparableQuantity<ElectricPotential> vRatedB =
        data.getQuantity(V_RATED_B, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    ComparableQuantity<ElectricConductance> gM = data.getQuantity(G_M, StandardUnits.CONDUCTANCE);
    ComparableQuantity<ElectricConductance> bM = data.getQuantity(B_M, StandardUnits.SUSCEPTANCE);
    ComparableQuantity<Dimensionless> dV = data.getQuantity(D_V, StandardUnits.DV_TAP);
    ComparableQuantity<Angle> dPhi = data.getQuantity(D_PHI, StandardUnits.DPHI_TAP);
    boolean tapSide = data.getBoolean(TAP_SIDE);
    int tapNeutr = data.getInt(TAP_NEUTR);
    int tapMin = data.getInt(TAP_MIN);
    int tapMax = data.getInt(TAP_MAX);

    return new Transformer2WTypeInput(
        uuid, id, rSc, xSc, sRated, vRatedA, vRatedB, gM, bM, dV, dPhi, tapSide, tapNeutr, tapMin,
        tapMax);
  }
}
