package edu.ie3.dataconnection.source;

import com.opencsv.bean.CsvToBeanBuilder;
import com.vividsolutions.jts.geom.Point;
import edu.ie3.models.csv.CsvWeatherInput;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.interval.ClosedInterval;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WeatherTestEntityBuilder {

    public static final ZonedDateTime START_DATE = ZonedDateTime.of(2013, 4, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
    public static final ZonedDateTime MIDDLE_DATE = ZonedDateTime.of(2013, 4, 3, 12, 0, 0, 0, ZoneId.of("UTC"));
    public static final ZonedDateTime END_DATE = ZonedDateTime.of(2013, 4, 7, 23, 59, 0, 0, ZoneId.of("UTC"));
    public static final ZonedDateTime INVALID_DATE = ZonedDateTime.of(1995, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
    public static final ClosedInterval<ZonedDateTime> MONTH_INTERVAL = new ClosedInterval<>(START_DATE, END_DATE);
    public static final Point TEST_COORDINATE_A = CsvCoordinateSource.getCoordinate(184465);
    public static final Point TEST_COORDINATE_B = CsvCoordinateSource.getCoordinate(192426);
    public static final Point TEST_COORDINATE_C = CsvCoordinateSource.getCoordinate(199848);
    public static final Point TEST_COORDINATE_INVALID = CsvCoordinateSource.getCoordinate(200000);
    public static final List<Point> TEST_COORDINATES = Arrays.asList(TEST_COORDINATE_A, TEST_COORDINATE_B, TEST_COORDINATE_C);
    public static final UUID UNSPECIFIC_UUID = UUID.randomUUID();
    public static List<TimeBasedValue<WeatherValues>> weatherValues = new LinkedList<>();

    public static void fillWeatherValues() {
        List<CsvWeatherInput> csvWeatherInputs = new LinkedList<>();
        try {
            String file = WeatherTestEntityBuilder.class.getClassLoader().getResource("weather.csv").getFile();
            csvWeatherInputs = new CsvToBeanBuilder(new FileReader(file))
                    .withType(CsvWeatherInput.class).withSeparator(';').build().parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<TimeBasedValue<WeatherValues>> weatherValuesWithoutUuid = csvWeatherInputs.stream().map(csvInput -> csvInput.toTimeBasedWeatherValues()).collect(Collectors.toList());
        weatherValues = weatherValuesWithoutUuid.stream().map(tbv -> new TimeBasedValue<WeatherValues>(UNSPECIFIC_UUID, tbv.getTime(), tbv.getValue())).collect(Collectors.toList());
    }
}
