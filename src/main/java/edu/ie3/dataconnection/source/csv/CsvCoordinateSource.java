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
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class CsvCoordinateSource { // TODO replace with real Csv Reader
  public static HashMap<Integer, Point> idToCoordinate = new HashMap<>();
  public static HashMap<Point, Integer> coordinateToId = new HashMap<>();

  public static void fillCoordinateMaps() {
    CSVReader reader = null;
    try {
      CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
      String file =
          CsvCoordinateSource.class.getClassLoader().getResource("eu_coords.csv").getFile();
      reader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(parser).build();
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

  public static Integer getId(Point coordinate) {
    return coordinateToId.get(coordinate);
  }

  public static Integer getCoordinateCount() {
    return idToCoordinate.keySet().size();
  }
}
