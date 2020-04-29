package edu.ie3.datamodel.io.source;

import org.locationtech.jts.geom.Point;

import java.util.Collection;

public interface CoordinateSource extends DataSource {

    Point getCoordinate(Integer id);

    Collection<Point> getCoordinates(Integer... ids);
    
    Collection<Point> getCoordinatesBetween(Integer fromId, Integer toId);

    Integer getId(Point coordinate);

}
