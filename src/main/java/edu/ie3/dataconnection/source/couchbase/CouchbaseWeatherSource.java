package edu.ie3.dataconnection.source.couchbase;

import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;
import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.dataconnectors.CouchbaseConnector;
import edu.ie3.dataconnection.dataconnectors.DataConnector;
import edu.ie3.dataconnection.source.CsvCoordinateSource;
import edu.ie3.dataconnection.source.WeatherSource;
import edu.ie3.models.timeseries.IndividualTimeSeries;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.interval.ClosedInterval;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CouchbaseWeatherSource implements WeatherSource {

    CouchbaseConnector connector;

    @Override
    public Map<Point, IndividualTimeSeries<WeatherValues>> getWeather(ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
        for(Point coordinate : coordinates){
            String query = createQueryStringForIntervalAndCoordinate(timeInterval, coordinate);
            CompletableFuture<QueryResult> result = connector.query(query);
            //TODO json magic
        }


        return null;
    }

    @Override
    public Optional<TimeBasedValue<WeatherValues>> getWeather(ZonedDateTime date, Point coordinate) {
        CompletableFuture<GetResult> result = connector.get(generateWeatherKey(date, coordinate));
        //TODO json magic
        return Optional.empty();
    }

    @Override
    public DataConnector getDataConnector() {
        return connector;
    }

    public String generateWeatherKey(ZonedDateTime date, Point coordinate){
        String key = "weather::";
        key += CsvCoordinateSource.getId(coordinate) + "::";
        key += LocalDateTime.from(date).toString();
        return key;
    }

    public String createQueryStringForIntervalAndCoordinate(ClosedInterval<ZonedDateTime> timeInterval, Point coordinate) {
        String basicQuery = "SELECT * " + connector.getBucketName();
        String whereClause = " WHERE META().id <= " + generateWeatherKey(timeInterval.getLower(), coordinate) ;
        whereClause += " AND META().id>= " + generateWeatherKey(timeInterval.getUpper(), coordinate);
        return basicQuery + whereClause;
    }
}
