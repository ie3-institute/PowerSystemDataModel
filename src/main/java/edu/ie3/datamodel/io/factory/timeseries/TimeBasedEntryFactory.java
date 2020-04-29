/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.TimeUtil;
import edu.ie3.util.quantities.interfaces.Irradiation;
import org.locationtech.jts.geom.Point;
import tec.uom.se.ComparableQuantity;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TimeBasedEntryFactory extends EntityFactory<TimeBasedValue, TimeBasedEntryData> {

  private static final TimeUtil timeUtil =
      new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd'T'HH:mm:ss'Z'");

  private static final String TIME = "time";
  // weather
  private static final String COORDINATE = "coordinate";
  private static final String DIFFUSE_IRRADIATION = "diffuse_irradiation";
  private static final String DIRECT_IRRADIATION = "direct_irradiation";
  private static final String TEMPERATURE = "temperature";
  private static final String WIND_DIRECTION = "wind_direction";
  private static final String WIND_VELOCITY = "wind_velocity";

  public TimeBasedEntryFactory() {
    super(TimeBasedValue.class);
  }

  @Override
  protected List<Set<String>> getFields(TimeBasedEntryData data) {
    Set<String> minConstructorParams = newSet(TIME);
    final Class<? extends Value> valueClass = data.getValueClass();
    if (valueClass.equals(WeatherValue.class)) {
      minConstructorParams =
          newSet(
              TIME,
              COORDINATE,
              DIFFUSE_IRRADIATION,
              DIRECT_IRRADIATION,
              TEMPERATURE,
              WIND_DIRECTION,
              WIND_VELOCITY);
    }
    return Collections.singletonList(minConstructorParams);
  }

  @Override
  protected TimeBasedValue buildModel(TimeBasedEntryData data) {
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
    final Class<? extends Value> valueClass = data.getValueClass();
    if (valueClass.equals(WeatherValue.class)) {
      Point coordinate = null; // TODO How do I introduce this properly?
      ComparableQuantity<Irradiation> directIrradiation =
          data.getQuantity(DIRECT_IRRADIATION, StandardUnits.IRRADIATION);
      ComparableQuantity<Irradiation> diffuseIrradiation =
          data.getQuantity(DIFFUSE_IRRADIATION, StandardUnits.IRRADIATION);
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
      return new TimeBasedValue<>(time, weatherValue);
    } else throw new FactoryException("Cannot process " + valueClass.getSimpleName() + ".class.");
  }
}
