package edu.ie3.dataconnection.source.influxdb;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.dataconnectors.InfluxDbConnector;
import edu.ie3.models.timeseries.IndividualTimeSeries;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.interval.ClosedInterval;
import edu.ie3.utils.CoordinateTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class InfluxDbWeatherSourceTest {
    static final ZonedDateTime start = ZonedDateTime.of(2013, 4, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
    static final ZonedDateTime middle = ZonedDateTime.of(2013, 4, 14, 12, 0, 0, 0, ZoneId.of("UTC"));
    static final ZonedDateTime end = ZonedDateTime.of(2013, 4, 30, 23, 59, 0, 0, ZoneId.of("UTC"));
    static final ClosedInterval<ZonedDateTime> month = new ClosedInterval<>(start, end);
    static final Point coordinateA = CoordinateTools.xyCoordToPoint(39.602772000000002, 1.279336);
    static final Point coordinateB = CoordinateTools.xyCoordToPoint(39.610000999999997, 1.358673);
    static final Point coordinateC = CoordinateTools.xyCoordToPoint(39.990299, 8.6342192000000004);
    static final List<Point> coordinates = Arrays.asList(coordinateA, coordinateB, coordinateC);

    static Map<Point, IndividualTimeSeries<WeatherValues>> oneMonthOfAllCoordinates;
    static Map<Point, IndividualTimeSeries<WeatherValues>> oneMonthOfMultipleCoordinates;
    InfluxDbWeatherSource src;
    private Optional<TimeBasedValue<WeatherValues>> weatherA;
    private Optional<TimeBasedValue<WeatherValues>> weatherB;
    private Optional<TimeBasedValue<WeatherValues>> weatherC;

    @BeforeEach
    void setUp() {
        InfluxDbConnector connector_in = new InfluxDbConnector("ie3_in");
        src = new InfluxDbWeatherSource(connector_in);
    }

    @Test
    @Order(1)
    void getOneMonthOfAllCoordinates() {
        oneMonthOfAllCoordinates = src.getWeather(month);
    }

    @Test
    @Order(2)
    void getOneMonthOfOneCoordinate() {
        oneMonthOfMultipleCoordinates = src.getWeather(month, coordinates);
    }
    @Test
    @Order(3)
    void getOneDateOfOneCoordinate() {
        weatherA = src.getWeather(middle, coordinateA);
        weatherB = src.getWeather(middle, coordinateB);
        weatherC = src.getWeather(middle, coordinateC);
    }




}