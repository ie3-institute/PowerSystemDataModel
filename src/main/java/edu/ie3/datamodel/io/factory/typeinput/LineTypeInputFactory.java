/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.naming.FieldNamingStrategy;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.connector.type.CableTypeInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.util.quantities.interfaces.SpecificConductance;
import edu.ie3.util.quantities.interfaces.SpecificResistance;
import java.util.*;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;

public class LineTypeInputFactory extends AssetTypeInputEntityFactory<LineTypeInput> {

  private static final Logger log = LoggerFactory.getLogger(LineTypeInputFactory.class);

  private final Map<UUID, CableTypeInput> cableTypes;

  public LineTypeInputFactory() {
    this(Collections.emptyMap());
  }

  public LineTypeInputFactory(Map<UUID, CableTypeInput> cableTypes) {
    super(LineTypeInput.class);
    this.cableTypes = Objects.requireNonNull(cableTypes);
  }

  @Override
  protected LineTypeInput buildModel(EntityData data) {
    UUID uuid = data.getUUID(UUID);
    String id = data.getField(ID);
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

    Optional<CableTypeInput> cableType =
        data.getFieldOptional(FieldNamingStrategy.CABLE_TYPE)
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .flatMap(
                cableUuidStr -> {
                  try {
                    UUID cableUuid = java.util.UUID.fromString(cableUuidStr);
                    return Optional.ofNullable(this.cableTypes.get(cableUuid));
                  } catch (IllegalArgumentException e) {
                    log.warn(
                        "Ignoring invalid cable_type UUID '{}' for line type {}",
                        cableUuidStr,
                        uuid,
                        e);
                    return Optional.empty();
                  }
                });

    if (cableType.isEmpty()
        && data.getFieldsToValues().containsKey(FieldNamingStrategy.CABLE_TYPE)) {
      // field was present but no matching cable type found
      String value = data.getFieldsToValues().get(FieldNamingStrategy.CABLE_TYPE);
      if (value != null && !value.isBlank()) {
        log.warn("Ignoring unknown cable_type '{}' for line type {}", value.trim(), uuid);
      }
    }

    return new LineTypeInput(
        uuid, id, b, g, r, x, iMax, vRated, cableType, data.getFieldsToValues());
  }
}
