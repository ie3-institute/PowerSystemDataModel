/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.util.quantities.interfaces.SpecificConductance;
import edu.ie3.util.quantities.interfaces.SpecificResistance;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import tech.units.indriya.ComparableQuantity;

public class LineTypeInputFactory extends AssetTypeInputEntityFactory<LineTypeInput> {
  private static final String B = "b";
  private static final String G = "g";
  private static final String R = "r";
  private static final String X = "x";
  private static final String I_MAX = "iMax";
  private static final String V_RATED = "vRated";

  public LineTypeInputFactory() {
    super(LineTypeInput.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    Set<String> constructorParams = newSet(ENTITY_UUID, ENTITY_ID, B, G, R, X, I_MAX, V_RATED);

    return Collections.singletonList(constructorParams);
  }

  @Override
  protected LineTypeInput buildModel(EntityData data) {
    UUID uuid = data.getUUID(ENTITY_UUID);
    String id = data.getField(ENTITY_ID);
    ComparableQuantity<SpecificConductance> b =
        data.getQuantity(B, StandardUnits.SUSCEPTANCE_PER_LENGTH);
    ComparableQuantity<SpecificConductance> g =
        data.getQuantity(G, StandardUnits.CONDUCTANCE_PER_LENGTH);
    ComparableQuantity<SpecificResistance> r =
        data.getQuantity(R, StandardUnits.RESISTANCE_PER_LENGTH);
    ComparableQuantity<SpecificResistance> x =
        data.getQuantity(X, StandardUnits.REACTANCE_PER_LENGTH);
    ComparableQuantity<ElectricCurrent> iMax =
        data.getQuantity(I_MAX, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE);
    ComparableQuantity<ElectricPotential> vRated =
        data.getQuantity(V_RATED, StandardUnits.RATED_VOLTAGE_MAGNITUDE);

    return new LineTypeInput(uuid, id, b, g, r, x, iMax, vRated);
  }
}
