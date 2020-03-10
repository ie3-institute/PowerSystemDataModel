/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics.couchbase;

import edu.ie3.dataconnection.dataconnectors.CouchbaseConnector;
import edu.ie3.dataconnection.metrics.WeatherTimeMetricLogger;
import edu.ie3.dataconnection.source.couchbase.CouchbaseWeatherSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CouchbaseWeatherTimeMetricLogger
    implements WeatherTimeMetricLogger<CouchbaseWeatherSource> {

  static Logger couchbaseWeatherLogger = LogManager.getLogger("couchbaseWeatherLogger");
  static int index = 0;
  private final CouchbaseWeatherSource source;

  private CouchbaseConnector connector; //    = new InfluxDbConnector("ie3_in");

  public CouchbaseWeatherTimeMetricLogger(CouchbaseConnector connector) {
    index++;
    this.connector = connector;
    this.source = new CouchbaseWeatherSource(connector);
  }

  @Override
  public CouchbaseWeatherSource getSource() {
    return source;
  }

  @Override
  public void logAndMeasureWeatherTime() {
    couchbaseWeatherLogger.info("couchbase weather", getWeatherTimeLog(index));
  }
}
