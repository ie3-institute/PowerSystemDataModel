/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.timeseries.IndividualTimeSeries;
import edu.ie3.models.value.WeatherValues;
import tec.uom.se.quantity.Quantities;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public class WeatherHealthCheck {

  private static final int coordinateCount = 10001; // MIA
  private static final int timeseriesLength = 720; // MIA
  private static final ZonedDateTime exampleDate =
      ZonedDateTime.of(2013, 4, 15, 9, 0, 0, 0, ZoneId.of("UTC"));
  private static final Point exampleCoordinate = CsvCoordinateSource.getCoordinate(184465); // MIA

  public static boolean check(
      Map<Point, IndividualTimeSeries<WeatherValues>> coordinateToTimeSeries) {
    if (coordinateToTimeSeries == null) return false;
    System.out.println("coordinateCount: " + coordinateToTimeSeries.keySet().size());
    System.out.println("timeseriesLength: " + coordinateToTimeSeries.get(exampleCoordinate).size());
    System.out.println("DiffuseIrradiation: " + coordinateToTimeSeries.get(exampleCoordinate).getValue(exampleDate)
            .getIrradiation()
            .getDiffuseIrradiation());
        System.out.println("Temperature: " + coordinateToTimeSeries.get(exampleCoordinate).getValue(exampleDate)
                        .getTemperature()
                        .getTemperature());
        System.out.println("Velocity: " + coordinateToTimeSeries.get(exampleCoordinate).getValue(exampleDate)
                .getWind()
                .getVelocity());

    if (coordinateToTimeSeries.keySet().size() != coordinateCount) return false;
    if (coordinateToTimeSeries.values().stream()
        .anyMatch(timeseries -> timeseries.size() != timeseriesLength)) return false;
    WeatherValues value = coordinateToTimeSeries.get(exampleCoordinate).getValue(exampleDate);
    if (!value
        .getIrradiation()
        .getDiffuseIrradiation()
        .equals(Quantities.getQuantity(153.503005981445, StandardUnits.IRRADIATION)))
      return false; // MIA
    if (!value
        .getTemperature()
        .getTemperature()
        .equals(Quantities.getQuantity(291.019012451172, StandardUnits.TEMPERATURE)))
      return false; // MIA
    return value
        .getWind()
        .getVelocity()
        .equals(Quantities.getQuantity(4.08193159103394, StandardUnits.WIND_VELOCITY)); // MIA
  }
}
