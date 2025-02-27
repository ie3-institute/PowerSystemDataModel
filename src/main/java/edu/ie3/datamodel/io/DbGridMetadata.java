/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import static edu.ie3.datamodel.io.SqlUtils.quote;

import java.util.UUID;
import java.util.stream.Stream;

/** Class for identification of entities and results from grids in SQL databases. */
public record DbGridMetadata(String gridName, UUID uuid) {

  public static final String GRID_TABLE_COLUMN = "grids";
  public static final String GRID_NAME_COLUMN = "grid_name";
  public static final String GRID_UUID_COLUMN = "grid_uuid";

  public String toString() {
    return GRID_NAME_COLUMN + "=" + gridName + ", " + GRID_UUID_COLUMN + "=" + uuid.toString();
  }

  /** @return Stream with grid uuid */
  public Stream<String> getStreamForQuery() {
    return Stream.of(quote(uuid.toString(), "'"));
  }
}
