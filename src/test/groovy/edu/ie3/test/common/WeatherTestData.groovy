/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.io.factory.timeseries.CosmoIdCoordinateFactory
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.datamodel.io.source.csv.CsvDataSource

import edu.ie3.datamodel.io.source.csv.CsvTestDataMeta
import edu.ie3.datamodel.models.UniqueEntity
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Point

import java.util.stream.Collectors
import java.util.stream.Stream

abstract class WeatherTestData {

  // Using a groovy bug to gain access to private methods in superclass:
  // by default, we cannot access private methods with parameters from abstract parent classes, introducing a
  // class that extends the abstract parent class and unveils the private methods by calling the parents private
  // methods in a public or protected method makes them available for testing
  public static final class DummyCsvSource extends CsvDataSource {

    DummyCsvSource(String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy) {
      super(csvSep, folderPath, fileNamingStrategy)
    }

    Map<String, String> buildFieldsToAttributes(
            final String csvRow, final String[] headline) {
      return super.buildFieldsToAttributes(csvRow, headline)
    }

    OperatorInput getFirstOrDefaultOperator(
            Collection<OperatorInput> operators, String operatorUuid, String entityClassName, String requestEntityUuid) {
      return super.getFirstOrDefaultOperator(operators, operatorUuid, entityClassName, requestEntityUuid)
    }

    def <T extends UniqueEntity> Set<Map<String, String>> distinctRowsWithLog(
            Class<T> entityClass, Collection<Map<String, String>> allRows) {
      return super.distinctRowsWithLog(allRows, uuidExtractor, entityClass.simpleName, "UUID")
    }

    String[] parseCsvRow(
            String csvRow, String csvSep) {
      return super.parseCsvRow(csvRow, csvSep)
    }

    String[] oldFieldVals(
            String csvSep, String csvRow) {
      return super.oldFieldVals(csvSep, csvRow)
    }
  }

  public static final class DummyIdCoordinateSource extends IdCoordinateSource implements CsvTestDataMeta {

    DummyIdCoordinateSource() {
      super(new CosmoIdCoordinateFactory(), new DummyCsvSource(csvSep, coordinatesCosmoFolderPath, fileNamingStrategy))
    }

    Optional<Point> getCoordinate(int id) {
      switch (id) {
        case 193186: return Optional.of(GeoUtils.buildPoint(7d, 49d))
        case 193187: return Optional.of(GeoUtils.buildPoint(8d, 49d))
        case 193188: return Optional.of(GeoUtils.buildPoint(7d, 50d))
        case 67775: return Optional.of(GeoUtils.buildPoint(8d, 50d))
        case 67776: return Optional.of(GeoUtils.buildPoint(7d, 51d))
      }
      return Optional.empty()
    }

    Collection<Point> getCoordinates(int... ids) {
      return Stream.of(ids).map(this.&getCoordinate).filter({ c -> c != null }).collect(Collectors.toSet())
    }

    Optional<Integer> getId(Point coordinate) {
      if (coordinate.x == 49 && coordinate.y == 7) {
        return Optional.of(193186)
      }
      if (coordinate.x == 49 && coordinate.y == 8) {
        return Optional.of(193187)
      }
      if (coordinate.x == 50 && coordinate.y == 7) {
        return Optional.of(193188)
      }
      if (coordinate.x == 50 && coordinate.y == 8) {
        return Optional.of(67775)
      }
      if (coordinate.x == 51 && coordinate.y == 7) {
        return Optional.of(67776)
      }
      return Optional.empty()
    }


    Collection<Point> getAllCoordinates() {
      return [
        GeoUtils.buildPoint(7d, 49d),
        GeoUtils.buildPoint(8d, 49d),
        GeoUtils.buildPoint(7d, 50d),
        GeoUtils.buildPoint(8d, 50d),
        GeoUtils.buildPoint(7d, 51d)
      ]
    }
  }

  public static final IdCoordinateSource coordinateSource = new DummyIdCoordinateSource()

  public static final COORDINATE_193186 = coordinateSource.getCoordinate(193186).get()
  public static final COORDINATE_193187 = coordinateSource.getCoordinate(193187).get()
  public static final COORDINATE_193188 = coordinateSource.getCoordinate(193188).get()
  public static final COORDINATE_67775 = coordinateSource.getCoordinate(67775).get()
  public static final COORDINATE_67776 = coordinateSource.getCoordinate(67776).get()
}