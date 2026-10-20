/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.VoltageLevelException;
import edu.ie3.datamodel.io.naming.FieldNamingStrategy;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.*;
import java.util.function.Function;
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

public class FieldMap {

  private static final Logger logger = LoggerFactory.getLogger(FieldMap.class);
  private static final GeoJsonReader geoJsonReader = new GeoJsonReader();

  private final Map<String, String> fieldsToAttributes;

  public FieldMap(Map<String, String> fieldsToAttributes) {
    this.fieldsToAttributes = new HashMap<>(fieldsToAttributes);
  }

  /** Returns all additional information as an unmodifiable map. */
  public Map<String, String> additionalInformation() {
    return Collections.unmodifiableMap(fieldsToAttributes);
  }

  /**
   * Checks whether attribute map contains a value for given key
   *
   * @param key key to check for
   * @return whether attribute map contains given field or not
   */
  public boolean containsKey(String key) {
    return fieldsToAttributes.containsKey(key);
  }

  /**
   * Checks if the field is empty.
   *
   * @param field to check
   * @return {@code true} if either the key is not present or the field is empty
   */
  public boolean isFieldEmpty(String field) {
    String value = fieldsToAttributes.getOrDefault(field, null);
    return value == null || value.isEmpty();
  }

  /**
   * Checks if the field is empty.
   *
   * @param field to check
   * @return {@code true} if either the key is not present or the field is empty
   */
  public boolean isFieldBlank(String field) {
    String value = fieldsToAttributes.getOrDefault(field, null);
    return value == null || value.isBlank();
  }

  /**
   * Method that takes multiple fields and builds an object with it.
   *
   * @param builder function for building the object
   * @return the build object
   * @param <R> type of the returned object
   */
  public <R> R get(Function<Map<String, String>, R> builder) {
    return builder.apply(fieldsToAttributes);
  }

  /**
   * Method that takes a field and builds an object with it.
   *
   * @param field that is used
   * @param builder function for building the object
   * @return the build object
   * @param <R> type of the returned object
   */
  public <R> R get(String field, Function<String, R> builder) {
    return builder.apply(getField(field));
  }

  /**
   * Method to get a view of the uuid for at a given field
   *
   * @param field that contains an uuid
   * @return the uuid
   */
  public UUID viewUUID(String field) {
    return getUUID(field, false);
  }

  /**
   * Returns field value for given field name. Throws {@link FactoryException} if field does not
   * exist.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @return field value
   */
  public String getField(String field) {
    if (!fieldsToAttributes.containsKey(field))
      throw new FactoryException(String.format("Field \"%s\" not found in source.", field));

    return fieldsToAttributes.remove(field);
  }

  /**
   * Returns field value for given field name. Throws {@link FactoryException} if field does not
   * exist.
   *
   * <p>Note: This method removes the field from the map, if specified.
   *
   * @param field field name
   * @param remove if true, the value is removed
   * @return field value
   */
  private String getField(String field, boolean remove) {
    if (!fieldsToAttributes.containsKey(field))
      throw new FactoryException(String.format("Field \"%s\" not found in source.", field));

    if (remove) {
      return fieldsToAttributes.remove(field);
    } else {
      return fieldsToAttributes.get(field);
    }
  }

  /**
   * Returns field value for given field name, or empty Optional if field does not exist.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @return field value
   */
  public Optional<String> getFieldOptional(String field) {
    return Optional.ofNullable(fieldsToAttributes.remove(field));
  }

  /**
   * Returns int value for given field name. Throws {@link FactoryException} if field does not exist
   * or parsing fails.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @return int value
   */
  public int getInt(String field) {
    String fieldValue = getField(field);

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
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @return double value
   */
  public double getDouble(String field) {
    String fieldValue = getField(field);

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
   * Returns boolean value for given field name. Throws {@link FactoryException} if field does not
   * exist, or field value is null or empty.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @return true if value is "1" or "true", false otherwise
   */
  public boolean getBoolean(String field) {
    String value = getField(field);

    if (value == null || value.trim().isEmpty())
      throw new FactoryException(String.format("Field \"%s\" is null or empty", field));

    return value.trim().equals("1") || value.trim().equalsIgnoreCase("true");
  }

  /**
   * Parses and returns a UUID from field value of given field name. Throws {@link FactoryException}
   * if field does not exist or parsing fails.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @return UUID
   */
  public UUID getUUID(String field) {
    return getUUID(field, true);
  }

  /**
   * Method for parsing an uuid.
   *
   * @param field that contains an uuid
   * @param remove if true, the field will be removed
   * @return the uuid
   */
  private UUID getUUID(String field, boolean remove) {
    String fieldValue = getField(field, remove);

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
   * Parses and returns a quantity from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @param unit unit of Quantity
   * @return Quantity of given field with given unit
   * @param <Q> unit type parameter
   */
  public <Q extends Quantity<Q>> ComparableQuantity<Q> getQuantity(String field, Unit<Q> unit) {
    return Quantities.getQuantity(getDouble(field), unit);
  }

  /**
   * Parses and returns a quantity from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @param sourceUnit the unit that is used in the source
   * @param targetUnit the unit that should be returned
   * @return a quantity in with the target unit
   * @param <Q> unit type parameter
   */
  public <Q extends Quantity<Q>> ComparableQuantity<Q> getQuantity(
      String field, Unit<Q> sourceUnit, Unit<Q> targetUnit) {
    return Quantities.getQuantity(getDouble(field), sourceUnit).to(targetUnit);
  }

  /**
   * Returns field value for given field name, or empty Optional if field does not exist.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @param unit unit of Quantity
   * @return field value
   * @param <Q> unit type parameter
   */
  public <Q extends Quantity<Q>> Optional<ComparableQuantity<Q>> getQuantityOptional(
      String field, Unit<Q> unit) {
    return Optional.ofNullable(fieldsToAttributes.remove(field))
        .filter(str -> !str.isEmpty())
        .map(value -> Quantities.getQuantity(Double.parseDouble(value), unit));
  }

  /**
   * Returns field value for given field name, or empty Optional if field does not exist.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @param sourceUnit the unit that is used in the source
   * @param targetUnit the unit that should be returned
   * @return a quantity in with the target unit
   * @param <Q> unit type parameter
   */
  public <Q extends Quantity<Q>> Optional<ComparableQuantity<Q>> getQuantityOptional(
      String field, Unit<Q> sourceUnit, Unit<Q> targetUnit) {
    return Optional.ofNullable(fieldsToAttributes.remove(field))
        .filter(str -> !str.isEmpty())
        .map(value -> Quantities.getQuantity(Double.parseDouble(value), sourceUnit).to(targetUnit));
  }

  /**
   * Method for passing a field and using it to retrieve an entity.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @param entities a map: uuid to entities
   * @return the entity
   * @param <E> type of entity
   */
  public <E extends Entity> E getEntity(String field, Map<UUID, E> entities) {
    UUID uuid = getUUID(field);

    if (entities.containsKey(uuid)) {
      return entities.get(uuid);
    } else {
      throw new FactoryException(
          "Extracting UUID for field '"
              + field
              + "' failed. Caused by: Entity with uuid "
              + uuid
              + " was not provided.");
    }
  }

  /**
   * Method for passing a field and using it to retrieve an entity. If no entity is specified by the
   * source, the default entity is returned.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @param entities a map: uuid to entities
   * @param defaultValue the default entity
   * @return the entity
   * @param <E> type of entity
   */
  public <E extends Entity> E getEntity(String field, Map<UUID, E> entities, E defaultValue) {
    if (isFieldEmpty(field)) {
      fieldsToAttributes.remove(field);
      return defaultValue;
    }

    return entities.getOrDefault(getUUID(field), defaultValue);
  }

  /**
   * Method for passing a field and using it to retrieve a type entity.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param entities a map: uuid to types
   * @return the entity
   * @param <E> type of entity
   */
  public <E extends AssetTypeInput> E getType(Map<UUID, E> entities) {
    return getEntity(FieldNamingStrategy.TYPE, entities);
  }

  /**
   * Parses and returns a geometry from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @return Geometry if field value is not empty, empty Optional otherwise
   */
  private Optional<Geometry> getGeometry(String field) {
    String value = getField(field);

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
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @return LineString if field value is not empty, empty Optional otherwise
   */
  public Optional<LineString> getLineString(String field) {
    Optional<Geometry> geom = getGeometry(field);
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
   * <p>Note: This method removes the field from the map.
   *
   * @param field field name
   * @return Point if field value is not empty, empty Optional otherwise
   */
  public Optional<Point> getPoint(String field) {
    Optional<Geometry> geom = getGeometry(field);
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
   * <p>Note: This method removes the fields from the map.
   *
   * @param voltLvlField name of the field containing the voltage level
   * @param ratedVoltField name of the field containing the rated voltage
   * @return Voltage level
   */
  public VoltageLevel getVoltageLvl(String voltLvlField, String ratedVoltField) {
    try {
      final String voltLvlId = getField(voltLvlField);
      final ComparableQuantity<ElectricPotential> vRated = getQuantity(ratedVoltField, KILOVOLT);

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
  private VoltageLevel parseToGermanVoltLvlOrIndividual(
      String voltLvlId, ComparableQuantity<ElectricPotential> vRated) {
    try {
      return GermanVoltageLevelUtils.parse(voltLvlId.toLowerCase(), vRated);
    } catch (VoltageLevelException e) {
      logger.warn(
          "Cannot parse ({}, {}) to common German voltage level. Build an individual one.",
          voltLvlId,
          vRated);
      return new VoltageLevel(voltLvlId, vRated);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FieldMap that = (FieldMap) o;
    return fieldsToAttributes.equals(that.fieldsToAttributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldsToAttributes);
  }

  @Override
  public String toString() {
    return "FieldMap{fieldsToAttributes=" + fieldsToAttributes + "}";
  }
}
