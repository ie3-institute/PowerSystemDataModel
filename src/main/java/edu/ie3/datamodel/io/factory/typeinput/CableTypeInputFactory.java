/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.typeinput.parser.CableTypeParser;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.connector.type.CableTypeInput;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.ElectricCapacitance;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.unit.Units;
import tools.jackson.databind.json.JsonMapper;

/**
 * Factory for building {@link CableTypeInput} instances from raw entity data. The embedded JSON
 * strings of the individual cable components (conductor, layers, screen layer) are parsed with a
 * {@link CableTypeParser}.
 */
public class CableTypeInputFactory extends AssetTypeInputEntityFactory<CableTypeInput> {

  private final CableTypeParser parser;

  /** Creates a factory using a default JSON mapper for the underlying {@link CableTypeParser}. */
  public CableTypeInputFactory() {
    this(new CableTypeParser(JsonMapper.builder().build()));
  }

  /**
   * Creates a factory using the given parser for the embedded cable component JSON strings.
   *
   * @param parser parser used to build the cable components; must not be {@code null}
   */
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
    ComparableQuantity<Frequency> frequency = data.getQuantity(FREQUENCY, Units.HERTZ);
    double skinEffectCoefficient = data.getDouble(SKIN_EFF_COEFF);
    double proxEffectCoefficient = data.getDouble(PROX_EFF_COEFF);
    ComparableQuantity<ElectricCapacitance> electricalCapacitance =
        data.getQuantity(ELECTR_CAPACITANCE, Units.FARAD);
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
