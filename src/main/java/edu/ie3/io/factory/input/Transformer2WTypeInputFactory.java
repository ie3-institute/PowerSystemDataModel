/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.io.factory.SimpleEntityFactory;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.connector.type.Transformer2WTypeInput;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.*;

public class Transformer2WTypeInputFactory extends SimpleEntityFactory<Transformer2WTypeInput> {
  private static final String entityUuid = "uuid";
  private static final String entityId = "id";
  private static final String rSc = "rsc";
  private static final String xSc = "xsc";
  private static final String sRated = "srated";
  private static final String vRatedA = "vrateda";
  private static final String vRatedB = "vratedb";
  private static final String gM = "gm";
  private static final String bM = "bm";
  private static final String dV = "dv";
  private static final String dPhi = "dphi";
  private static final String tapSide = "tapside";
  private static final String tapNeutr = "tapneutr";
  private static final String tapMin = "tapmin";
  private static final String tapMax = "tapmax";

  @Override
  protected List<Set<String>> getFields(SimpleEntityData data) {
    Set<String> constructorParams =
        newSet(
            entityUuid,
            entityId,
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
            tapMax);

    return Collections.singletonList(constructorParams);
  }

  @Override
  protected Transformer2WTypeInput buildModel(SimpleEntityData data) {
    UUID uuid = data.getUUID(entityUuid);
    String id = data.get(entityId);
    Quantity<ElectricResistance> rScVal = data.get(rSc, StandardUnits.IMPEDANCE);
    Quantity<ElectricResistance> xScVal = data.get(xSc, StandardUnits.IMPEDANCE);
    Quantity<Power> sRatedVal = data.get(sRated, StandardUnits.S_RATED);
    Quantity<ElectricPotential> vRatedAVal = data.get(vRatedA, StandardUnits.V_RATED);
    Quantity<ElectricPotential> vRatedBVal = data.get(vRatedB, StandardUnits.V_RATED);
    Quantity<ElectricConductance> gMVal = data.get(gM, StandardUnits.ADMITTANCE);
    Quantity<ElectricConductance> bMVal = data.get(bM, StandardUnits.ADMITTANCE);
    Quantity<Dimensionless> dVVal = data.get(dV, StandardUnits.DV_TAP);
    Quantity<Angle> dPhiVal = data.get(dPhi, StandardUnits.DPHI_TAP);
    boolean tapSideVal = data.get(tapSide).trim().equals("1");
    int tapNeutrVal = Integer.parseInt(data.get(tapNeutr));
    int tapMinVal = Integer.parseInt(data.get(tapMin));
    int tapMaxVal = Integer.parseInt(data.get(tapMax));

    return new Transformer2WTypeInput(
        uuid,
        id,
        rScVal,
        xScVal,
        sRatedVal,
        vRatedAVal,
        vRatedBVal,
        gMVal,
        bMVal,
        dVVal,
        dPhiVal,
        tapSideVal,
        tapNeutrVal,
        tapMinVal,
        tapMaxVal);
  }
}
