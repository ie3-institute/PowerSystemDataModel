package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.csv.FileNamingStrategy;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.io.source.WeatherSource;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.interval.ClosedInterval;
import org.locationtech.jts.geom.Point;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class CsvWeatherSource extends CsvTimeSeriesSource implements WeatherSource {


    private final IdCoordinateSource coordinateSource;

    public CsvWeatherSource(String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy, IdCoordinateSource coordinateSource) {
        super(csvSep, folderPath, fileNamingStrategy, coordinateSource);
        this.coordinateSource = coordinateSource;
    }

    @Override
    public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(ClosedInterval<ZonedDateTime> timeInterval) {
        return null;
    }

    @Override
    public Map<Point, IndividualTimeSeries<WeatherValue>> getWeather(ClosedInterval<ZonedDateTime> timeInterval, Collection<Point> coordinates) {
        return null;
    }

    @Override
    public Optional<TimeBasedValue<WeatherValue>> getWeather(ZonedDateTime date, Point coordinate) {
        return Optional.empty();
    }
}
