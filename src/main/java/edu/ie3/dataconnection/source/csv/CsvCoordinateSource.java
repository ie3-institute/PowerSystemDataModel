/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.dataconnection.source.csv;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.vividsolutions.jts.geom.Point;
import edu.ie3.utils.CoordinateUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CsvCoordinateSource { // TODO replace with real Csv Reader
  public static HashMap<Integer, Point> idToCoordinate = new HashMap<>();
  public static HashMap<Point, Integer> coordinateToId = new HashMap<>();

  public static void fillCoordinateMaps() {
    CSVReader reader = null;
    try {
      CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
      InputStream inputStream =
          CsvCoordinateSource.class.getClassLoader().getResourceAsStream("eu_coords.csv");
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
      reader = new CSVReaderBuilder(inputStreamReader).withCSVParser(parser).build();
      String[] nextLine = reader.readNext();
      int latIndex = -1, lonIndex = -1, idIndex = -1;
      if (nextLine == null) return;
      for (int i = 0; i < nextLine.length; i++) {
        if (nextLine[i].equals("lat_geo")) latIndex = i;
        if (nextLine[i].equals("long_geo")) lonIndex = i;
        if (nextLine[i].equals("id")) idIndex = i;
      }
      while ((nextLine = reader.readNext()) != null) {
        idToCoordinate.put(
            Integer.parseInt(nextLine[idIndex]),
            CoordinateUtils.xyCoordToPoint(
                Double.parseDouble(nextLine[latIndex]), Double.parseDouble(nextLine[lonIndex])));
      }
    } catch (IOException | CsvValidationException e) {
      e.printStackTrace();
    }
    idToCoordinate.forEach((k, v) -> coordinateToId.put(v, k));
  }

  public static Point getCoordinate(Integer id) {
    return idToCoordinate.get(id);
  }

  public static Collection<Point> getCoordinates(int... ids) {
    return Arrays.stream(ids)
        .mapToObj(CsvCoordinateSource::getCoordinate)
        .collect(Collectors.toSet());
  }

  public static Collection<Point> getCoordinatesBetween(Integer fromId, Integer toId) {
    return getCoordinates(IntStream.rangeClosed(fromId, toId).toArray());
  }

  public static Integer getId(Point coordinate) {
    return coordinateToId.get(coordinate);
  }

  public static Integer getCoordinateCount() {
    return idToCoordinate.keySet().size();
  }
}
