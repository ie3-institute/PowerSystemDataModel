/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import static edu.ie3.datamodel.models.StandardUnits.TEMPERATURE;
import static edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT;
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.UniqueEntityFactory;
import edu.ie3.datamodel.models.input.SoilType;
import edu.ie3.util.quantities.interfaces.ThermalCapacitance;
import edu.ie3.util.quantities.interfaces.ThermalResistivity;
import java.util.UUID;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

/** Factory for building {@link SoilType} instances from raw entity data. */
public class SoilTypeInputFactory extends UniqueEntityFactory<SoilType, EntityData> {

  public SoilTypeInputFactory() {
    super(SoilType.class);
  }

  @Override
  protected SoilType buildModel(EntityData data) {
    UUID uuid = data.getUUID(UUID);
    String id = data.getField(ID);
    if (id.isBlank()) {
      throw new FactoryException("Missing or empty field " + ID + ".");
    }
    ComparableQuantity<ThermalResistivity> thermalResistivityWet =
        data.getQuantity(THERMAL_RESISTIVITY_WET, KELVIN_METRE_PER_WATT)
            .asType(ThermalResistivity.class);
    ComparableQuantity<ThermalResistivity> thermalResistivityDry =
        data.getQuantity(THERMAL_RESISTIVITY_DRY, KELVIN_METRE_PER_WATT)
            .asType(ThermalResistivity.class);
    ComparableQuantity<ThermalCapacitance> specificHeatCapacity =
        data.getQuantity(SPECIFIC_HEAT_CAPACITY, KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE)
            .asType(ThermalCapacitance.class);
    ComparableQuantity<Temperature> criticalTemperatureDifference =
        data.getQuantity(CRITICAL_TEMPERATURE_DIFFERENCE, TEMPERATURE);

    return new SoilType(
        uuid,
        id,
        thermalResistivityWet,
        thermalResistivityDry,
        specificHeatCapacity,
        criticalTemperatureDifference,
        data.getFieldsToValues());
  }
}
