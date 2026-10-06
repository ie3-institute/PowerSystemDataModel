/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.util.quantities.interfaces.Irradiance;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.unit.Units;

/**
 * Factory implementation of {@link TimeBasedWeatherValueFactory}, that is able to handle field to
 * value mapping in the typical PowerSystemDataModel (PSDM) column scheme
 */
public class CosmoTimeBasedWeatherValueFactory extends TimeBasedWeatherValueFactory {

  public CosmoTimeBasedWeatherValueFactory() {
    super();
  }

  @Override
  protected TimeBasedValue<WeatherValue> buildModel(TimeBasedWeatherValueData tbd) {
    Point coordinate = tbd.getCoordinate();
    Map<String, String> data = tbd.getFieldsToAttributes();

    ZonedDateTime time = timeUtil.toZonedDateTime(getField(data, TIME));
    ComparableQuantity<Irradiance> directIrradiance =
        getQuantity(data, COSMO_DIRECT_IRRADIANCE, PowerSystemUnits.WATT_PER_SQUAREMETRE);
    ComparableQuantity<Irradiance> diffuseIrradiance =
        getQuantity(data, COSMO_DIFFUSE_IRRADIANCE, PowerSystemUnits.WATT_PER_SQUAREMETRE);
    ComparableQuantity<Temperature> temperature =
        getQuantity(data, COSMO_TEMPERATURE, Units.KELVIN).to(StandardUnits.TEMPERATURE);
    ComparableQuantity<Angle> windDirection =
        getQuantity(data, COSMO_WIND_DIRECTION, StandardUnits.WIND_DIRECTION);
    ComparableQuantity<Speed> windVelocity =
        getQuantity(data, COSMO_WIND_VELOCITY, StandardUnits.WIND_VELOCITY);
    Optional<ComparableQuantity<Temperature>> groundTemperatureLevel1 =
        getQuantityOptional(data, COSMO_GROUND_TEMPERATURE_LEVEL_1, Units.KELVIN)
            .map(quantity -> quantity.to(StandardUnits.TEMPERATURE));
    Optional<ComparableQuantity<Temperature>> groundTemperatureLevel2 =
        getQuantityOptional(data, COSMO_GROUND_TEMPERATURE_LEVEL_2, Units.KELVIN)
            .map(quantity -> quantity.to(StandardUnits.TEMPERATURE));
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
}
