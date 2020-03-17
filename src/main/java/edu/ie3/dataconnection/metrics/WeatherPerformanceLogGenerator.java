/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.DataConnectorName;
import edu.ie3.dataconnection.dataconnectors.CouchbaseConnector;
import edu.ie3.dataconnection.dataconnectors.HibernateConnector;
import edu.ie3.dataconnection.dataconnectors.InfluxDbConnector;
import edu.ie3.dataconnection.source.WeatherSource;
import edu.ie3.dataconnection.source.couchbase.CouchbaseWeatherSource;
import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.dataconnection.source.hibernate.HibernateWeatherSource;
import edu.ie3.dataconnection.source.influxdb.InfluxDbWeatherSource;
import edu.ie3.models.timeseries.IndividualTimeSeries;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WeatherPerformanceLogGenerator implements PerformanceLogGenerator {

  // MIA check values
  public static final ZonedDateTime START_DATE =
      ZonedDateTime.of(2013, 4, 8, 0, 0, 0, 0, ZoneId.of("UTC"));
  public static final ZonedDateTime END_DATE =
      ZonedDateTime.of(2013, 4, 14, 23, 59, 0, 0, ZoneId.of("UTC"));
  public static final ClosedInterval<ZonedDateTime> TIME_INTERVAL =
      new ClosedInterval<>(START_DATE, END_DATE);
  public static final Collection<Point> COORDINATES =
      CsvCoordinateSource.getCoordinatesBetween(184465, 194465);

  private static int index = 0;

  private final String name;
  private final int idx = index++;
  private final Logger logger;
  private final DataConnectorName connectorName;
  private WeatherSource source;

  public WeatherPerformanceLogGenerator(DataConnectorName connectorName) {
    this.connectorName = connectorName;
    this.name = connectorName.getName() + "Weather";
    this.logger = LogManager.getLogger(name + "Logger");
  }

  @Override
  public Object[] call() {
    try {
      source = getDefaultSource(connectorName);
    } catch (Exception e) {
      mainLogger.error("Error at source creation: ", e);
      return getCsvLogParams(index, ZonedDateTime.now(), false, -1);
    }
    Object[] csvLogParams = getLogData(index);
    logger.info(name, csvLogParams);
    source.getDataConnector().shutdown();
    try {
      this.wait(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return csvLogParams;
  }

  private long measureTime() throws AssertionError {
    StopWatch watch = new StopWatch();
    mainLogger.debug("{}", String.format("%3o | %16s | Starting watch", idx, name));
    watch.start();
    Map<Point, IndividualTimeSeries<WeatherValues>> coordinateToTimeSeries = null;
    try {
      coordinateToTimeSeries = source.getWeather(TIME_INTERVAL, COORDINATES);
    } catch (Exception e) {
      mainLogger.error(e);
    } finally {
      watch.stop();
      mainLogger.debug("{}", String.format("%3o | %16s | Stopping watch", idx, name));
    }
    if (!WeatherHealthCheck.check(coordinateToTimeSeries))
      throw new HealthCheckError("Result did not succeed health check", watch.getTime());
    return watch.getTime();
  }

  private Object[] getLogData(int index) {
    long time = -1L;
    boolean succeededHealthCheck;
    ZonedDateTime start = ZonedDateTime.now();
    try {
      time = measureTime();
      succeededHealthCheck = true;
    } catch (HealthCheckError e) {
      time = e.getMeasuredTime();
      succeededHealthCheck = false;
    }
    return getCsvLogParams(idx, start, succeededHealthCheck, time);
  }

  static Object[] getCsvLogParams(
      int index, ZonedDateTime timestamp, boolean succeededHealthCheck, long time) {
    return new Object[] {index, timestamp.toLocalDateTime(), succeededHealthCheck, time};
  }

  public static WeatherSource getDefaultSource(DataConnectorName database) {
    switch (database) {
      case INFLUXDB:
        InfluxDbConnector influxdbConnector = new InfluxDbConnector("ie3_in");
        if (!influxdbConnector.isConnectionValid())
          throw new IllegalStateException(database + "Connector for weather is invalid");
        return new InfluxDbWeatherSource(influxdbConnector);
      case HIBERNATE:
        HibernateConnector hibernateConnector = new HibernateConnector("dataconnector");
        if (!hibernateConnector.isConnectionValid())
          throw new IllegalStateException(database + "Connector for weather is invalid");
        return new HibernateWeatherSource(hibernateConnector);
      case COUCHBASE:
        CouchbaseConnector couchbaseConnector = new CouchbaseConnector("ie3_in");
        if (!couchbaseConnector.isConnectionValid())
          throw new IllegalStateException(database + "Connector for weather is invalid");
        return new CouchbaseWeatherSource(couchbaseConnector);
      default:
        throw new IllegalArgumentException("Unknown connector name");
    }
  }
}
