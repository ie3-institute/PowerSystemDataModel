/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics.influxdb;
//
// import edu.ie3.dataconnection.dataconnectors.InfluxDbConnector;
// import edu.ie3.dataconnection.metrics.OutputPerformanceLogGenerator;
// import edu.ie3.dataconnection.sink.InfluxDbDataSink;
// import edu.ie3.models.result.ResultEntity;
// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
//
// import java.util.Collection;
//
// public class InfluxDbOutputTimeMetricLogger implements
// OutputPerformanceLogGenerator<InfluxDbDataSink> {
//
//    static Logger influxdbOutputLogger = LogManager.getLogger("influxdbOutputLogger");
//    static int index = 0;
//    private final InfluxDbDataSink sink;
//
//    private InfluxDbConnector connector; //    = new InfluxDbConnector("ie3_out");
//
//    public InfluxDbOutputTimeMetricLogger(InfluxDbConnector connector) {
//        index++;
//        this.connector = connector;
//        this.sink = new InfluxDbDataSink(connector);
//    }
//
//    @Override
//    public InfluxDbDataSink getSink() {
//        return sink;
//    }
//
//    @Override
//    public void logAndMeasureOutputTime(Collection<? extends ResultEntity> resultEntities) {
//        influxdbOutputLogger.info("influxb output", getOutputTimeLog(index, resultEntities));
//    }
// }
