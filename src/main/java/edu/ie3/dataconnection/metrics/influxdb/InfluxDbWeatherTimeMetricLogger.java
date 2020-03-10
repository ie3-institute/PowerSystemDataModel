/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics.influxdb;

import edu.ie3.dataconnection.dataconnectors.InfluxDbConnector;
import edu.ie3.dataconnection.metrics.WeatherTimeMetricLogger;
import edu.ie3.dataconnection.source.influxdb.InfluxDbWeatherSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InfluxDbWeatherTimeMetricLogger
    implements WeatherTimeMetricLogger<InfluxDbWeatherSource> {

  static Logger influxdbWeatherLogger = LogManager.getLogger("influxdbWeatherLogger");
  static int index = 0;
  private final InfluxDbWeatherSource source;

  private InfluxDbConnector connector; //    = new InfluxDbConnector("ie3_in");

  public InfluxDbWeatherTimeMetricLogger(InfluxDbConnector connector) {
    index++;
    this.connector = connector;
    this.source = new InfluxDbWeatherSource(connector);
  }

  @Override
  public InfluxDbWeatherSource getSource() {
    return source;
  }

  @Override
  public void logAndMeasureWeatherTime() {
    influxdbWeatherLogger.info("influxdb weather", getWeatherTimeLog(index));
  }
}
