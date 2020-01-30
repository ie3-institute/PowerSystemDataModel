/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.io.factory.SimpleEntityFactory;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.connector.type.LineTypeInput;
import edu.ie3.util.quantities.interfaces.SpecificConductance;
import edu.ie3.util.quantities.interfaces.SpecificResistance;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;

public class LineTypeInputFactory extends SimpleEntityFactory<LineTypeInput> {
  private static final String entityUuid = "uuid";
  private static final String entityId = "id";
  private static final String b = "b";
  private static final String g = "g";
  private static final String r = "r";
  private static final String x = "x";
  private static final String iMax = "imax";
  private static final String vRated = "vrated";

  @Override
  protected List<Set<String>> getFields(SimpleEntityData entityData) {
    Set<String> constructorParams = newSet(entityUuid, entityId, b, g, r, x, iMax, vRated);

    return Collections.singletonList(constructorParams);
  }

  @Override
  protected LineTypeInput buildModel(SimpleEntityData data) {

    UUID uuid = data.getUUID(entityUuid);
    String id = data.get(entityId);
    Quantity<SpecificConductance> bVal = data.get(b, StandardUnits.SPECIFIC_ADMITTANCE);
    Quantity<SpecificConductance> gVal = data.get(g, StandardUnits.SPECIFIC_ADMITTANCE);
    Quantity<SpecificResistance> rVal = data.get(r, StandardUnits.SPECIFIC_IMPEDANCE);
    Quantity<SpecificResistance> xVal = data.get(x, StandardUnits.SPECIFIC_IMPEDANCE);
    Quantity<ElectricCurrent> iMaxVal = data.get(iMax, StandardUnits.CURRENT);
    Quantity<ElectricPotential> vRatedVal = data.get(vRated, StandardUnits.V_RATED);

    return new LineTypeInput(uuid, id, bVal, gVal, rVal, xVal, iMaxVal, vRatedVal);
  }
}
