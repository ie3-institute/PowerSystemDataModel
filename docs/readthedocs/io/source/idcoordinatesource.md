# IdCoordinateSource
An id coordinate source provides a mapping between ids of a coordinate and the actual coordinates
latitude and longitude values. The id coordinate source itself is an interface that provides some
methods to get coordinates, ids of coordinates or the distance between a given coordinate and other
coordinates.


## Information

| Attribute    | Remarks                                                      |
|:-------------|:-------------------------------------------------------------|
| `Id`         | An integer value for identifying the coordinate.             |
| `Coordiante` | Geographical information each as `Lat/long` of as a `Point`. |



## Known implementations:
The following implementations are currently known:

- [Csv Id Coordinate Source](/io/csvfiles)
- [Sql Id Coordinate Source](/io/sql)


## Method for coordinates:
The IdCoordinateSource contains method for returning coordinates for given ids.

    Optional<Point> getCoordinate(int id)
    Collection<Point> getCoordinates(int... ids)
    Collection<Point> getAllCoordinates()

1. This method is used to return the coordinate of a given id. If no coordinate is found for
the given id, an empty optional is returned.

2. This method is used to return the coordinates of a given set of ids. The method will only return
coordinates for existing ids.

3. This method is used to return all available coordinates.


## Method for ids:

The IdCoordinateSource contains a method for retrieving the id of a given coordinate.

    Optional<Integer> getId(Point coordinate)

This method is used to return the id of a given coordinate. If no id is found for the given
coordinate, an empty optional is returned.


## Method for calculating distances:
The IdCoordinateSource also contains methods for calculation the distances og a given coordinate 
to a set of coordinates. All the following methods will return the closest n coordinates with their 
distances.

    List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n)
    List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n, ComparableQuantity<Length> distance)
    List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n, Collection<Point> coordinates)


1. This method is used when no set of coordinates or no maximal distance is given. The method will
search for coordinates within a bounding box with increasing size until at least n coordinates are 
found. After that this method will calculate the distances and return the closest n coordinates

2. This method is used to return the closest n coordinates within a given distance. The method will
use the given distance to create a bounding box. After that the distances to all the coordinates in
the bounding box are calculated and n the closest coordinates are returned. If the number of found 
coordinates < n, this method will return less than n coordinates.

3. This method is used to calculate the distances to a set of give coordinates. After the calculation
the method will return the closest n coordinates. If the number of distances < n, this method will
return less than n coordinates.


## Finding and returning the closest corner coordinates:
In most cases we need four corner coordinates for our given coordinate. Therefor the 
IdCoordinateSource contains a method that will use the calculated distances to find the closest 
corner coordinates for the given coordinate.

    List<CoordinateDistance> restrictToBoundingBoxWithSetNumberOfCorner(
          Point coordinate,
          Collection<CoordinateDistance> distances,
          int numberOfPoints
          )

For a given set of coordinates, the closest four corner coordinates plus more close points if n > 4
are returned. If n < 4 the method will return the closest n corner coordinates. If the set of 
coordinates contains a coordinate that matches the given coordinate, only this one coordinate is
returned. If n > number of coordinates in the set, all coordinates are returned.