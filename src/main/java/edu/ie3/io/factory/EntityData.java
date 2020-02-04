/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

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

  public String get(String field) {
    return fieldsToAttributes.get(field);
  }

  public UUID getUUID(String field) {
    return UUID.fromString(get(field));
  }

  public <Q extends Quantity<Q>> ComparableQuantity<Q> get(String field, Unit<Q> unit) {
    return Quantities.getQuantity(Double.parseDouble(get(field)), unit);
  }

  public Class<? extends UniqueEntity> getEntityClass() {
    return entityClass;
  }
}
