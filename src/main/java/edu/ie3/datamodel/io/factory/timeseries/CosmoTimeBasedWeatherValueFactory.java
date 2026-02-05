/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.TimeUtil;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.util.quantities.interfaces.Irradiance;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

/**
 * Factory implementation of {@link TimeBasedWeatherValueFactory}, that is able to handle field to
 * value mapping in the typical PowerSystemDataModel (PSDM) column scheme
 */
public class CosmoTimeBasedWeatherValueFactory extends TimeBasedWeatherValueFactory {
    private static final String DIFFUSE_IRRADIANCE = "diffuseIrradiance";
    private static final String DIRECT_IRRADIANCE = "directIrradiance";
    private static final String TEMPERATURE = "temperature";
    private static final String WIND_DIRECTION = "windDirection";
    private static final String WIND_VELOCITY = "windVelocity";
    private static final String GROUND_TEMPERATURE_LEVEL_1 = "groundTemperatureLevel1";
    private static final String GROUND_TEMPERATURE_LEVEL_2 = "groundTemperatureLevel2";

    public CosmoTimeBasedWeatherValueFactory(TimeUtil timeUtil) {
        super(timeUtil);
    }

    public CosmoTimeBasedWeatherValueFactory(DateTimeFormatter dateTimeFormatter) {
        super(dateTimeFormatter);
    }

    public CosmoTimeBasedWeatherValueFactory() {
        super();
    }

    @Override
    protected List<Set<String>> getFields(Class<?> entityClass) {
        Set<String> minConstructorParams =
                newSet(
                        COORDINATE_ID,
                        DIFFUSE_IRRADIANCE,
                        DIRECT_IRRADIANCE,
                        TEMPERATURE,
                        WIND_DIRECTION,
                        WIND_VELOCITY);

        Set<String> withGroundTemp =
                expandSet(minConstructorParams, GROUND_TEMPERATURE_LEVEL_1, GROUND_TEMPERATURE_LEVEL_2);

        return Arrays.asList(minConstructorParams, withGroundTemp);
    }

  @Override
  protected TimeBasedValue<WeatherValue> buildModel(TimeBasedWeatherValueData data) {
    Set<String> requiredFields =
        newSet(
            TIME,
            DIFFUSE_IRRADIANCE,
            DIRECT_IRRADIANCE,
            TEMPERATURE,
            WIND_DIRECTION,
            WIND_VELOCITY);
    validatedRequiredFields(data, requiredFields);
    Point coordinate = data.getCoordinate();
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
    ComparableQuantity<Irradiance> directIrradiance =
        data.getQuantity(DIRECT_IRRADIANCE, PowerSystemUnits.WATT_PER_SQUAREMETRE);
    ComparableQuantity<Irradiance> diffuseIrradiance =
        data.getQuantity(DIFFUSE_IRRADIANCE, PowerSystemUnits.WATT_PER_SQUAREMETRE);
    ComparableQuantity<Temperature> temperature =
        data.getQuantity(TEMPERATURE, StandardUnits.TEMPERATURE);
    ComparableQuantity<Angle> windDirection =
        data.getQuantity(WIND_DIRECTION, StandardUnits.WIND_DIRECTION);
    ComparableQuantity<Speed> windVelocity =
        data.getQuantity(WIND_VELOCITY, StandardUnits.WIND_VELOCITY);
    Optional<ComparableQuantity<Temperature>> groundTemperatureLevel1 =
        data.getQuantityOptional(GROUND_TEMPERATURE_LEVEL_1, StandardUnits.TEMPERATURE);
    Optional<ComparableQuantity<Temperature>> groundTemperatureLevel2 =
        data.getQuantityOptional(GROUND_TEMPERATURE_LEVEL_2, StandardUnits.TEMPERATURE);
    WeatherValue weatherValue =
        new WeatherValue(
            coordinate,
            directIrradiance,
            diffuseIrradiance,
            temperature,
            windDirection,
            windVelocity,
            groundTemperatureLevel1,
            groundTemperatureLevel2);

    return new TimeBasedValue<>(time, weatherValue);
  }

  /**
   * * Validates that all required fields are present and not empty in the provided data
   *
   * @param data the data to validate
   * @param requiredFields the fields that must be present and non-empty
   * @throws FactoryException if any required field is missing or empty
   */
  protected void validatedRequiredFields(
      TimeBasedWeatherValueData data, Set<String> requiredFields) {
    for (String field : requiredFields) {
      String value = data.getField(field);
      if (value == null || value.isEmpty()) {
        throw new FactoryException("The field \"" + field + "\" is missing or empty.");
      }
    }
  }
}
