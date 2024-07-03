/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUtils {

  protected static final Logger log = LoggerFactory.getLogger(SqlUtils.class);
  private static final String endQueryCreateTable =
      ")\n \t WITHOUT OIDS\n \t TABLESPACE pg_default;";

  private SqlUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  private static String beginQueryCreateTable(String schemaName, String tableName) {
    return "CREATE TABLE " + schemaName + "." + tableName + "\n(\n";
  }

  /** @return query to create a SQL table for a grid */
  public static String queryCreateGridTable(String schemaName) {
    return beginQueryCreateTable(schemaName, DbGridMetadata.GRID_TABLE)
        + "\tuuid uuid PRIMARY KEY,\n\tname TEXT NOT NULL\n"
        + endQueryCreateTable;
  }

  /**
   * To avoid data type conflicts while insertion into a SQL table all columns should be quoted.
   *
   * @return input with quoteSymbol
   */
  public static String quote(String input, String quoteSymbol) {
    if (Objects.equals(input, "") || Objects.equals(input, "null")) {
      return "NULL";
    } else {
      return input.matches("^\".*\"$") ? input : quoteSymbol + input + quoteSymbol;
    }
  }
}
