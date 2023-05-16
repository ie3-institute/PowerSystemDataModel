# IdCoordinateSource
An id coordinate source provides a mapping between ids of a coordinate and the actual coordinates
latitude and longitude values. The id coordinate source itself is an interface that provides some
methods to get coordinates, ids of coordinates or the distance between a given coordinate and other
coordinates.


## Information

| Attribute    | Remarks                                                        |
|:-------------|:---------------------------------------------------------------|
| `Id`         | An integer value for identifying the coordinate.               |
| `Coordiante` | Geographical information presented as `Lat/long` of a `Point`. |



## Known implementations:
The following implementations are currently known:

- [Csv Id Coordinate Source](/io/csvfiles)
- [Sql Id Coordinate Source](/io/sql)


## Method for coordinates:
The IdCoordinateSource contains method for returning coordinates for given ids.

``` java 
    Optional<Point> getCoordinate(int id)
    Collection<Point> getCoordinates(int... ids)
    Collection<Point> getAllCoordinates()
```

1. This method is used to return the coordinate of a given id. If no coordinate is found for
the given id, an empty optional is returned.

2. This method is used to return the coordinates of a given set of ids. The method will only return
coordinates for existing ids.

3. This method is used to return all available coordinates.


## Method for ids:
The IdCoordinateSource contains a method for retrieving the id of a given coordinate.

``` java
    Optional<Integer> getId(Point coordinate)
```

This method is used to return the id of a given coordinate. If no id is found for the given
coordinate, an empty optional is returned.


## Method for retrieving near coordinates:
The IdCoordinateSource also contains methods for retrieving coordinates/points that are near a given coordinate.
All implementations of these methods in this project will use the method ``restrictToBoundingBox`` for finding and
returning four corner points.

``` java
    List<CoordinateDistance> getNearestCoordinates(Point coordinatem int n)
    List<CoordinateDistance> getClosestCoordinates(Point coordinate, int n, ComparableQuantity<Length> distance)
    List<CoordinateDistance> calculateCoordinateDistances(Point coordinate, int n, Collection<Point> coordinates)
```

1. This method will return the nearest n coordinates for a given coordinate. The method works by having a default radius
that is increased with every iteration until n coordinates are found.

2. This method will return the closest n coordinates for a given coordinate. Unlike the first method, this method has a
defined radius, that won't be increased. Therefor this method can only consider the coordinates inside the bounding box
around this radius.

3. This method is used to calculate the distances to a set of give coordinates. After the calculation
the method will return the closest n coordinates. If the number of distances is less than n, this method will
return less than n coordinates.


## Finding and returning the closest corner coordinates:
In most cases we need four corner coordinates for our given coordinate. Therefor the 
IdCoordinateSource contains a method that will use the calculated distances to find the closest 
corner coordinates for the given coordinate.

``` java
    List<CoordinateDistance> restrictToBoundingBox(
          Point coordinate,
          Collection<CoordinateDistance> distances,
          int numberOfPoints
    )
```

For a given set of coordinates, the closest four corner coordinates plus more close points if n > 4
are returned. If n < 4 the method will return the closest n corner coordinates. If the set of 
coordinates contains a coordinate that matches the given coordinate, only this one coordinate is
returned. If n > number of coordinates in the set, all coordinates are returned.