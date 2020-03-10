/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics.couchbase;
//
// import edu.ie3.dataconnection.dataconnectors.CouchbaseConnector;
// import edu.ie3.dataconnection.metrics.OutputPerformanceLogGenerator;
// import edu.ie3.dataconnection.sink.CouchbaseDataSink;
// import edu.ie3.models.result.ResultEntity;
// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
//
// import java.util.Collection;
//
// public class CouchbaseOutputTimeMetricLogger implements
// OutputPerformanceLogGenerator<CouchbaseDataSink> {
//
//    static Logger couchbaseOutputLogger = LogManager.getLogger("couchbaseOutputLogger");
//    static int index = 0;
//    private final CouchbaseDataSink sink;
//
//    private CouchbaseConnector connector; //    = new CouchbaseConnector("ie3_out");
//
//    public CouchbaseOutputTimeMetricLogger(CouchbaseConnector connector) {
//        index++;
//        this.connector = connector;
//        this.sink = new CouchbaseDataSink(connector);
//    }
//
//    @Override
//    public CouchbaseDataSink getSink() {
//        return sink;
//    }
//
//    @Override
//    public void logAndMeasureOutputTime(Collection<? extends ResultEntity> resultEntities) {
//        couchbaseOutputLogger.info("influxb output", getOutputTimeLog(index, resultEntities));
//        connector.shutdown();
//    }
//
// }
