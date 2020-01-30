/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.models.UniqueEntity;
import java.util.Map;
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
    this.fieldsToAttributes = fieldsToAttributes;
    this.entityClass = entityClass;
  }

  public Map<String, String> getFieldsToValues() {
    return fieldsToAttributes;
  }

  public boolean containsKey(String key) {
    return fieldsToAttributes.containsKey(key);
  }

  public UUID getUUID(String field) {
    return UUID.fromString(field);
  }

  public String get(String field) {
    return fieldsToAttributes.get(field);
  }

  public <Q extends Quantity<Q>> ComparableQuantity<Q> get(String field, Unit<Q> unit) {
    final String value = fieldsToAttributes.get(field);
    return Quantities.getQuantity(Double.parseDouble(value), unit);
  }

  public Class<? extends UniqueEntity> getEntityClass() {
    return entityClass;
  }
}
