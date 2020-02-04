/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.io.factory.SimpleEntityFactory;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.connector.type.Transformer3WTypeInput;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.*;

public class Transformer3WTypeInputFactory extends SimpleEntityFactory<Transformer3WTypeInput> {
  private static final String entityUuid = "uuid";
  private static final String entityId = "id";
  private static final String sRatedA = "srateda";
  private static final String sRatedB = "sratedb";
  private static final String sRatedC = "sratedc";
  private static final String vRatedA = "vrateda";
  private static final String vRatedB = "vratedb";
  private static final String vRatedC = "vratedc";
  private static final String rScA = "rsca";
  private static final String rScB = "rscb";
  private static final String rScC = "rscc";
  private static final String xScA = "xsca";
  private static final String xScB = "xscb";
  private static final String xScC = "xscc";
  private static final String gM = "gm";
  private static final String bM = "bm";
  private static final String dV = "dv";
  private static final String dPhi = "dphi";
  private static final String tapNeutr = "tapneutr";
  private static final String tapMin = "tapmin";
  private static final String tapMax = "tapmax";

  public Transformer3WTypeInputFactory() {
    super(Transformer3WTypeInput.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData data) {
    Set<String> constructorParams =
        newSet(
            entityUuid,
            entityId,
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
            tapMax);

    return Collections.singletonList(constructorParams);
  }

  @Override
  protected Transformer3WTypeInput buildModel(SimpleEntityData data) {
    UUID uuid = data.getUUID(entityUuid);
    String id = data.get(entityId);
    Quantity<Power> sRatedAVal = data.get(sRatedA, StandardUnits.S_RATED);
    Quantity<Power> sRatedBVal = data.get(sRatedB, StandardUnits.S_RATED);
    Quantity<Power> sRatedCVal = data.get(sRatedC, StandardUnits.S_RATED);
    Quantity<ElectricPotential> vRatedAVal = data.get(vRatedA, StandardUnits.V_RATED);
    Quantity<ElectricPotential> vRatedBVal = data.get(vRatedB, StandardUnits.V_RATED);
    Quantity<ElectricPotential> vRatedCVal = data.get(vRatedC, StandardUnits.V_RATED);
    Quantity<ElectricResistance> rScAVal = data.get(rScA, StandardUnits.IMPEDANCE);
    Quantity<ElectricResistance> rScBVal = data.get(rScB, StandardUnits.IMPEDANCE);
    Quantity<ElectricResistance> rScCVal = data.get(rScC, StandardUnits.IMPEDANCE);
    Quantity<ElectricResistance> xScAVal = data.get(xScA, StandardUnits.IMPEDANCE);
    Quantity<ElectricResistance> xScBVal = data.get(xScB, StandardUnits.IMPEDANCE);
    Quantity<ElectricResistance> xScCVal = data.get(xScC, StandardUnits.IMPEDANCE);
    Quantity<ElectricConductance> gMVal = data.get(gM, StandardUnits.ADMITTANCE);
    Quantity<ElectricConductance> bMVal = data.get(bM, StandardUnits.ADMITTANCE);
    Quantity<Dimensionless> dVVal = data.get(dV, StandardUnits.DV_TAP);
    Quantity<Angle> dPhiVal = data.get(dPhi, StandardUnits.DPHI_TAP);
    int tapNeutrVal = Integer.parseInt(data.get(tapNeutr));
    int tapMinVal = Integer.parseInt(data.get(tapMin));
    int tapMaxVal = Integer.parseInt(data.get(tapMax));

    return new Transformer3WTypeInput(
        uuid,
        id,
        sRatedAVal,
        sRatedBVal,
        sRatedCVal,
        vRatedAVal,
        vRatedBVal,
        vRatedCVal,
        rScAVal,
        rScBVal,
        rScCVal,
        xScAVal,
        xScBVal,
        xScCVal,
        gMVal,
        bMVal,
        dVVal,
        dPhiVal,
        tapNeutrVal,
        tapMinVal,
        tapMaxVal);
  }
}
