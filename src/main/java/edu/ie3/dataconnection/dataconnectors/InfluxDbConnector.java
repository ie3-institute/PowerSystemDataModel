/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.dataconnectors;

import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;

public class InfluxDbConnector implements DataConnector {
  private static Logger mainLogger = LogManager.getLogger("Main");
  private static final String INFLUXDB_URL = "http://localhost:8086/";
  private String databaseName = "ie3_in";

  public InfluxDbConnector(String databaseName) {
    this.databaseName = databaseName;
  }

  public InfluxDbConnector() {}

  @Override
  public Boolean isConnectionValid() {
    InfluxDB session = getSession();
    if (session == null) return false;
    Pong response = session.ping();
    session.close();
    if (response.getVersion().equalsIgnoreCase("unknown")) {
      return false;
    }
    return true;
  }

  @Override
  public void shutdown() {
    if (databaseName.endsWith("out")) deleteOutput();
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public InfluxDB getSession() {
    InfluxDB session;
    session = InfluxDBFactory.connect(INFLUXDB_URL);
    session.setDatabase(databaseName);
    session.query(new Query("CREATE DATABASE " + databaseName, databaseName));
    session.setLogLevel(InfluxDB.LogLevel.NONE);
    session.enableBatch(100000, 5, TimeUnit.SECONDS);
    return session;
  }

  private void deleteOutput() {
    try (InfluxDB session = getSession()) {
      session.query(new Query("DELETE FROM line_result", databaseName));
    }
  }
}
