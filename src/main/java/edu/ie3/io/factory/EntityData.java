/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import java.util.Map;

/**
 * Internal API Contains data that is needed by an {@link EntityFactory} to generate an entity
 *
 * @version 0.1
 * @since 28.01.20
 */
abstract class EntityData {

  private final Map<String, String> fieldsToAttributes;

  public EntityData(Map<String, String> fieldsToAttributes) {
    this.fieldsToAttributes = fieldsToAttributes;
  }

  public Map<String, String> getFieldsToAttributes() {
    return fieldsToAttributes;
  }
}
