package edu.ie3.dataconnection.dataconnectors.test;

import edu.ie3.dataconnection.source.CsvCoordinateSource;

public class Main {

    public static void main(String[] args){

//        InfluxDbConnector connector_in = new InfluxDbConnector("ie3_in");
//        InfluxDbWeatherSource src = new InfluxDbWeatherSource(connector_in);
//        Optional<TimeBasedValue<WeatherValues>> weather = src.getWeather(ZonedDateTime.ofInstant(Instant.ofEpochMilli(1577836800000000000L), ZoneId.of("UTC")), CoordinateTools.xyCoordToPoint(11.2, 11.1));
//        System.out.println(weather.get());
//
//        ArrayList l = new ArrayList();
//        for(int i = 0; i<10; i++){
//        LineResult lr = new LineResult(ZonedDateTime.now(), UUID.randomUUID(), Quantities.getQuantity(1.0+i, PowerSystemUnits.AMPERE),
//                Quantities.getQuantity(2.0+i, PowerSystemUnits.RADIAN),
//                Quantities.getQuantity(3.0+i, PowerSystemUnits.AMPERE),
//                Quantities.getQuantity(4.0+i, PowerSystemUnits.RADIAN));
//        l.add(lr);
//        }
//
//        InfluxDbConnector connector_out = new InfluxDbConnector("ie3_out");
//        InfluxDbDataSink sink = new InfluxDbDataSink(connector_out);
//        sink.persistAll(l);

        System.out.println(CsvCoordinateSource.getCoordinateCount());
    }

}
