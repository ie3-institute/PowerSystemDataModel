/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import edu.ie3.datamodel.exceptions.FactoryException;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.Unit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public abstract class FactoryData {
  private final Map<String, String> fieldsToAttributes;
  private final Class<?> targetClass;

  protected FactoryData(Map<String, String> fieldsToAttributes, Class<?> targetClass) {
    // this does the magic: case-insensitive get/set calls on keys
    this.fieldsToAttributes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    this.fieldsToAttributes.putAll(fieldsToAttributes);
    this.targetClass = targetClass;
  }

  public Map<String, String> getFieldsToValues() {
    return fieldsToAttributes;
  }

  public Class<?> getTargetClass() {
    return targetClass;
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
    return Optional.ofNullable(fieldsToAttributes.get(field));
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
   * Parses and returns an array of UUIDs from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param field field name
   * @return UUID
   */
  public UUID[] getUUIDs(String field) {
    try {
      String fieldValue = getField(field);
      if (fieldValue.trim().isEmpty()) return new UUID[0];

      String[] uuidFields = fieldValue.split(" ");
      return Arrays.stream(uuidFields).map(UUID::fromString).toArray(UUID[]::new);
    } catch (IllegalArgumentException iae) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse UUIDs of field \"%s\" with value \"%s\"",
              field, getField(field)),
          iae);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FactoryData that)) return false;
    return fieldsToAttributes.equals(that.fieldsToAttributes)
        && targetClass.equals(that.targetClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldsToAttributes, targetClass);
  }

  @Override
  public String toString() {
    return "FactoryData{"
        + "fieldsToAttributes="
        + fieldsToAttributes
        + ", targetClass="
        + targetClass
        + '}';
  }
}
