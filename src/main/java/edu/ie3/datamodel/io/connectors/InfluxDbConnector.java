/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;

import java.util.concurrent.TimeUnit;

public class InfluxDbConnector implements DataConnector {
  private static final String INFLUXDB_URL = "http://localhost:8086/";
  private final String databaseName;
  private final String scenarioName;

  public InfluxDbConnector(String databaseName, String scenarioName) {
    this.databaseName = databaseName;
    this.scenarioName = scenarioName;
  }

  public InfluxDbConnector(String databaseName) {
    this(databaseName, null);
  }

  public InfluxDbConnector() {
    this("ie3_in");
  }

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

  public String getScenarioName() {
    return scenarioName;
  }
}
