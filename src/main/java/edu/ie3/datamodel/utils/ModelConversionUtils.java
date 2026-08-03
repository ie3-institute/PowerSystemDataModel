/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import static edu.ie3.datamodel.io.naming.FieldNamingStrategy.OPERATES_FROM;
import static edu.ie3.datamodel.io.naming.FieldNamingStrategy.OPERATES_UNTIL;
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.VoltageLevelException;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.ElectricPotential;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public final class ModelConversionUtils {

  private static final Logger logger = LoggerFactory.getLogger(ModelConversionUtils.class);
  private static final GeoJsonReader geoJsonReader = new GeoJsonReader();

  private ModelConversionUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Checks whether attribute map contains a value for given key
   *
   * @param fieldsToAttributes map field key to attribute
   * @param key key to check for
   * @return whether attribute map contains given field or not
   */
  public static boolean containsKey(Map<String, String> fieldsToAttributes, String key) {
    return fieldsToAttributes.containsKey(key);
  }

  /**
   * Checks if the field is empty.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field to check
   * @return {@code true} if either the key is not present or the field is empty
   */
  public static boolean isFieldEmpty(Map<String, String> fieldsToAttributes, String field) {
    String value = fieldsToAttributes.getOrDefault(field, null);
    return value == null || value.isEmpty();
  }

  /**
   * Checks if the field is empty.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field to check
   * @return {@code true} if either the key is not present or the field is empty
   */
  public static boolean isFieldBlank(Map<String, String> fieldsToAttributes, String field) {
    String value = fieldsToAttributes.getOrDefault(field, null);
    return value == null || value.isBlank();
  }

  /**
   * Returns field value for given field name. Throws {@link FactoryException} if field does not
   * exist.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field field name
   * @return field value
   */
  public static String getField(Map<String, String> fieldsToAttributes, String field) {
    if (!fieldsToAttributes.containsKey(field))
      throw new FactoryException(String.format("Field \"%s\" not found in EntityData", field));

    return fieldsToAttributes.remove(field);
  }

  /**
   * Returns field value for given field name, or empty Optional if field does not exist.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field field name
   * @return field value
   */
  public static Optional<String> getFieldOptional(
      Map<String, String> fieldsToAttributes, String field) {
    return Optional.ofNullable(fieldsToAttributes.remove(field));
  }

  /**
   * Parses and returns a Quantity from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field field name
   * @param unit unit of Quantity
   * @param <Q> unit type parameter
   * @return Quantity of given field with given unit
   */
  public static <Q extends Quantity<Q>> ComparableQuantity<Q> getQuantity(
      Map<String, String> fieldsToAttributes, String field, Unit<Q> unit) {
    return Quantities.getQuantity(getDouble(fieldsToAttributes, field), unit);
  }

  /**
   * Returns field value for given field name, or empty Optional if field does not exist.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field field name
   * @param unit unit of Quantity
   * @param <Q> unit type parameter
   * @return field value
   */
  public static <Q extends Quantity<Q>> Optional<ComparableQuantity<Q>> getQuantityOptional(
      Map<String, String> fieldsToAttributes, String field, Unit<Q> unit) {
    return Optional.ofNullable(fieldsToAttributes.remove(field))
        .filter(str -> !str.isEmpty())
        .map(Double::parseDouble)
        .map(value -> Quantities.getQuantity(value, unit));
  }

  /**
   * Returns int value for given field name. Throws {@link FactoryException} if field does not exist
   * or parsing fails.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field field name
   * @return int value
   */
  public static int getInt(Map<String, String> fieldsToAttributes, String field) {
    String fieldValue = getField(fieldsToAttributes, field);

    try {
      return Integer.parseInt(fieldValue);
    } catch (NumberFormatException nfe) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse field \"%s\" with supposed int value \"%s\"",
              field, fieldValue),
          nfe);
    }
  }

  /**
   * Returns double value for given field name. Throws {@link FactoryException} if field does not
   * exist or parsing fails.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field field name
   * @return double value
   */
  public static double getDouble(Map<String, String> fieldsToAttributes, String field) {
    String fieldValue = getField(fieldsToAttributes, field);

    try {
      return Double.parseDouble(fieldValue);
    } catch (NumberFormatException nfe) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse field \"%s\" with supposed double value \"%s\"",
              field, fieldValue),
          nfe);
    }
  }

  /**
   * Parses and returns a UUID from field value of given field name. Throws {@link FactoryException}
   * if field does not exist or parsing fails.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field field name
   * @return UUID
   */
  public static UUID getUUID(Map<String, String> fieldsToAttributes, String field) {
    String fieldValue = getField(fieldsToAttributes, field);

    try {
      return UUID.fromString(fieldValue);
    } catch (IllegalArgumentException iae) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse UUID of field \"%s\" with value \"%s\"",
              field, fieldValue),
          iae);
    }
  }

  /**
   * Returns boolean value for given field name. Throws {@link FactoryException} if field does not
   * exist, or field value is null or empty.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field field name
   * @return true if value is "1" or "true", false otherwise
   */
  public static boolean getBoolean(Map<String, String> fieldsToAttributes, String field) {
    String value = getField(fieldsToAttributes, field);

    if (value == null || value.trim().isEmpty())
      throw new FactoryException(String.format("Field \"%s\" is null or empty", field));

    return value.trim().equals("1") || value.trim().equalsIgnoreCase("true");
  }

  /**
   * Parses and returns a geometry from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field field name
   * @return Geometry if field value is not empty, empty Optional otherwise
   */
  private static Optional<Geometry> getGeometry(
      Map<String, String> fieldsToAttributes, String field) {
    String value = getField(fieldsToAttributes, field);

    try {
      if (value.trim().isEmpty()) return Optional.empty();
      else return Optional.of(geoJsonReader.read(value));
    } catch (ParseException pe) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse geometry of field \"%s\" with value \"%s\"",
              field, value),
          pe);
    }
  }

  /**
   * Parses and returns a geometrical LineString from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field field name
   * @return LineString if field value is not empty, empty Optional otherwise
   */
  public static Optional<LineString> getLineString(
      Map<String, String> fieldsToAttributes, String field) {
    Optional<Geometry> geom = getGeometry(fieldsToAttributes, field);
    if (geom.isPresent()) {
      if (geom.get() instanceof LineString lineString) return Optional.of(lineString);
      else
        throw new FactoryException(
            "Geometry is of type "
                + geom.getClass().getSimpleName()
                + ", but type LineString is required");
    } else return Optional.empty();
  }

  /**
   * Parses and returns a geometrical Point from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param field field name
   * @return Point if field value is not empty, empty Optional otherwise
   */
  public static Optional<Point> getPoint(Map<String, String> fieldsToAttributes, String field) {
    Optional<Geometry> geom = getGeometry(fieldsToAttributes, field);
    if (geom.isPresent()) {
      if (geom.get() instanceof Point point) return Optional.of(point);
      else
        throw new FactoryException(
            "Geometry is of type "
                + geom.getClass().getSimpleName()
                + ", but type Point is required");
    } else return Optional.empty();
  }

  /**
   * Parses and returns a voltage level from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param fieldsToAttributes map field key to attribute
   * @param voltLvlField name of the field containing the voltage level
   * @param ratedVoltField name of the field containing the rated voltage
   * @return Voltage level
   */
  public static VoltageLevel getVoltageLvl(
      Map<String, String> fieldsToAttributes, String voltLvlField, String ratedVoltField) {
    try {
      final String voltLvlId = getField(fieldsToAttributes, voltLvlField);
      final ComparableQuantity<ElectricPotential> vRated =
          getQuantity(fieldsToAttributes, ratedVoltField, KILOVOLT);

      return parseToGermanVoltLvlOrIndividual(voltLvlId, vRated);
    } catch (IllegalArgumentException iae) {
      throw new FactoryException("VoltageLevel could not be parsed", iae);
    }
  }

  /**
   * Parses the given voltage level information to German voltage level or builds an individual one,
   * if no suitable one can be found.
   *
   * @param voltLvlId Identifier of the voltage level
   * @param vRated Foreseen rated voltage
   * @return A suitable German {@link edu.ie3.datamodel.models.voltagelevels.CommonVoltageLevel} or
   *     an individual one
   */
  private static VoltageLevel parseToGermanVoltLvlOrIndividual(
      String voltLvlId, ComparableQuantity<ElectricPotential> vRated) {
    try {
      return GermanVoltageLevelUtils.parse(voltLvlId, vRated);
    } catch (VoltageLevelException e) {
      logger.warn(
          "Cannot parse ({}, {}) to common German voltage level. Build an individual one.",
          voltLvlId,
          vRated);
      return new VoltageLevel(voltLvlId, vRated);
    }
  }

  /**
   * Creates an {@link OperationTime} from the entity data from attributes OPERATES_FROM and
   * OPERATES_UNTIL. Both or one of these can be empty or non-existing.
   *
   * @param data data to take the dates from
   * @return Operation time object
   */
  public static OperationTime buildOperationTime(Map<String, String> data) {
    final String from = getFieldOptional(data, OPERATES_FROM).orElse(null);
    final String until = getFieldOptional(data, OPERATES_UNTIL).orElse(null);

    OperationTime.OperationTimeBuilder builder = new OperationTime.OperationTimeBuilder();
    if (from != null && !from.trim().isEmpty()) builder.withStart(ZonedDateTime.parse(from));
    if (until != null && !until.trim().isEmpty()) builder.withEnd(ZonedDateTime.parse(until));

    return builder.build();
  }
}
