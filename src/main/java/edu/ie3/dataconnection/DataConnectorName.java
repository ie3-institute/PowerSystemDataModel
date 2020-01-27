/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection;

import java.util.Optional;

public enum DataConnectorName {
  INFLUXDB("InfluxDb");

  private String name;

  private DataConnectorName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Optional<DataConnectorName> fromName(String name) {
    for (DataConnectorName connector : DataConnectorName.values()) {
      if (connector.getName().equals(name)) return Optional.of(connector);
    }
    return Optional.empty();
  }
}
