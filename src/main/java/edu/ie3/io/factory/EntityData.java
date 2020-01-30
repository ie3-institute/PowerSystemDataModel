/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.models.UniqueEntity;

import java.util.Map;
import java.util.TreeMap;

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

  public Class<? extends UniqueEntity> getEntityClass() {
    return entityClass;
  }
}
