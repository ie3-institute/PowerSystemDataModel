/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics;

import edu.ie3.dataconnection.DataConnectorName;
import edu.ie3.dataconnection.dataconnectors.CouchbaseConnector;
import edu.ie3.dataconnection.dataconnectors.HibernateConnector;
import edu.ie3.dataconnection.dataconnectors.InfluxDbConnector;
import edu.ie3.dataconnection.sink.CouchbaseDataSink;
import edu.ie3.dataconnection.sink.DataSink;
import edu.ie3.dataconnection.sink.HibernateSink;
import edu.ie3.dataconnection.sink.InfluxDbDataSink;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.result.ResultEntity;
import edu.ie3.models.result.connector.LineResult;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tec.uom.se.quantity.Quantities;

public class OutputPerformanceLogGenerator implements PerformanceLogGenerator {

  private static final Random random = new Random();
  private static final int numberOfEntities = 10000;
  private static final List<UUID> lineUUIDs =
      Arrays.asList(
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID());
  private static final UUID comparisonUUID =
      UUID.fromString("11111111-2222-3333-4444-555555555555");

  private static int index = 0;

  private final String name;
  private final Logger logger;
  private final DataConnectorName connectorName;
  private DataSink sink;
  private Collection<LineResult> resultEntities;

  public OutputPerformanceLogGenerator(DataConnectorName connectorName) {
    this.connectorName = connectorName;
    this.name = connectorName.getName() + "Output";
    this.logger = LogManager.getLogger(name + "Logger");
    index++;
  }

  @Override
  public Object[] call() {
    sink = getDefaultSink(connectorName);
    Object[] csvLogParams = getLogData(index, generateResultEntities());
    logger.info(name, csvLogParams);
    sink.getDataConnector().shutdown();
    return csvLogParams;
  }

  private long measureTime(Collection<? extends ResultEntity> resultEntities) {
    StopWatch watch = new StopWatch();

    mainLogger.debug("{}", String.format("%3o | %16s | Starting watch", index, name));
    watch.start();
    try {
      sink.persistAll(resultEntities);
    } catch (Exception e) {
      mainLogger.error(e);
    } finally {
      watch.stop();
      mainLogger.debug("{}", String.format("%3o | %16s | Stopping watch", index, name));
    }
    return watch.getTime();
  }

  private Object[] getLogData(int index, Collection<? extends ResultEntity> resultEntities) {
    long time = -1L;
    boolean succeededHealthCheck;
    ZonedDateTime start = ZonedDateTime.now();
    try {
      time = measureTime(resultEntities);
      succeededHealthCheck = true;
    } catch (AssertionError e) {
      succeededHealthCheck = false;
    }
    return getCsvLogParams(index, start, succeededHealthCheck, time);
  }

  private static Object[] getCsvLogParams(
      int index, ZonedDateTime timestamp, boolean succeededHealthCheck, long time) {
    return new Object[] {index, timestamp.toLocalDateTime(), succeededHealthCheck, time};
  }

  public static Collection<LineResult> generateResultEntities() {
    Set<LineResult> resultEntities = new HashSet<>(numberOfEntities);
    for (int i = 0; i < numberOfEntities - 1; i++) {
      Quantity<ElectricCurrent> iAMa =
          Quantities.getQuantity(generateDoubleValue(), StandardUnits.CURRENT);
      Quantity<Angle> iAAng =
          Quantities.getQuantity(generateDoubleValue(), StandardUnits.ELECTRIC_CURRENT_ANGLE);
      Quantity<ElectricCurrent> iBMag =
          Quantities.getQuantity(generateDoubleValue(), StandardUnits.CURRENT);
      Quantity<Angle> iBAng =
          Quantities.getQuantity(generateDoubleValue(), StandardUnits.ELECTRIC_CURRENT_ANGLE);
      LineResult lineResult =
          new LineResult(
              ZonedDateTime.now(), lineUUIDs.get(random.nextInt(10)), iAMa, iAAng, iBMag, iBAng);
      resultEntities.add(lineResult);
    }

    // Add a comparison object
    Quantity<ElectricCurrent> iAMa = null;
    Quantity<Angle> iAAng = Quantities.getQuantity(1, StandardUnits.ELECTRIC_CURRENT_ANGLE);
    Quantity<ElectricCurrent> iBMag = Quantities.getQuantity(-2d, StandardUnits.CURRENT);
    Quantity<Angle> iBAng =
        Quantities.getQuantity(3.3333333333333333, StandardUnits.ELECTRIC_CURRENT_ANGLE);
    LineResult lineResult =
        new LineResult(ZonedDateTime.now(), comparisonUUID, iAMa, iAAng, iBMag, iBAng);
    resultEntities.add(lineResult);

    return resultEntities;
  }

  /** generates a Double Value higher than 1.0 */
  private static Double generateDoubleValue() {
    return random.nextInt() + random.nextDouble();
  }

  public static DataSink getDefaultSink(DataConnectorName database) {
    switch (database) {
      case INFLUXDB:
        InfluxDbConnector influxdbConnector = new InfluxDbConnector("ie3_out");
        return new InfluxDbDataSink(influxdbConnector);
      case HIBERNATE:
        HibernateConnector hibernateConnector = new HibernateConnector("dataconnector");
        return new HibernateSink(hibernateConnector);
      case COUCHBASE:
        CouchbaseConnector couchbaseConnector = new CouchbaseConnector("ie3_out");
        return new CouchbaseDataSink(couchbaseConnector);
      default:
        throw new IllegalArgumentException("Unknown connector name");
    }
  }
}
