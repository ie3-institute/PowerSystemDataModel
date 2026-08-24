/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import com.fasterxml.jackson.databind.*;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.typeinput.parser.CableTypeParser;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.connector.type.CableTypeInput;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.*;
import javax.measure.quantity.ElectricCapacitance;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

public class CableTypeInputFactory extends AssetTypeInputEntityFactory<CableTypeInput> {

  public static final com.fasterxml.jackson.databind.ObjectMapper OBJECT_MAPPER =
      CableTypeObjectMapperProvider.OBJECT_MAPPER;

  private final CableTypeParser parser;

  public CableTypeInputFactory() {
    this(new CableTypeParser(CableTypeObjectMapperProvider.OBJECT_MAPPER));
  }

  public CableTypeInputFactory(CableTypeParser parser) {
    super(CableTypeInput.class);
    this.parser = Objects.requireNonNull(parser);
  }

  @Override
  protected CableTypeInput buildModel(EntityData data) {
    UUID uuid = data.getUUID(UUID);
    String id = data.getField(ID);
    int cores = data.getInt(CORE_NUMBER);

    final ConductorInput conductor;
    final List<LayerInput> isolation;
    final ScreenLayerInput screen;
    final List<LayerInput> filler;
    final List<LayerInput> armor;
    final List<LayerInput> jack;

    try {
      conductor = parser.parseConductor(data.getField(CONDUCTOR_STRING));
      isolation = parser.parseLayerList(data.getField(ISOLATION_STRING));
      screen = parser.parseScreenLayer(data.getField(SCREEN_STRING));
      filler = parser.parseLayerList(data.getField(FILLER_STRING));
      armor = parser.parseLayerList(data.getField(ARMOR_STRING));
      jack = parser.parseLayerList(data.getField(JACK_STRING));
    } catch (ParsingException e) {
      throw new IllegalArgumentException(
          "Cannot build CableTypeInput '"
              + id
              + "': invalid cable component JSON. Caused by: "
              + e.getMessage(),
          e);
    }

    ComparableQuantity<Temperature> limitTemp =
        data.getQuantity(LIMIT_TEMP, StandardUnits.TEMPERATURE);
    ComparableQuantity<Frequency> frequency = data.getQuantity(FREQUENCY, PowerSystemUnits.HERTZ);
    double skinEffectCoefficient = data.getDouble(SKIN_EFF_COEFF);
    double proxEffectCoefficient = data.getDouble(PROX_EFF_COEFF);
    ComparableQuantity<ElectricCapacitance> electricalCapacitance =
        data.getQuantity(ELECTR_CAPACITANCE, PowerSystemUnits.FARAD);
    double tanDelta = data.getDouble(TAN_DELTA);
    double circulatingLossFactor = data.getDouble(CIRCULATING_LOSS_FACTOR);
    double eddyCurrentLossFactor = data.getDouble(EDDY_CURRENT_LOSS_FACTOR);

    return new CableTypeInput(
        uuid,
        id,
        cores,
        conductor,
        isolation,
        screen,
        filler,
        armor,
        jack,
        limitTemp,
        frequency,
        skinEffectCoefficient,
        proxEffectCoefficient,
        electricalCapacitance,
        tanDelta,
        circulatingLossFactor,
        eddyCurrentLossFactor);
  }
}
