package edu.ie3.dataconnection.dataconnectors.test;

import edu.ie3.dataconnection.dataconnectors.InfluxDbConnector;
import edu.ie3.dataconnection.sink.InfluxDbDataSink;
import edu.ie3.dataconnection.source.influxdb.InfluxDbWeatherSource;
import edu.ie3.models.result.connector.LineResult;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.utils.CoordinateTools;
import tec.uom.se.quantity.Quantities;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class Main {

    public static void main(String[] args){
        InfluxDbConnector connector_in = new InfluxDbConnector("ie3_in");
        InfluxDbWeatherSource src = new InfluxDbWeatherSource(connector_in);
        Optional<TimeBasedValue<WeatherValues>> weather = src.getWeather(ZonedDateTime.ofInstant(Instant.ofEpochMilli(1577836800000000000l), ZoneId.of("UTC")), CoordinateTools.xyCoordToPoint(11.2, 11.1));
        System.out.println(weather.get());

        ArrayList l = new ArrayList();
        for(int i = 0; i<10; i++){
        LineResult lr = new LineResult(Quantities.getQuantity(1.0+i, PowerSystemUnits.AMPERE),
                Quantities.getQuantity(2.0+i, PowerSystemUnits.RADIAN),
                Quantities.getQuantity(3.0+i, PowerSystemUnits.AMPERE),
                Quantities.getQuantity(4.0+i, PowerSystemUnits.RADIAN));
        lr.setInput(UUID.randomUUID());
        lr.setTimestamp(ZonedDateTime.now());
        l.add(lr);
        }

        InfluxDbConnector connector_out = new InfluxDbConnector("ie3_out");
        InfluxDbDataSink sink = new InfluxDbDataSink(connector_out);
        sink.writeAll(l);
    }

}
