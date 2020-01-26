package edu.ie3.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;


public class CoordinateUtils {

    public static final int             SPATIAL_REFERENCE_ID = 4326;
    private static      GeometryFactory geometryFactory;

    /** Private Constructor as this class is not meant to be instantiated */
    private CoordinateUtils() {
        throw new IllegalStateException("This is an Utility Class and not meant to be instantiated");
    }

    static {
        geometryFactory =
                        new GeometryFactory(
                                        new PrecisionModel(PrecisionModel.FLOATING_SINGLE), SPATIAL_REFERENCE_ID);
    }

    /**
     * Wraps XY values in a JTS geometry point
     *
     * @param x latitude value
     * @param y longitude value
     * @return JTS geometry Point
     */
    public static Point xyCoordToPoint(Double x, Double y) {
        Coordinate coordinate = new Coordinate(x, y, 0);
        return geometryFactory.createPoint(coordinate);
    }
}
