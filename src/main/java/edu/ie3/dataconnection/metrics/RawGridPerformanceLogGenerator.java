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
import edu.ie3.dataconnection.dataconnectors.Neo4JConnector;
import edu.ie3.dataconnection.source.RawGridSource;
import edu.ie3.dataconnection.source.couchbase.CouchbaseRawGridSource;
import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.dataconnection.source.hibernate.HibernateRawGridSource;
import edu.ie3.dataconnection.source.neo4j.Neo4JRawGridSource;
import edu.ie3.models.input.aggregated.AggregatedRawGridInput;
import edu.ie3.util.interval.ClosedInterval;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;

public class RawGridPerformanceLogGenerator implements PerformanceLogGenerator {

  // MIA check values
  private static final ZonedDateTime START_DATE =
      ZonedDateTime.of(2013, 4, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
  private static final ZonedDateTime END_DATE =
      ZonedDateTime.of(2013, 4, 7, 23, 59, 0, 0, ZoneId.of("UTC"));
  private static final ClosedInterval<ZonedDateTime> TIME_INTERVAL =
      new ClosedInterval<>(START_DATE, END_DATE);
  private static final Collection<Point> COORDINATES =
      CsvCoordinateSource.getCoordinatesBetween(10000, 20000);

  private static int index = 0;

  private final String name;
  private final Logger logger;
  private final DataConnectorName connectorName;
  private RawGridSource source;

  public RawGridPerformanceLogGenerator(DataConnectorName connectorName) {
    this.connectorName = connectorName;
    this.name = connectorName.getName() + "Grid";
    this.logger = LogManager.getLogger(name + "Logger");
    index++;
  }

  @Override
  public Object[] call() {
    source = getDefaultSource(connectorName);
    Object[] csvLogParams = getLogData(index);
    logger.info(name, csvLogParams);
    source.getDataConnector().shutdown();
    return csvLogParams;
  }

  private long measureTime() throws AssertionError {
    StopWatch watch = new StopWatch();
    mainLogger.debug("{}", String.format("%3o | %16s | Starting watch", index, name));
    watch.start();
    AggregatedRawGridInput gridData = null;
    try {
      gridData = source.getGridData();
    } catch (Exception e) {
      mainLogger.error(e);
    } finally {
      watch.stop();
      mainLogger.debug("{}", String.format("%3o | %16s | Stopping watch", index, name));
    }
    if (!RawGridHealthCheck.check(gridData))
      throw new AssertionError("Result did not succeed health check");
    return watch.getTime();
  }

  private Object[] getLogData(int index) {
    long time = -1L;
    boolean succeededHealthCheck;
    ZonedDateTime start = ZonedDateTime.now();
    try {
      time = measureTime();
      succeededHealthCheck = true;
    } catch (AssertionError e) {
      succeededHealthCheck = false;
    }
    return getCsvLogParams(index, start, succeededHealthCheck, time);
  }

  static Object[] getCsvLogParams(
      int index, ZonedDateTime timestamp, boolean succeededHealthCheck, long time) {
    return new Object[] {index, timestamp.toLocalDateTime(), succeededHealthCheck, time};
  }

  public static RawGridSource getDefaultSource(DataConnectorName database) {
    switch (database) {
      case NEO4J:
        Neo4JConnector neo4JConnector = new Neo4JConnector();
        return new Neo4JRawGridSource(neo4JConnector);
      case HIBERNATE:
        HibernateConnector hibernateConnector = new HibernateConnector("dataconnector");
        return new HibernateRawGridSource(hibernateConnector);
      case COUCHBASE:
        CouchbaseConnector couchbaseConnector = new CouchbaseConnector("ie3_in");
        return new CouchbaseRawGridSource(couchbaseConnector, "vn_simona");
      default:
        throw new IllegalArgumentException("Unknown connector name");
    }
  }
}
