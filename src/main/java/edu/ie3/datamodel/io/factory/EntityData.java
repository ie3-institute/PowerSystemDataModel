/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.VoltageLevelException;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.*;
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

/**
 * Internal API Contains data that is needed by an {@link EntityFactory} to generate an entity
 *
 * @version 0.1
 * @since 28.01.20
 */
public abstract class EntityData {
  private static final Logger logger = LoggerFactory.getLogger(EntityData.class);
  private static final GeoJsonReader geoJsonReader = new GeoJsonReader();

  private final Map<String, String> fieldsToAttributes;
  private final Class<? extends UniqueEntity> entityClass;

  /**
   * Creates a new EntityData object
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   */
  public EntityData(
      Map<String, String> fieldsToAttributes, Class<? extends UniqueEntity> entityClass) {
    // this does the magic: case-insensitive get/set calls on keys
    TreeMap<String, String> insensitiveFieldsToAttributes =
        new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    insensitiveFieldsToAttributes.putAll(fieldsToAttributes);
    this.fieldsToAttributes = insensitiveFieldsToAttributes;
    this.entityClass = entityClass;
  }

  public Map<String, String> getFieldsToValues() {
    return fieldsToAttributes;
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
   * Returns field value for given field name. Throws {@link FactoryException} if field does not
   * exist.
   *
   * @param field field name
   * @return field value
   */
  public String getField(String field) {
    if (!fieldsToAttributes.containsKey(field))
      throw new FactoryException(String.format("Field \"%s\" not found in EntityData", field));

    return fieldsToAttributes.get(field);
  }

  /**
   * Returns field value for given field name, or empty Optional if field does not exist.
   *
   * @param field field name
   * @return field value
   */
  public Optional<String> getFieldOptional(String field) {
    if (!fieldsToAttributes.containsKey(field)) return Optional.empty();

    return Optional.of(fieldsToAttributes.get(field));
  }

  /**
   * Returns boolean value for given field name. Throws {@link FactoryException} if field does not
   * exist, or field value is null or empty.
   *
   * @param field field name
   * @return true if value is "1" or "true", false otherwise
   */
  public boolean getBoolean(String field) {
    final String value = getField(field);

    if (value == null || value.trim().isEmpty())
      throw new FactoryException(String.format("Field \"%s\" is null or empty", field));

    return value.trim().equals("1") || value.trim().equalsIgnoreCase("true");
  }

  /**
   * Returns int value for given field name. Throws {@link FactoryException} if field does not exist
   * or parsing fails.
   *
   * @param field field name
   * @return int value
   */
  public int getInt(String field) {
    try {
      return Integer.parseInt(getField(field));
    } catch (NumberFormatException nfe) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse field \"%s\" with supposed int value \"%s\"",
              field, getField(field)),
          nfe);
    }
  }

  /**
   * Returns double value for given field name. Throws {@link FactoryException} if field does not
   * exist or parsing fails.
   *
   * @param field field name
   * @return double value
   */
  public double getDouble(String field) {
    try {
      return Double.parseDouble(getField(field));
    } catch (NumberFormatException nfe) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse field \"%s\" with supposed double value \"%s\"",
              field, getField(field)),
          nfe);
    }
  }

  /**
   * Parses and returns a UUID from field value of given field name. Throws {@link FactoryException}
   * if field does not exist or parsing fails.
   *
   * @param field field name
   * @return UUID
   */
  public UUID getUUID(String field) {
    try {
      return UUID.fromString(getField(field));
    } catch (IllegalArgumentException iae) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse UUID of field \"%s\" with value \"%s\"",
              field, getField(field)),
          iae);
    }
  }

  /**
   * Parses and returns a geometry from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
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
   * @param field field name
   * @return LineString if field value is not empty, empty Optional otherwise
   */
  public Optional<LineString> getLineString(String field) {
    Optional<Geometry> geom = getGeometry(field);
    if (geom.isPresent()) {
      if (geom.get() instanceof LineString) return Optional.of((LineString) geom.get());
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
   * @param field field name
   * @return Point if field value is not empty, empty Optional otherwise
   */
  public Optional<Point> getPoint(String field) {
    Optional<Geometry> geom = getGeometry(field);
    if (geom.isPresent()) {
      if (geom.get() instanceof Point) return Optional.of((Point) geom.get());
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
   * Parses and returns a Quantity from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param field field name
   * @param unit unit of Quantity
   * @param <Q> unit type parameter
   * @return Quantity of given field with given unit
   */
  public <Q extends Quantity<Q>> ComparableQuantity<Q> getQuantity(String field, Unit<Q> unit) {
    return Quantities.getQuantity(getDouble(field), unit);
  }

  public Class<? extends UniqueEntity> getEntityClass() {
    return entityClass;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EntityData that = (EntityData) o;
    return fieldsToAttributes.equals(that.fieldsToAttributes)
        && entityClass.equals(that.entityClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldsToAttributes, entityClass);
  }

  @Override
  public String toString() {
    return "EntityData{"
        + "fieldsToAttributes="
        + fieldsToAttributes
        + ", entityClass="
        + entityClass
        + '}';
  }
}
