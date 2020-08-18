package edu.ie3.datamodel.io.factory.deserializing;

import edu.ie3.datamodel.exceptions.FactoryException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;

import java.util.Optional;

public class PointDeserializingStrategy implements SingleValueDeserializingStrategy<Point> {

    private static final GeoJsonReader geoJsonReader = new GeoJsonReader();

    @Override
    public Optional<Point> deserialize(String valueToDeserialize) {
        try {
            if (valueToDeserialize.trim().isEmpty()) return Optional.empty();
            Geometry geometry = geoJsonReader.read(valueToDeserialize);
            if (geometry instanceof Point) return Optional.of((Point) geometry);
            else return Optional.empty();
        } catch (ParseException pe) {
            //TODO revamp Exception
            throw new FactoryException("Nope.");
        }
    }

    @Override
    public Class<Point> getType() {
        return Point.class;
    }
}
