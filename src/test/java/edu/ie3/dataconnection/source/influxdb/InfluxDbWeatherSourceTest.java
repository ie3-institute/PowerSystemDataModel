/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source.influxdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.dataconnectors.InfluxDbConnector;
import edu.ie3.dataconnection.source.WeatherTestEntityBuilder;
import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.models.timeseries.IndividualTimeSeries;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InfluxDbWeatherSourceTest {

  private InfluxDbWeatherSource src;

  @BeforeAll
  static void setUpOnce() {
    CsvCoordinateSource.fillCoordinateMaps();
    WeatherTestEntityBuilder.fillWeatherValues();
  }

  @BeforeEach
  void setUp() {
    InfluxDbConnector connector_in = new InfluxDbConnector("ie3_in");
    src = new InfluxDbWeatherSource(connector_in);
  }

  //    @Test //Not enough ram :(
  void getOneMonthOfAllCoordinates() { // TODO One week...
    Map<Point, IndividualTimeSeries<WeatherValues>> oneMonthOfAllCoordinates =
        src.getWeather(WeatherTestEntityBuilder.MONTH_INTERVAL);
    assertEquals((int) CsvCoordinateSource.getCoordinateCount(), oneMonthOfAllCoordinates.size());
    for (TimeBasedValue<WeatherValues> tbv : WeatherTestEntityBuilder.weatherValues) {
      WeatherValues values = tbv.getValue();
      assertEquals(
          tbv,
          oneMonthOfAllCoordinates.get(values.getCoordinate()).getTimeBasedValue(tbv.getTime()));
    }
  }

  @Test
  void getOneMonthOfMultipleCoordinate() {
    Map<Point, IndividualTimeSeries<WeatherValues>> oneMonthOfMultipleCoordinates =
        src.getWeather(
            WeatherTestEntityBuilder.MONTH_INTERVAL, WeatherTestEntityBuilder.TEST_COORDINATES);
    for (TimeBasedValue<WeatherValues> tbv : WeatherTestEntityBuilder.weatherValues) {
      WeatherValues values = tbv.getValue();
      if (WeatherTestEntityBuilder.TEST_COORDINATES.contains(values.getCoordinate())) {
        TimeBasedValue<WeatherValues> actualTbvWithoutUuid =
            oneMonthOfMultipleCoordinates
                .get(values.getCoordinate())
                .getTimeBasedValue(tbv.getTime());
        TimeBasedValue<WeatherValues> actualTbvWithUuid =
            new TimeBasedValue<>(
                WeatherTestEntityBuilder.UNSPECIFIC_UUID,
                actualTbvWithoutUuid.getTime(),
                actualTbvWithoutUuid.getValue());
        assertEquals(tbv, actualTbvWithUuid);
      } else {
        assertFalse(oneMonthOfMultipleCoordinates.containsKey(values.getCoordinate()));
      }
    }
  }

  @Test
  void getOneDateOfOneCoordinate() {
    TimeBasedValue<WeatherValues> weatherA =
        src.getWeather(
                WeatherTestEntityBuilder.MIDDLE_DATE, WeatherTestEntityBuilder.TEST_COORDINATE_A)
            .get();
    TimeBasedValue<WeatherValues> weatherAWithUuid =
        new TimeBasedValue<WeatherValues>(
            WeatherTestEntityBuilder.UNSPECIFIC_UUID, weatherA.getTime(), weatherA.getValue());
    Assertions.assertTrue(WeatherTestEntityBuilder.weatherValues.contains(weatherAWithUuid));
    TimeBasedValue<WeatherValues> weatherB =
        src.getWeather(
                WeatherTestEntityBuilder.MIDDLE_DATE, WeatherTestEntityBuilder.TEST_COORDINATE_B)
            .get();
    TimeBasedValue<WeatherValues> weatherBWithUuid =
        new TimeBasedValue<WeatherValues>(
            WeatherTestEntityBuilder.UNSPECIFIC_UUID, weatherB.getTime(), weatherB.getValue());
    Assertions.assertTrue(WeatherTestEntityBuilder.weatherValues.contains(weatherBWithUuid));
    TimeBasedValue<WeatherValues> weatherC =
        src.getWeather(
                WeatherTestEntityBuilder.MIDDLE_DATE, WeatherTestEntityBuilder.TEST_COORDINATE_C)
            .get();
    TimeBasedValue<WeatherValues> weatherCWithUuid =
        new TimeBasedValue<WeatherValues>(
            WeatherTestEntityBuilder.UNSPECIFIC_UUID, weatherC.getTime(), weatherC.getValue());
    Assertions.assertTrue(WeatherTestEntityBuilder.weatherValues.contains(weatherCWithUuid));
  }

  @Test
  public void testInvalidCoordinate() {
    Optional<TimeBasedValue<WeatherValues>> weatherInvalid =
        src.getWeather(
            WeatherTestEntityBuilder.MIDDLE_DATE, WeatherTestEntityBuilder.TEST_COORDINATE_INVALID);
    assertEquals(Optional.empty(), weatherInvalid);
  }

  @Test
  public void testInvalidDate() {
    Optional<TimeBasedValue<WeatherValues>> weatherInvalid =
        src.getWeather(
            WeatherTestEntityBuilder.INVALID_DATE, WeatherTestEntityBuilder.TEST_COORDINATE_A);
    assertEquals(Optional.empty(), weatherInvalid);
  }
}
