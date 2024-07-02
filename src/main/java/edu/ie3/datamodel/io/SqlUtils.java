/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import static edu.ie3.util.StringUtils.camelCaseToSnakeCase;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.exceptions.ProcessorProviderException;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.models.Entity;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUtils {

  protected static final Logger log = LoggerFactory.getLogger(SqlUtils.class);
  private static final String endQueryCreateTable =
      ")\n" + "\t WITHOUT OIDS\n" + "\t TABLESPACE pg_default;";

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

  /** @return query to create a SQL table for an unique entity */
  public static String queryCreateTableUniqueEntity(Class<? extends Entity> cls, String schemaName)
      throws EntityProcessorException, ProcessorProviderException {
    ProcessorProvider processorProvider = new ProcessorProvider();
    DatabaseNamingStrategy namingStrategy = new DatabaseNamingStrategy();
    String[] headerElements = processorProvider.getHeaderElements(cls);
    Stream<String> strHeader =
        Stream.concat(Arrays.stream(headerElements), Stream.of(DbGridMetadata.GRID_UUID));
    Stream<String> dtHeader =
        strHeader.map(
            element ->
                camelCaseToSnakeCase(element)
                    + " "
                    + columnToSqlDataType().get(camelCaseToSnakeCase(element)));
    return "CREATE TABLE "
        + schemaName
        + "."
        + namingStrategy.getEntityName(cls).orElseThrow()
        + "\n(\n\t"
        + String.valueOf(dtHeader.collect(Collectors.joining(",\n\t")))
        + "\n)\n\t"
        + "WITHOUT OIDS\n"
        + "\t"
        + "TABLESPACE pg_default;\n";
  }

  /**
   * Map to create a SQL table for an entity with the right data types.
   *
   * @return Map column name -> data type
   */
  public static Map<String, String> columnToSqlDataType() {
    Map<String, String> map = new HashMap<>();

    map.put("uuid", "uuid PRIMARY KEY");
    map.put("time_series", "uuid NOT NULL");
    map.put("time", "timestamp with time zone NOT NULL");
    map.put("p", "double precision NOT NULL");
    map.put("q", "double precision NOT NULL");
    map.put("c", "double precision NOT NULL");
    map.put("s_rated", "double precision NOT NULL");

    map.put("cost_controlled", "bool NOT NULL");
    map.put("feed_in_tariff", "int NOT NULL");
    map.put("id", "TEXT NOT NULL");
    map.put("market_reaction", "bool NOT NULL");
    map.put("node", "uuid NOT NULL");
    map.put("operates_from", "timestamp with time zone");
    map.put("operates_until", "timestamp with time zone");
    map.put("operator", "uuid");
    map.put("q_characteristics", "TEXT NOT NULL");
    map.put("geo_position", "TEXT NOT NULL");
    map.put("length", "double precision NOT NULL");
    map.put("node_a", "uuid NOT NULL");
    map.put("node_b", "uuid NOT NULL");
    map.put("olm_characteristic", "TEXT NOT NULL");
    map.put("parallel_devices", "int NOT NULL");
    map.put("cos_phi_rated", "TEXT NOT NULL");
    map.put("dsm", "bool NOT NULL");
    map.put("e_cons_annual", "double precision NOT NULL");
    map.put("load_profile", "TEXT NOT NULL");
    map.put("controlling_em", "uuid NOT NULL");

    map.put("auto_tap", "bool NOT NULL");
    map.put("tap_pos", "int NOT NULL");
    map.put("type", "uuid NOT NULL");

    map.put("v_ang", "bool NOT NULL");
    map.put("v_mag", "bool NOT NULL");
    map.put("slack", "bool NOT NULL");
    map.put("subnet", "int NOT NULL");
    map.put("v_rated", "double precision NOT NULL");
    map.put("v_target", "double precision NOT NULL");
    map.put("volt_lvl", "TEXT NOT NULL");
    map.put("charging_points", "int NOT NULL");
    map.put("location_type", "TEXT NOT NULL");
    map.put("v_2g_support", "bool NOT NULL");

    map.put("albedo", "double precision NOT NULL");
    map.put("azimuth", "double precision NOT NULL");
    map.put("elevation_angle", "double precision NOT NULL");
    map.put("eta_conv", "double precision NOT NULL");
    map.put("k_g", "double precision NOT NULL");
    map.put("k_t", "double precision NOT NULL");

    map.put("grid_name", "TEXT NOT NULL");
    map.put("grid_uuid", "uuid NOT NULL");

    map.put("b_m", "double precision NOT NULL");
    map.put("d_phi", "double precision NOT NULL");
    map.put("d_v", "double precision NOT NULL");
    map.put("g_m", "double precision NOT NULL");
    map.put("r_sc", "double precision NOT NULL");
    map.put("tap_max", "int NOT NULL");
    map.put("tap_min", "int NOT NULL");
    map.put("tap_neutr", "int NOT NULL");
    map.put("tap_side", "bool NOT NULL");
    map.put("v_rated_a", "int NOT NULL");
    map.put("v_rated_b", "int NOT NULL");
    map.put("x_sc", "int NOT NULL");
    map.put("graphic_layer", "TEXT NOT NULL");
    map.put("line", "uuid NOT NULL");
    map.put("path", "TEXT NOT NULL");
    map.put("point", "TEXT NOT NULL");
    map.put("inlet_temp", "double precision NOT NULL");
    map.put("return_temp", "double precision NOT NULL");
    map.put("storage_volume_lvl", "double precision NOT NULL");
    map.put("storage_volume_lvl_min", "double precision NOT NULL");
    map.put("thermal_bus", "uuid NOT NULL");
    map.put("eth_capa", "double precision NOT NULL");
    map.put("eth_losses", "double precision NOT NULL");
    map.put("lower_temperature_limit", "double precision NOT NULL");
    map.put("target_temperature", "double precision NOT NULL");
    map.put("upper_temperature_limit", "double precision NOT NULL");
    map.put("b", "double precision NOT NULL");
    map.put("g", "double precision NOT NULL");
    map.put("i_max", "double precision NOT NULL");
    map.put("r", "double precision NOT NULL");
    map.put("x", "double precision NOT NULL");

    map.put("connected_assets", "TEXT NOT NULL");
    map.put("capex", "double precision NOT NULL");
    map.put("control_strategy", "TEXT NOT NULL");

    map.put("input_model", "uuid NOT NULL");
    map.put("soc", "double precision NOT NULL");
    map.put("p_max", "double precision NOT NULL");
    map.put("p_min", "double precision NOT NULL");
    map.put("p_ref", "double precision NOT NULL");

    map.put("dod", "double precision NOT NULL");
    map.put("e_storage", "double precision NOT NULL");
    map.put("eta", "double precision NOT NULL");
    map.put("life_cycle", "double precision NOT NULL");
    map.put("life_time", "double precision NOT NULL");
    map.put("opex", "double precision NOT NULL");
    map.put("active_power_gradient", "double precision NOT NULL");

    // TODO: Not all data types are implemented

    return map;
  }

  /**
   * To avoid data type conflicts while insertion into a SQL table all columns should be quoted.
   *
   * @return input with quoteSymbol
   */
  public static String quote(String input, String quoteSymbol) {
    if (Objects.equals(input, "")) {
      return "NULL";
    } else {
      return input.matches("^\".*\"$") ? input : quoteSymbol + input + quoteSymbol;
    }
  }
}
