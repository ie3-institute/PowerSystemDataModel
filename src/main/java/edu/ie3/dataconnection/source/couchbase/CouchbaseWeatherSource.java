package edu.ie3.dataconnection.source.couchbase;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;
import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.dataconnectors.CouchbaseConnector;
import edu.ie3.dataconnection.dataconnectors.DataConnector;
import edu.ie3.dataconnection.source.CsvCoordinateSource;
import edu.ie3.dataconnection.source.WeatherSource;
import edu.ie3.models.json.JsonWeatherInput;
import edu.ie3.models.timeseries.IndividualTimeSeries;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.interval.ClosedInterval;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class CouchbaseWeatherSource implements WeatherSource {

    CouchbaseConnector connector;

    public CouchbaseWeatherSource(CouchbaseConnector connector){
        this.connector = connector;
    }

    @Override
    public Map<Point, IndividualTimeSeries<WeatherValues>> getWeather(ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
        HashMap<Point, IndividualTimeSeries<WeatherValues>> coordinateToTimeSeries = new HashMap<>();
        for(Point coordinate : coordinates) {
            String query = createQueryStringForIntervalAndCoordinate(timeInterval, coordinate);
            CompletableFuture<QueryResult> futureResult = connector.query(query);
            QueryResult queryResult = futureResult.join();
            List<JsonWeatherInput> jsonWeatherInputs = queryResult.rowsAs(JsonWeatherInput.class);
            List<TimeBasedValue<WeatherValues>> weatherInputs = jsonWeatherInputs.stream().map(JsonWeatherInput::toTimeBasedWeatherValues).collect(Collectors.toList());
            IndividualTimeSeries<WeatherValues> weatherTimeSeries = new IndividualTimeSeries<>();
            weatherTimeSeries.addAll(weatherInputs);
            coordinateToTimeSeries.put(coordinate, weatherTimeSeries);
        }
           return coordinateToTimeSeries;
    }

    @Override
    public Optional<TimeBasedValue<WeatherValues>> getWeather(ZonedDateTime date, Point coordinate) {
        try {
            CompletableFuture<GetResult> futureResult = connector.get(generateWeatherKey(date, coordinate));
            GetResult getResult = futureResult.join();
            JsonWeatherInput jsonWeatherInput = getResult.contentAs(JsonWeatherInput.class);
            TimeBasedValue<WeatherValues> timeBasedWeatherValues = jsonWeatherInput.toTimeBasedWeatherValues();
            return Optional.ofNullable(timeBasedWeatherValues);
        } catch (DocumentNotFoundException ex) {
            return Optional.empty();
        } catch(CompletionException ex) {
            if(ex.getCause() instanceof DocumentNotFoundException)
            return Optional.empty();
            else throw ex;
        }
    }

    @Override
    public DataConnector getDataConnector() {
        return connector;
    }

    public static String generateWeatherKey(ZonedDateTime date, Point coordinate){
        String key = "weather::";
        key += CsvCoordinateSource.getId(coordinate) + "::";
        key += LocalDateTime.from(date).toString() + ":00";
        return key;
    }

    public String createQueryStringForIntervalAndCoordinate(ClosedInterval<ZonedDateTime> timeInterval, Point coordinate) {
        String basicQuery = "SELECT " + connector.getBucketName() + ".* FROM " + connector.getBucketName();
        String whereClause = " WHERE META().id >= '" + generateWeatherKey(timeInterval.getLower(), coordinate) ;
        whereClause += "' AND META().id <= '" + generateWeatherKey(timeInterval.getUpper(), coordinate) + "'";
        return basicQuery + whereClause;
    }
}
