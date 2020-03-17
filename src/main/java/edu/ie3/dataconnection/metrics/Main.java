/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.metrics;

import edu.ie3.dataconnection.DataConnectorName;
import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.dataconnection.source.csv.CsvTypeSource;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

  static Logger logger = LogManager.getLogger("Main");

  private static int numberOfThreads = 1;
  private static int numberOfAttempts = 5;
  private static boolean measureWeather = true;
  private static boolean measureRawGrid = false;
  private static boolean measureOutput = false;
  private static boolean useHibernate = false;
  private static boolean useInfluxDb = false;
  private static boolean useCouchbase = false;
  private static boolean useNeo4j = false;

  private static ExecutorService taskExecutor;

  public static void main(String[] args) {
    if (args.length > 0) numberOfThreads = Integer.parseInt(args[0]);
    if (args.length > 1) numberOfAttempts = Integer.parseInt(args[1]);
    if (args.length > 2) {
      measureWeather = args[2].contains("w");
      measureRawGrid = args[2].contains("g");
      measureOutput = args[2].contains("o");
    }
    if (args.length > 3) {
      useHibernate = args[3].contains("h");
      useInfluxDb = args[3].contains("i");
      useCouchbase = args[3].contains("c");
      useNeo4j = args[3].contains("n");
    }
    numberOfAttempts /= 8;
    taskExecutor = Executors.newFixedThreadPool(numberOfThreads);
    logger.info("Version: " + 1.2);
    logger.info(
        "\n"
            + "numberOfThreads: "
            + numberOfThreads
            + "\n"
            + "numberOfAttempts: "
            + numberOfAttempts
            + "\n"
            + "measureWeather: "
            + measureWeather
            + "\n"
            + "measureRawGrid: "
            + measureRawGrid
            + "\n"
            + "measureOutput: "
            + measureOutput
            + "\n"
            + "useHibernate: "
            + useHibernate
            + "\n"
            + "useInfluxDb: "
            + useInfluxDb
            + "\n"
            + "useCouchbase: "
            + useCouchbase
            + "\n"
            + "useNeo4j: "
            + useNeo4j);

    CsvCoordinateSource.fillCoordinateMaps();
    CsvTypeSource.fillMaps();

    for (int i = 0; i < numberOfAttempts; i++) {
      if (measureWeather) getWeatherPerformance();
      if (measureRawGrid) getRawGridPerformance();
      if (measureOutput) getOutputPerformance();
    }
    taskExecutor.shutdownNow();
  }

  public static void getWeatherPerformance() {
    if (useHibernate) getWeatherPerformance(DataConnectorName.HIBERNATE);
    if (useInfluxDb) getWeatherPerformance(DataConnectorName.INFLUXDB);
    if (useCouchbase) getWeatherPerformance(DataConnectorName.COUCHBASE);
  }

  private static void getWeatherPerformance(DataConnectorName connectorName) {
    ArrayList<Callable<Object[]>> tasks = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      tasks.add(new WeatherPerformanceLogGenerator(connectorName));
    }
    try {
      taskExecutor.invokeAll(tasks);
    } catch (InterruptedException e) {
      logger.error(
          "Error during task execution for " + connectorName.getName() + "WeatherPerfomance");
      Thread.currentThread().interrupt();
    }
  }

  public static void getRawGridPerformance() {
    if (useHibernate) getRawGridPerformance(DataConnectorName.HIBERNATE);
    if (useCouchbase) getRawGridPerformance(DataConnectorName.COUCHBASE);
    if (useNeo4j) getRawGridPerformance(DataConnectorName.NEO4J);
  }

  private static void getRawGridPerformance(DataConnectorName connectorName) {
    ArrayList<Callable<Object[]>> tasks = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      tasks.add(new RawGridPerformanceLogGenerator(connectorName));
    }
    try {
      taskExecutor.invokeAll(tasks);
    } catch (InterruptedException e) {
      logger.error(
          "Error during task execution for " + connectorName.getName() + "RawGridPerfomance");
      Thread.currentThread().interrupt();
    }
  }

  public static void getOutputPerformance() {
    if (useHibernate) getOutputPerformance(DataConnectorName.HIBERNATE);
    if (useInfluxDb) getOutputPerformance(DataConnectorName.INFLUXDB);
    if (useCouchbase) getOutputPerformance(DataConnectorName.COUCHBASE);
  }

  private static void getOutputPerformance(DataConnectorName connectorName) {
    ArrayList<Callable<Object[]>> tasks = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      tasks.add(new OutputPerformanceLogGenerator(connectorName));
    }
    try {
      taskExecutor.invokeAll(tasks);
    } catch (InterruptedException e) {
      logger.error(
          "Error during task execution for " + connectorName.getName() + "OutputPerfomance");
      Thread.currentThread().interrupt();
    }
  }
}
