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

  private static ExecutorService taskExecutor = Executors.newFixedThreadPool(numberOfThreads);

  public static void main(String[] args) {
    if (args.length > 0) numberOfThreads = Integer.parseInt(args[0]);
    if (args.length > 1) numberOfAttempts = Integer.parseInt(args[1]);
    if (args.length > 2) {
      measureWeather = args[2].contains("w");
      measureRawGrid = args[2].contains("g");
      measureOutput = args[2].contains("o");
    }
    numberOfAttempts /= 5;

    CsvCoordinateSource.fillCoordinateMaps();
    CsvTypeSource.fillMaps();

    for (int i = 0; i < 5; i++) {
      if (measureWeather) getWeatherPerformance();
      if (measureRawGrid) getRawGridPerformance();
      if (measureOutput) getOutputPerformance();
    }
    taskExecutor.shutdownNow();
  }

  public static void getWeatherPerformance() {
    getWeatherPerformance(DataConnectorName.HIBERNATE);
    getWeatherPerformance(DataConnectorName.INFLUXDB);
    getWeatherPerformance(DataConnectorName.COUCHBASE);
  }

  private static void getWeatherPerformance(DataConnectorName connectorName) {
    ArrayList<Callable<Object[]>> tasks = new ArrayList<>();
    for (int i = 0; i < numberOfAttempts; i++) {
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
    getRawGridPerformance(DataConnectorName.HIBERNATE);
    getRawGridPerformance(DataConnectorName.COUCHBASE);
    getRawGridPerformance(DataConnectorName.NEO4J);
  }

  private static void getRawGridPerformance(DataConnectorName connectorName) {
    ArrayList<Callable<Object[]>> tasks = new ArrayList<>();
    for (int i = 0; i < numberOfAttempts; i++) {
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
    getOutputPerformance(DataConnectorName.HIBERNATE);
    getOutputPerformance(DataConnectorName.INFLUXDB);
    getOutputPerformance(DataConnectorName.COUCHBASE);
  }

  private static void getOutputPerformance(DataConnectorName connectorName) {
    ArrayList<Callable<Object[]>> tasks = new ArrayList<>();
    for (int i = 0; i < numberOfAttempts; i++) {
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
