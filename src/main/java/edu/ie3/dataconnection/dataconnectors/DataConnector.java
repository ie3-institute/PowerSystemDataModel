/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.dataconnectors;

import edu.ie3.dataconnection.DataConnectorName;

/** Describes a class that is used to establish a connection to a data location */
public interface DataConnector {

    public static DataConnector build(String configJson) {
        // MIA richtiges string parse
        String connectorName = "InfluxDb";
        DataConnectorName dataConnectorName =
                DataConnectorName.fromName(connectorName)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown connector name"));
        return build(dataConnectorName);
    }

    public static DataConnector build(DataConnectorName connectorName) {
        switch (connectorName) {
            case INFLUXDB:
                return new InfluxDbConnector("ie3");
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    Boolean isConnectionValid();

    void shutdown();
}
