import com.vividsolutions.jts.geom.Point;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.interval.ClosedInterval;
import edu.ie3.utils.CoordinateTools;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class TestEntityBuilder {

    public static final ZonedDateTime START_DATE = ZonedDateTime.of(2013, 4, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
    public static final ZonedDateTime MIDDLE_DATE = ZonedDateTime.of(2013, 4, 14, 12, 0, 0, 0, ZoneId.of("UTC"));
    public static final ZonedDateTime END_DATE = ZonedDateTime.of(2013, 4, 30, 23, 59, 0, 0, ZoneId.of("UTC"));
    public static final ClosedInterval<ZonedDateTime> MONTH_INTERVAL = new ClosedInterval<>(START_DATE, END_DATE);
    public static final Point TEST_COORDINATE_A = CoordinateTools.xyCoordToPoint(39.602772000000002, 1.279336);
    public static final Point TEST_COORDINATE_B = CoordinateTools.xyCoordToPoint(39.610000999999997, 1.358673);
    public static final Point TEST_COORDINATE_C = CoordinateTools.xyCoordToPoint(39.990299, 8.6342192000000004);
    public static final List<Point> TEST_COORDINATES = Arrays.asList(TEST_COORDINATE_A, TEST_COORDINATE_B, TEST_COORDINATE_C);

    public static List<TimeBasedValue<WeatherValues>> getWeatherValues() {
        
    }


}
