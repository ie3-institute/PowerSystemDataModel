/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.typeinput.parser.CableTypeParser;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.connector.type.CableTypeInput;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import edu.ie3.util.quantities.interfaces.SpecificCapacitance;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.measure.Unit;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.unit.ProductUnit;
import tech.units.indriya.unit.Units;
import tools.jackson.databind.json.JsonMapper;

/**
 * Factory for building {@link CableTypeInput} instances from raw entity data. The embedded JSON
 * strings of the individual cable components (conductor, layers, screen layer) are parsed with a
 * {@link CableTypeParser}.
 */
public class CableTypeInputFactory
    extends AssetTypeInputEntityFactory<CableTypeInput, CableTypeInput> {

  private static final Unit<SpecificCapacitance> FARAD_PER_METRE =
      new ProductUnit<>(Units.FARAD.divide(Units.METRE));

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
  protected CableTypeInput buildModel(Map<String, String> data) {
    String id = getID(data);
    int cores = getInt(data, CORE_NUMBER);

    final ConductorInput conductor;
    final List<LayerInput> isolation;
    final ScreenLayerInput screen;
    final List<LayerInput> filler;
    final List<LayerInput> armor;
    final List<LayerInput> jack;

    try {
      conductor = parser.parseConductor(getField(data, CONDUCTOR_STRING));
      isolation = parser.parseLayerList(getField(data, ISOLATION_STRING));
      screen = parser.parseScreenLayer(getField(data, SCREEN_STRING));
      filler = parser.parseLayerList(getField(data, FILLER_STRING));
      armor = parser.parseLayerList(getField(data, ARMOR_STRING));
      jack = parser.parseLayerList(getField(data, JACK_STRING));
    } catch (ParsingException e) {
      throw new IllegalArgumentException(
          "Cannot build CableTypeInput '"
              + id
              + "': invalid cable component JSON. Caused by: "
              + e.getMessage(),
          e);
    }

    ComparableQuantity<Temperature> limitTemp =
        getQuantity(data, LIMIT_TEMPERATURE, StandardUnits.TEMPERATURE);
    ComparableQuantity<Frequency> frequency = getQuantity(data, FREQUENCY, Units.HERTZ);
    double skinEffectCoefficient = getDouble(data, SKIN_EFFECT_COEFFICIENT);
    double proxEffectCoefficient = getDouble(data, PROXIMITY_EFFECT_COEFFICIENT);
    ComparableQuantity<SpecificCapacitance> electricalCapacitance =
        getQuantity(data, ELECTRICAL_CAPACITANCE, FARAD_PER_METRE);
    double tanDelta = getDouble(data, TAN_DELTA);
    double circulatingLossFactor = getDouble(data, CIRCULATING_LOSS_FACTOR);
    double eddyCurrentLossFactor = getDouble(data, EDDY_CURRENT_LOSS_FACTOR);

    return new CableTypeInput(
        getUUID(data),
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
