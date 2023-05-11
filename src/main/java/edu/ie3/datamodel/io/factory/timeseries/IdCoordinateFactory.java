/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.exceptions.InvalidColumnNameException;
import edu.ie3.datamodel.io.factory.Factory;
import edu.ie3.datamodel.io.factory.SimpleFactoryData;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.locationtech.jts.geom.Point;

/**
 * Abstract class definition for a factory, that is able to build single mapping entries from
 * coordinate identifier to actual coordinate
 */
public abstract class IdCoordinateFactory
    extends Factory<Pair, SimpleFactoryData, Pair<Integer, Point>> {
  protected IdCoordinateFactory() {
    super(Pair.class);
  }

  /** @return the field id for the coordinate id */
  public abstract String getIdField();

  /** @return the field id for the coordinate latitude */
  public abstract String getLatField();

  /** @return the field id for the coordinate longitude */
  public abstract String getLonField();

  public void checkForInvalidColumnNames(Set<String> columnNames) {
    List<String> validColumnNames = List.of(getIdField(), getLatField(), getLonField());

    if (!columnNames.containsAll(validColumnNames)) {
      throw new InvalidColumnNameException(
          "The provided column names "
              + columnNames
              + " does not match the expected column names "
              + validColumnNames
              + "!");
    }

    if (columnNames.size() != validColumnNames.size()) {
      log.warn(
          "The provided row has more column names than expected. Provided: "
              + columnNames
              + ", expected: "
              + validColumnNames
              + ".");
    }
  }
}
