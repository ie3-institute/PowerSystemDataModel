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
import org.influxdb.dto.QueryResult;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfluxDbConnector implements DataConnector {
    /** Merges two sets with (fieldName -> fieldValue) maps */
    private static final BinaryOperator<Set<Map<String, String>>> mergeSets = (maps, maps2) -> {
        maps.addAll(maps2);
        return maps;
    };

    private static final String INFLUXDB_URL = "http://localhost:8086/";
    private final String databaseName;
    private final String scenarioName;
    private final String url;

    public InfluxDbConnector(String url, String databaseName, String scenarioName) {
        this.url = url;
        this.databaseName = databaseName;
        this.scenarioName = scenarioName;
    }

    public InfluxDbConnector(String url, String databaseName) {
        this(url, databaseName, null);
    }

    public InfluxDbConnector() {
        this(INFLUXDB_URL, "ie3_in");
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

    public static Map<String,Set<Map<String, String>>> parseQueryResult(QueryResult queryResult, String... measurementNames){
        HashMap<String,Set<Map<String, String>>> measurementToFields = new HashMap<>();
        queryResult.getResults().stream().map(result -> parseResult(result, measurementNames)).forEach(measurementMap ->
        {
            for (Map.Entry<String, Set<Map<String, String>>> measurementEntry : measurementMap.entrySet()) {
                if(measurementToFields.containsKey(measurementEntry.getKey())){
                    measurementToFields.get(measurementEntry.getKey()).addAll(measurementEntry.getValue());
                } else measurementToFields.put(measurementEntry.getKey(), measurementEntry.getValue());
            }
        });
        return measurementToFields;
    }

    public static Map<String, Set<Map<String, String>>> parseResult(QueryResult.Result result, String... measurementNames) {
        Stream<QueryResult.Series> seriesStream = result.getSeries().stream();
        if(measurementNames.length>0){
            seriesStream = seriesStream.filter(series -> Arrays.asList(measurementNames).contains(series.getName()));
        }
        return seriesStream.collect(Collectors.toMap(QueryResult.Series::getName, InfluxDbConnector::parseSeries, mergeSets));
    }

    public static Set<Map<String, String>> parseSeries(QueryResult.Series series) {
        String[] columns = series.getColumns().toArray(new String[0]);
        return series.getValues().stream().map(valueList -> parseValueList(valueList, columns)).collect(Collectors.toSet());
    }

    public static Map<String, String> parseValueList(List valueList, String[] columns){
        Map<String, String> attributeMap = new HashMap<>();
        Object[] valueArr = valueList.toArray();
        for (int i = 0; i < columns.length; i++) {
            attributeMap.put(columns[i], valueArr[i].toString());
        }
        return attributeMap;
    }
}
