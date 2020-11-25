/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory.*;
import static tech.units.indriya.unit.Units.HOUR;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.TimeUtil;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.util.quantities.interfaces.EnergyDensity;
import edu.ie3.util.quantities.interfaces.Irradiation;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.*;

import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public class TimeBasedWeatherValueFactory
    extends TimeBasedValueFactory<TimeBasedWeatherValueData, WeatherValue> {

  private static final String UUID = "uuid";
  private static final String TIME = "time";
  private static final  Quantity<Time> DEFAULT_RESOLUTION = Quantities.getQuantity(1, HOUR);

  private final TimeUtil timeUtil;
  private final Quantity<Time> resolution;

  public TimeBasedWeatherValueFactory() {
    this("yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'");
  }

  public TimeBasedWeatherValueFactory(String timePattern) {
    this(new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, timePattern));
  }

  public TimeBasedWeatherValueFactory(TimeUtil timeUtil) {
    this(timeUtil, DEFAULT_RESOLUTION);
  }

  public TimeBasedWeatherValueFactory(TimeUtil timeUtil, Quantity<Time> resolution) {
    super(WeatherValue.class);
    this.timeUtil = timeUtil;
    this.resolution = resolution;
  }

  @Override
  protected List<Set<String>> getFields(TimeBasedWeatherValueData data) {
    Set<String> minConstructorParams =
        newSet(
            UUID,
            TIME,
            DIFFUSE_IRRADIATION,
            DIRECT_IRRADIATION,
            TEMPERATURE,
            WIND_DIRECTION,
            WIND_VELOCITY);
    return Collections.singletonList(minConstructorParams);
  }

  @Override
  protected TimeBasedValue<WeatherValue> buildModel(TimeBasedWeatherValueData data) {
    Point coordinate = data.getCoordinate();
    UUID uuid = data.getUUID(UUID);
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
    ComparableQuantity<Irradiation> directIrradiation = toIrradiation(
        data.getQuantity(DIRECT_IRRADIATION, StandardUnits.ENERGY_DENSITY), resolution);
    ComparableQuantity<Irradiation> diffuseIrradiation =toIrradiation(
            data.getQuantity(DIFFUSE_IRRADIATION, StandardUnits.ENERGY_DENSITY), resolution);
    ComparableQuantity<Temperature> temperature =
        data.getQuantity(TEMPERATURE, StandardUnits.TEMPERATURE);
    ComparableQuantity<Angle> windDirection =
        data.getQuantity(WIND_DIRECTION, StandardUnits.WIND_DIRECTION);
    ComparableQuantity<Speed> windVelocity =
        data.getQuantity(WIND_VELOCITY, StandardUnits.WIND_VELOCITY);
    WeatherValue weatherValue =
        new WeatherValue(
            coordinate,
            directIrradiation,
            diffuseIrradiation,
            temperature,
            windDirection,
            windVelocity);
    return new TimeBasedValue<>(uuid, time, weatherValue);
  }

  /**
   * Converts an energy density quantity to an irradiation quantity, using the resolution to determine the time frame that the energy density represents
   * @param energyDensity the energy density to convert
   * @param resolution the resolution of the data
   * @return a quantity of type irradiation
   */
  private static ComparableQuantity<Irradiation> toIrradiation(Quantity<EnergyDensity> energyDensity, Quantity<Time> resolution) {
    return (ComparableQuantity<Irradiation>) energyDensity.to(PowerSystemUnits.KILOWATTHOUR_PER_SQUAREMETRE).divide(resolution.to(HOUR)).asType(Irradiation.class).to(StandardUnits.IRRADIATION);
  }

}
