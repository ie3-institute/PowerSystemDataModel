/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.models.UniqueEntity;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.Unit;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

/**
 * Internal API Contains data that is needed by an {@link EntityFactory} to generate an entity
 *
 * @version 0.1
 * @since 28.01.20
 */
abstract class EntityData {

  private final Map<String, String> fieldsToAttributes;
  private final Class<? extends UniqueEntity> entityClass;

  public EntityData(
      Map<String, String> fieldsToAttributes, Class<? extends UniqueEntity> entityClass) {
    // this does the magic: case-insensitive get/set calls on keys
    TreeMap<String, String> insensitiveFieldsToAttribtues =
        new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    insensitiveFieldsToAttribtues.putAll(fieldsToAttributes);
    this.fieldsToAttributes = insensitiveFieldsToAttribtues;
    this.entityClass = entityClass;
  }

  public Map<String, String> getFieldsToValues() {
    return fieldsToAttributes;
  }

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
   * Parses and returns a Quantity from field value of given field name. Throws {@link
   * FactoryException} if field does not exist or parsing fails.
   *
   * @param field field name
   * @param unit unit of Quantity
   * @param <Q> unit type parameter
   * @return Quantity of given field with given unit
   */
  public <Q extends Quantity<Q>> ComparableQuantity<Q> getQuantity(String field, Unit<Q> unit) {
    try {
      return Quantities.getQuantity(Double.parseDouble(getField(field)), unit);
    } catch (NumberFormatException nfe) {
      throw new FactoryException(
          String.format(
              "Exception while trying to parse field \"%s\" with supposed double value \"%s\"",
              field, getField(field)),
          nfe);
    }
  }

  public Class<? extends UniqueEntity> getEntityClass() {
    return entityClass;
  }
}
